import { useEffect, useRef } from 'react';
import {
  upbitSocketManager,
  type UpbitMessageType,
  type UpbitSocketMessage,
} from '@/services/upbitSocketManager';

export type { UpbitMessageType, UpbitSocketMessage } from '@/services/upbitSocketManager';

type UpbitSocketCallback = (data: UpbitSocketMessage) => void;

interface UseUpbitSocketOptions {
  type?: UpbitMessageType;
  codes?: string[];
  throttleMs?: number;
}

const DEFAULT_CODES = ['KRW-BTC'];
const DEFAULT_THROTTLE_MS = 250;

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
  } = options;

  const callbackRef = useRef(onMessage);
  const lastMessageKeyRef = useRef<string | null>(null);
  const lastEmittedAtRef = useRef(0);
  const pendingMessageRef = useRef<UpbitSocketMessage | null>(null);
  const throttleTimerRef = useRef<ReturnType<typeof setTimeout> | null>(null);
  const codesKey = codes.join(',');

  useEffect(() => {
    callbackRef.current = onMessage;
  }, [onMessage]);

  useEffect(() => {
    const subscriptionCodes = codesKey
      ? codesKey.split(',').filter(Boolean)
      : DEFAULT_CODES;

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

    const handleMessage = (data: UpbitSocketMessage) => {
      const messageKey = getMessageKey(data);

      if (messageKey === lastMessageKeyRef.current) return;
      lastMessageKeyRef.current = messageKey;

      if (throttleMs <= 0) {
        emitMessage(data);
        return;
      }

      const elapsed = Date.now() - lastEmittedAtRef.current;

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

    const unsubscribe = upbitSocketManager.subscribe({
      type,
      codes: subscriptionCodes,
      callback: handleMessage,
    });

    return () => {
      unsubscribe();
      clearThrottleTimer();
      pendingMessageRef.current = null;
      lastMessageKeyRef.current = null;
    };
  }, [type, codesKey, throttleMs]);
}
