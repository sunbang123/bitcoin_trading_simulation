export type UpbitMessageType = 'trade' | 'ticker' | 'orderbook';

export interface UpbitSocketMessage {
  type?: UpbitMessageType;
  code?: string;
  trade_price?: number;
  trade_volume?: number;
  trade_time?: string;
  trade_time_utc?: string;
  trade_timestamp?: number;
  timestamp?: number;
  timestamp_ms?: number;
  sequential_id?: number | string;
  signed_change_rate?: number;
  ask_bid?: 'ASK' | 'BID';
  best_ask_price?: number;
  best_ask_size?: number;
  best_bid_price?: number;
  best_bid_size?: number;
  opening_price?: number;
}

type SubscriptionCallback = (data: UpbitSocketMessage) => void;

interface Subscription {
  type: UpbitMessageType;
  codes: string[];
  callback: SubscriptionCallback;
}

const UPBIT_WS_URL = 'wss://api.upbit.com/websocket/v1';
const CONNECT_DEBOUNCE_MS = 250;
const DISCONNECT_GRACE_MS = 1500;
const MIN_REQUEST_INTERVAL_MS = 11000;
const MAX_RECONNECT_DELAY_MS = 60000;

class UpbitSocketManager {
  private socket: WebSocket | null = null;
  private subscriptions = new Map<number, Subscription>();
  private nextSubscriptionId = 1;
  private connectTimer: ReturnType<typeof setTimeout> | null = null;
  private disconnectTimer: ReturnType<typeof setTimeout> | null = null;
  private subscriptionTimer: ReturnType<typeof setTimeout> | null = null;
  private reconnectAttempt = 0;
  private lastConnectionAttemptAt = 0;
  private lastSubscriptionSentAt = 0;
  private intentionallyClosing = false;

  subscribe(subscription: Subscription) {
    const id = this.nextSubscriptionId++;
    this.subscriptions.set(id, subscription);
    this.clearDisconnectTimer();

    if (
      this.socket?.readyState === WebSocket.OPEN ||
      this.socket?.readyState === WebSocket.CONNECTING
    ) {
      this.scheduleSubscriptionUpdate();
    } else {
      this.scheduleConnect(CONNECT_DEBOUNCE_MS);
    }

    return () => {
      this.subscriptions.delete(id);

      if (this.subscriptions.size === 0) {
        this.clearConnectTimer();
        this.clearSubscriptionTimer();
        this.scheduleDisconnect();
      } else {
        this.scheduleSubscriptionUpdate();
      }
    };
  }

  private scheduleConnect(requestedDelayMs: number) {
    if (this.subscriptions.size === 0 || this.connectTimer) return;

    const elapsed = Date.now() - this.lastConnectionAttemptAt;
    const rateLimitDelay = Math.max(0, MIN_REQUEST_INTERVAL_MS - elapsed);
    const delay = Math.max(requestedDelayMs, rateLimitDelay);

    this.connectTimer = setTimeout(() => {
      this.connectTimer = null;
      this.connect();
    }, delay);
  }

  private connect() {
    if (
      this.subscriptions.size === 0 ||
      this.socket?.readyState === WebSocket.OPEN ||
      this.socket?.readyState === WebSocket.CONNECTING
    ) {
      return;
    }

    this.intentionallyClosing = false;
    this.lastConnectionAttemptAt = Date.now();

    const socket = new WebSocket(UPBIT_WS_URL);
    this.socket = socket;
    socket.binaryType = 'arraybuffer';

    socket.onopen = () => {
      if (this.socket !== socket) return;

      this.reconnectAttempt = 0;

      if (this.subscriptions.size === 0) {
        this.disconnect();
        return;
      }

      this.sendSubscription();
    };

    socket.onmessage = (event) => {
      try {
        const text =
          typeof event.data === 'string'
            ? event.data
            : new TextDecoder('utf-8').decode(event.data);
        const data = JSON.parse(text) as UpbitSocketMessage;

        for (const subscription of this.subscriptions.values()) {
          if (
            data.type === subscription.type &&
            (!data.code || subscription.codes.includes(data.code))
          ) {
            subscription.callback(data);
          }
        }
      } catch (error) {
        console.error('Failed to parse Upbit WebSocket message:', error);
      }
    };

    socket.onerror = () => {
      console.warn('Upbit WebSocket connection failed. Reconnect will be delayed.');
    };

    socket.onclose = () => {
      if (this.socket === socket) {
        this.socket = null;
      }

      if (!this.intentionallyClosing && this.subscriptions.size > 0) {
        this.scheduleReconnect();
      }
    };
  }

  private buildSubscriptionPayload() {
    const codesByType = new Map<UpbitMessageType, Set<string>>();

    for (const subscription of this.subscriptions.values()) {
      const codes = codesByType.get(subscription.type) ?? new Set<string>();
      subscription.codes.forEach((code) => codes.add(code));
      codesByType.set(subscription.type, codes);
    }

    return [
      { ticket: `bits-web-${Date.now()}` },
      ...Array.from(codesByType.entries()).map(([type, codes]) => ({
        type,
        codes: Array.from(codes),
        isOnlyRealtime: true,
      })),
    ];
  }

  private sendSubscription() {
    if (this.socket?.readyState !== WebSocket.OPEN || this.subscriptions.size === 0) {
      return;
    }

    this.clearSubscriptionTimer();
    this.socket.send(JSON.stringify(this.buildSubscriptionPayload()));
    this.lastSubscriptionSentAt = Date.now();
  }

  private scheduleSubscriptionUpdate() {
    if (this.socket?.readyState !== WebSocket.OPEN || this.subscriptionTimer) return;

    const elapsed = Date.now() - this.lastSubscriptionSentAt;
    const delay = Math.max(0, MIN_REQUEST_INTERVAL_MS - elapsed);

    this.subscriptionTimer = setTimeout(() => {
      this.subscriptionTimer = null;
      this.sendSubscription();
    }, delay);
  }

  private scheduleReconnect() {
    const exponentialDelay = Math.min(
      MIN_REQUEST_INTERVAL_MS * 2 ** this.reconnectAttempt,
      MAX_RECONNECT_DELAY_MS
    );
    const jitter = Math.floor(Math.random() * 1000);

    this.reconnectAttempt += 1;
    this.scheduleConnect(exponentialDelay + jitter);
  }

  private scheduleDisconnect() {
    if (this.disconnectTimer) return;

    this.disconnectTimer = setTimeout(() => {
      this.disconnectTimer = null;

      if (this.subscriptions.size === 0) {
        this.disconnect();
      }
    }, DISCONNECT_GRACE_MS);
  }

  private disconnect() {
    this.intentionallyClosing = true;
    this.clearConnectTimer();
    this.clearSubscriptionTimer();

    if (this.socket) {
      this.socket.close();
      this.socket = null;
    }
  }

  private clearConnectTimer() {
    if (this.connectTimer) {
      clearTimeout(this.connectTimer);
      this.connectTimer = null;
    }
  }

  private clearDisconnectTimer() {
    if (this.disconnectTimer) {
      clearTimeout(this.disconnectTimer);
      this.disconnectTimer = null;
    }
  }

  private clearSubscriptionTimer() {
    if (this.subscriptionTimer) {
      clearTimeout(this.subscriptionTimer);
      this.subscriptionTimer = null;
    }
  }
}

export const upbitSocketManager = new UpbitSocketManager();
