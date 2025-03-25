import React from "react";

type TickerCardProps = {
  market: string;        // 예: "KRW-BTC"
  tradePrice: number;    // 현재가
  changeRate: number;    // 전일 대비 변동률 (ex: 0.015 → 1.5%)
};

export default function TickerCard({ market, tradePrice, changeRate }: TickerCardProps) {
  const coin = market.replace("KRW-", "");
  const isUp = changeRate >= 0;

  return (
    <div className="rounded-xl shadow-md p-4 w-60 bg-white dark:bg-neutral-900">
      <div className="text-lg font-semibold mb-2">{coin}</div>
      <div className="text-2xl font-bold text-blue-600 dark:text-blue-400">
        {tradePrice.toLocaleString()} 원
      </div>
      <div className={`text-sm mt-1 ${isUp ? "text-green-600" : "text-red-500"}`}>
        {isUp ? "▲" : "▼"} {(changeRate * 100).toFixed(2)}%
      </div>
    </div>
  );
}
