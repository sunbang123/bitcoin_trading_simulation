import React from "react";

type TickerCardProps = {
  market?: string;       // 예: "KRW-BTC"
  symbol?: string;       // dashboard.tsx와 호환성을 위한 별칭
  tradePrice?: number;   // 현재가
  changeRate?: number;   // 전일 대비 변동률 (ex: 0.015 → 1.5%)
};

export default function TickerCard({ 
  market, 
  symbol,
  tradePrice = 0, 
  changeRate = 0 
}: TickerCardProps) {
  // symbol prop이 전달되면 market 대신 사용
  const actualMarket = market || symbol || "";
  const coin = actualMarket.replace("KRW-", "");
  const isUp = changeRate >= 0;

  // 실제 데이터가 없으면 API 호출 필요 (여기서는 생략)
  const needFetch = !tradePrice || (market === undefined && symbol !== undefined);
  
  // API 호출 필요한 경우 임시로 표시할 로딩 상태
  if (needFetch) {
    return (
      <div className="rounded-xl shadow-md p-4 w-60 bg-white dark:bg-neutral-900">
        <div className="text-lg font-semibold mb-2">{coin || '로딩 중...'}</div>
        <div className="text-2xl font-bold text-blue-600 dark:text-blue-400">
          로딩 중...
        </div>
        <div className="text-sm mt-1">
          데이터를 가져오는 중입니다...
        </div>
      </div>
    );
  }

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