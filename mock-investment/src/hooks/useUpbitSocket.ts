import { useEffect, useRef } from 'react';

type UpbitMessageType = 'trade' | 'ticker' | 'orderbook';

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

type UpbitSocketCallback = (data: UpbitSocketMessage) => void;

interface UseUpbitSocketOptions {
  type?: UpbitMessageType;
  codes?: string[];
  throttleMs?: number;
  reconnectDelayMs?: number;
  maxReconnectDelayMs?: number;
}

const DEFAULT_CODES = ['KRW-BTC'];
const DEFAULT_THROTTLE_MS = 250;
const DEFAULT_RECONNECT_DELAY_MS = 1000;
const DEFAULT_MAX_RECONNECT_DELAY_MS = 10000;
const UPBIT_WS_URL = 'wss://api.upbit.com/websocket/v1';

function getMessageKey(data: UpbitSocketMessage) {
  const code = data?.code ?? 'UNKNOWN';
  const type = data?.type ?? 'UNKNOWN';
  const timestamp = data?.trade_timestamp ?? data?.timestamp ?? data?.timestamp_ms;
  const sequentialId = data?.sequential_id ?? data?.trade_time_utc ?? data?.trade_time;
  const price = data?.trade_price ?? data?.opening_price ?? '';

  if (timestamp || sequentialId) {
    return `${type}:${code}:${timestamp ?? ''}:${sequentialId ?? ''}:${price}`;
  }

  return JSON.stringify(data);
}

export function useUpbitSocket(
  onMessage: UpbitSocketCallback,
  options: UseUpbitSocketOptions = {}
) {
  const {
    type = 'trade',
    codes = DEFAULT_CODES,
    throttleMs = DEFAULT_THROTTLE_MS,
    reconnectDelayMs = DEFAULT_RECONNECT_DELAY_MS,
    maxReconnectDelayMs = DEFAULT_MAX_RECONNECT_DELAY_MS,
  } = options;

  const socketRef = useRef<WebSocket | null>(null);
  const callbackRef = useRef(onMessage);
  const reconnectTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const reconnectAttemptRef = useRef(0);
  const manuallyClosedRef = useRef(false);
  const lastMessageKeyRef = useRef<string | null>(null);
  const lastEmittedAtRef = useRef(0);
  const pendingMessageRef = useRef<UpbitSocketMessage | null>(null);
  const throttleTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);

  useEffect(() => {
    callbackRef.current = onMessage;
  }, [onMessage]);

  useEffect(() => {
    const subscriptionCodes = codes.length > 0 ? codes : DEFAULT_CODES;

    const clearReconnectTimer = () => {
      if (reconnectTimerRef.current) {
        clearTimeout(reconnectTimerRef.current);
        reconnectTimerRef.current = null;
      }
    };

    const clearThrottleTimer = () => {
      if (throttleTimerRef.current) {
        clearTimeout(throttleTimerRef.current);
        throttleTimerRef.current = null;
      }
    };

    const emitMessage = (data: UpbitSocketMessage) => {
      callbackRef.current(data);
      lastEmittedAtRef.current = Date.now();
    };

    const emitWithThrottle = (data: UpbitSocketMessage) => {
      if (throttleMs <= 0) {
        emitMessage(data);
        return;
      }

      const now = Date.now();
      const elapsed = now - lastEmittedAtRef.current;

      if (elapsed >= throttleMs) {
        clearThrottleTimer();
        pendingMessageRef.current = null;
        emitMessage(data);
        return;
      }

      pendingMessageRef.current = data;

      if (!throttleTimerRef.current) {
        throttleTimerRef.current = setTimeout(() => {
          throttleTimerRef.current = null;

          if (pendingMessageRef.current) {
            const pending = pendingMessageRef.current;
            pendingMessageRef.current = null;
            emitMessage(pending);
          }
        }, throttleMs - elapsed);
      }
    };

    const scheduleReconnect = () => {
      if (manuallyClosedRef.current) return;

      const delay = Math.min(
        reconnectDelayMs * 2 ** reconnectAttemptRef.current,
        maxReconnectDelayMs
      );

      reconnectAttemptRef.current += 1;
      clearReconnectTimer();

      reconnectTimerRef.current = setTimeout(() => {
        connect();
      }, delay);
    };

    const connect = () => {
      clearReconnectTimer();

      if (
        socketRef.current &&
        (socketRef.current.readyState === WebSocket.OPEN ||
          socketRef.current.readyState === WebSocket.CONNECTING)
      ) {
        return;
      }

      const socket = new WebSocket(UPBIT_WS_URL);
      socketRef.current = socket;
      socket.binaryType = 'arraybuffer';

      socket.onopen = () => {
        reconnectAttemptRef.current = 0;

        socket.send(
          JSON.stringify([
            { ticket: `upbit-${type}-${Date.now()}` },
            { type, codes: subscriptionCodes, isOnlyRealtime: true },
          ])
        );
      };

      socket.onmessage = (event) => {
        try {
          const text =
            typeof event.data === 'string'
              ? event.data
              : new TextDecoder('utf-8').decode(event.data);
          const data = JSON.parse(text) as UpbitSocketMessage;
          const messageKey = getMessageKey(data);

          if (messageKey === lastMessageKeyRef.current) {
            return;
          }

          lastMessageKeyRef.current = messageKey;
          emitWithThrottle(data);
        } catch (error) {
          console.error('Failed to parse Upbit WebSocket message:', error);
        }
      };

      socket.onerror = (error) => {
        console.error('Upbit WebSocket error:', error);
      };

      socket.onclose = () => {
        if (socketRef.current === socket) {
          socketRef.current = null;
        }

        scheduleReconnect();
      };
    };

    manuallyClosedRef.current = false;
    connect();

    return () => {
      manuallyClosedRef.current = true;
      clearReconnectTimer();
      clearThrottleTimer();
      pendingMessageRef.current = null;

      if (socketRef.current) {
        socketRef.current.close();
        socketRef.current = null;
      }
    };
  }, [
    type,
    codes,
    throttleMs,
    reconnectDelayMs,
    maxReconnectDelayMs,
  ]);
}
