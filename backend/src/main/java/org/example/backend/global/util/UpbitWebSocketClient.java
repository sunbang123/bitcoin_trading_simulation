package org.example.backend.global.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.backend.global.exception.requestError.order.PriceUnavailableException;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpbitWebSocketClient {

    private final ObjectMapper objectMapper;
    private final Map<String, BigDecimal> priceCache = new ConcurrentHashMap<>();
    private final Map<String, CompletableFuture<BigDecimal>> priceWaiters = new ConcurrentHashMap<>();
    private final Set<String> subscribedSymbols = ConcurrentHashMap.newKeySet();
    private final Object connectionLock = new Object();
    private final AtomicBoolean reconnectScheduled = new AtomicBoolean(false);
    private final AtomicBoolean subscriptionUpdateScheduled = new AtomicBoolean(false);
    private final AtomicBoolean shuttingDown = new AtomicBoolean(false);
    private final AtomicInteger reconnectAttempt = new AtomicInteger(0);
    private final ScheduledExecutorService connectionExecutor = Executors.newSingleThreadScheduledExecutor(
            daemonThreadFactory("upbit-websocket")
    );

    @Value("${upbit.websocket.url:wss://api.upbit.com/websocket/v1}")
    private String websocketUrl;

    @Value("${upbit.websocket.enabled:true}")
    private boolean enabled;

    @Value("${upbit.websocket.reconnect-initial-delay-ms:1000}")
    private long reconnectInitialDelayMs;

    @Value("${upbit.websocket.reconnect-max-delay-ms:10000}")
    private long reconnectMaxDelayMs;

    private volatile WebSocketClient webSocketClient;

    @PostConstruct
    public void init() {
        if (enabled) {
            scheduleConnect(0);
        }
    }

    @PreDestroy
    public void shutdown() {
        shuttingDown.set(true);
        connectionExecutor.shutdownNow();

        WebSocketClient client = webSocketClient;
        if (client != null) {
            client.close();
        }
    }

    public void subscribe(String coinSymbol) {
        String normalizedSymbol = normalizeSymbol(coinSymbol);
        boolean added = subscribedSymbols.add(normalizedSymbol);

        if (added && isConnected()) {
            scheduleSubscriptionUpdate();
        }
    }

    public BigDecimal getPrice(String coinSymbol) {
        return findPrice(coinSymbol).orElse(BigDecimal.ZERO);
    }

    public Optional<BigDecimal> findPrice(String coinSymbol) {
        return Optional.ofNullable(priceCache.get(normalizeSymbol(coinSymbol)));
    }

    public BigDecimal subscribeAndAwaitPrice(String coinSymbol, Duration timeout) {
        String normalizedSymbol = normalizeSymbol(coinSymbol);
        subscribe(normalizedSymbol);

        BigDecimal cachedPrice = priceCache.get(normalizedSymbol);
        if (isValidPrice(cachedPrice)) {
            return cachedPrice;
        }

        CompletableFuture<BigDecimal> waiter = priceWaiters.computeIfAbsent(
                normalizedSymbol,
                ignored -> new CompletableFuture<>()
        );

        cachedPrice = priceCache.get(normalizedSymbol);
        if (isValidPrice(cachedPrice)) {
            waiter.complete(cachedPrice);
        }

        try {
            return waiter.get(timeout.toMillis(), TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new PriceUnavailableException(normalizedSymbol);
        } catch (ExecutionException | TimeoutException e) {
            throw new PriceUnavailableException(normalizedSymbol);
        } finally {
            priceWaiters.remove(normalizedSymbol, waiter);
        }
    }

    private void scheduleConnect(long delayMs) {
        if (!enabled || shuttingDown.get() || !reconnectScheduled.compareAndSet(false, true)) {
            return;
        }

        connectionExecutor.schedule(() -> {
            reconnectScheduled.set(false);
            connectWebSocket();
        }, delayMs, TimeUnit.MILLISECONDS);
    }

    private void connectWebSocket() {
        if (shuttingDown.get() || isConnected()) {
            return;
        }

        synchronized (connectionLock) {
            if (shuttingDown.get() || isConnected()) {
                return;
            }

            try {
                WebSocketClient client = createClient(new URI(websocketUrl));
                webSocketClient = client;
                client.connect();
            } catch (Exception e) {
                log.error("Upbit WebSocket connection failed", e);
                scheduleReconnect();
            }
        }
    }

    private WebSocketClient createClient(URI uri) {
        return new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
                reconnectAttempt.set(0);
                log.info("Upbit WebSocket connected");
                sendSubscriptionSnapshot();
            }

            @Override
            public void onMessage(String message) {
                handlePriceMessage(message);
            }

            @Override
            public void onMessage(ByteBuffer bytes) {
                handlePriceMessage(StandardCharsets.UTF_8.decode(bytes).toString());
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                log.warn("Upbit WebSocket closed: code={}, reason={}", code, reason);
                clearClient(this);
                scheduleReconnect();
            }

            @Override
            public void onError(Exception ex) {
                log.error("Upbit WebSocket error", ex);
            }
        };
    }

    private void clearClient(WebSocketClient closedClient) {
        synchronized (connectionLock) {
            if (webSocketClient == closedClient) {
                webSocketClient = null;
            }
        }
    }

    private void scheduleReconnect() {
        if (shuttingDown.get()) {
            return;
        }

        int attempt = reconnectAttempt.getAndIncrement();
        long multiplier = 1L << Math.min(attempt, 20);
        long delay = Math.min(reconnectInitialDelayMs * multiplier, reconnectMaxDelayMs);
        scheduleConnect(delay);
    }

    private boolean isConnected() {
        WebSocketClient client = webSocketClient;
        return client != null && client.isOpen();
    }

    private void scheduleSubscriptionUpdate() {
        if (shuttingDown.get() || !subscriptionUpdateScheduled.compareAndSet(false, true)) {
            return;
        }

        connectionExecutor.schedule(() -> {
            subscriptionUpdateScheduled.set(false);
            sendSubscriptionSnapshot();
        }, 50, TimeUnit.MILLISECONDS);
    }

    private void sendSubscriptionSnapshot() {
        WebSocketClient client = webSocketClient;
        if (client == null || !client.isOpen() || subscribedSymbols.isEmpty()) {
            return;
        }

        try {
            ArrayNode payload = objectMapper.createArrayNode();

            ObjectNode ticketNode = objectMapper.createObjectNode();
            ticketNode.put("ticket", "price-subscription");
            payload.add(ticketNode);

            ObjectNode typeNode = objectMapper.createObjectNode();
            typeNode.put("type", "trade");
            ArrayNode codesNode = typeNode.putArray("codes");
            subscribedSymbols.stream()
                    .sorted(Comparator.naturalOrder())
                    .map(symbol -> "KRW-" + symbol)
                    .forEach(codesNode::add);
            typeNode.put("isOnlyRealtime", true);
            payload.add(typeNode);

            client.send(objectMapper.writeValueAsString(payload));
            log.info("Upbit subscription updated: {}", subscribedSymbols);
        } catch (Exception e) {
            log.error("Failed to send Upbit subscription", e);
        }
    }

    void handlePriceMessage(String message) {
        try {
            JsonNode json = objectMapper.readTree(message);
            JsonNode codeNode = json.get("code");
            JsonNode priceNode = json.get("trade_price");

            if (codeNode == null || priceNode == null || !priceNode.isNumber()) {
                log.debug("Ignoring unsupported Upbit message: {}", message);
                return;
            }

            String coinSymbol = normalizeSymbol(codeNode.asText());
            BigDecimal price = priceNode.decimalValue();
            if (!isValidPrice(price)) {
                return;
            }

            priceCache.put(coinSymbol, price);
            CompletableFuture<BigDecimal> waiter = priceWaiters.get(coinSymbol);
            if (waiter != null) {
                waiter.complete(price);
            }
        } catch (Exception e) {
            log.warn("Failed to parse Upbit price message", e);
        }
    }

    static String normalizeSymbol(String coinSymbol) {
        if (coinSymbol == null || coinSymbol.isBlank()) {
            throw new IllegalArgumentException("Coin symbol must not be blank");
        }

        String normalized = coinSymbol.trim().toUpperCase(Locale.ROOT);
        return normalized.startsWith("KRW-") ? normalized.substring(4) : normalized;
    }

    private static boolean isValidPrice(BigDecimal price) {
        return price != null && price.compareTo(BigDecimal.ZERO) > 0;
    }

    private static ThreadFactory daemonThreadFactory(String threadName) {
        return runnable -> {
            Thread thread = new Thread(runnable, threadName);
            thread.setDaemon(true);
            return thread;
        };
    }
}
