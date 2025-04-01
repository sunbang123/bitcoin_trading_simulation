import { useEffect, useState } from "react";
import TickerCard from "@/components/TickerCard"; // 📦 시세 카드 컴포넌트
import { Geist, Geist_Mono } from "next/font/google"; // 🖋️ 구글 폰트 로딩

// Geist Sans 폰트 불러오기 (일반 텍스트용)
const geistSans = Geist({
  variable: "--font-geist-sans",
  subsets: ["latin"],
});

// Geist Mono 폰트 불러오기 (코드/숫자용)
const geistMono = Geist_Mono({
  variable: "--font-geist-mono",
  subsets: ["latin"],
});

export default function Home() {
  // API에서 받아올 시세 정보 저장
  const [ticker, setTicker] = useState<any>(null);

  // 페이지 로드 시 API 호출 => 백엔드에서 구현한 ticker 호출
  useEffect(() => {
    fetch("https://bitcoin-trading-simulation.onrender.com/api/ticker?market=KRW-BTC")
        .then((res) => res.json())
        .then((data) => setTicker(data))
        .catch((err) => console.error("백엔드 fetch 실패:", err));
  }, []);

  return (
    // 폰트 변수 적용 + 화면 패딩
    <div
      className={`min-h-screen p-8 font-sans ${geistSans.variable} ${geistMono.variable}`}
    >
      <main className="flex items-center justify-center">
        {ticker ? (
          // ✅ 시세 데이터를 넘겨서 카드 컴포넌트 렌더링
          <TickerCard
            market={ticker.market}
            tradePrice={ticker.trade_price}
            changeRate={ticker.signed_change_rate}
          />
        ) : (
          // ⏳ 데이터 로딩 중 표시
          <p>로딩 중...</p>
        )}
      </main>
    </div>
  );
}
