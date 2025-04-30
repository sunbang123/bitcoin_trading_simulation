import React, { useEffect, useState } from 'react';
import CandleChart from './CandleChart';
import axios from 'axios';
import { CandlestickData } from 'lightweight-charts';

const ChartArea: React.FC = () => {
  const [candleData, setCandleData] = useState<CandlestickData[]>([]);
  const [latest, setLatest] = useState<{
    time: string;
    open: number;
    high: number;
    low: number;
    close: number;
  } | null>(null);

  useEffect(() => {
    const fetchCandles = async () => {
      const res = await axios.get('https://api.upbit.com/v1/candles/minutes/1', {
        params: { market: 'KRW-BTC', count: 100 },
      });

      const formatted: CandlestickData[] = res.data
        .map((item: any) => ({
          time: Math.floor(new Date(item.candle_date_time_utc).getTime() / 1000),
          open: item.opening_price,
          high: item.high_price,
          low: item.low_price,
          close: item.trade_price,
        }))
        .reverse();

      setCandleData(formatted);

      const latestItem = res.data[0];
      setLatest({
        time: new Date(latestItem.candle_date_time_kst).toLocaleString(),
        open: latestItem.opening_price,
        high: latestItem.high_price,
        low: latestItem.low_price,
        close: latestItem.trade_price,
      });
    };

    fetchCandles();
  }, []);

  return (
    <div className="w-full space-y-6">
      <h2 className="text-2xl font-bold text-center text-gray-800">Upbit KRW-BTC 1분봉 차트</h2>

      {latest && (
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 justify-center text-center">
          <div className="bg-gray-100 rounded-md shadow p-3">
            <p className="text-sm text-gray-500">현재가</p>
            <p className="text-lg font-bold text-blue-600">{latest.close.toLocaleString()} ₩</p>
          </div>
          <div className="bg-gray-100 rounded-md shadow p-3">
            <p className="text-sm text-gray-500">고가</p>
            <p className="text-lg font-bold text-red-600">{latest.high.toLocaleString()} ₩</p>
          </div>
          <div className="bg-gray-100 rounded-md shadow p-3">
            <p className="text-sm text-gray-500">저가</p>
            <p className="text-lg font-bold text-green-600">{latest.low.toLocaleString()} ₩</p>
          </div>
          <div className="bg-gray-100 rounded-md shadow p-3">
            <p className="text-sm text-gray-500">시간</p>
            <p className="text-base text-gray-700">{latest.time}</p>
          </div>
        </div>
      )}

      <div className="bg-white shadow-lg rounded-md border p-2">
        <CandleChart data={candleData} />
      </div>
    </div>
  );
};

export default ChartArea;
