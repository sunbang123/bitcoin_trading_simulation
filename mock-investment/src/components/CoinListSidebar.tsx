import React, { useState, useCallback } from 'react';
import { UpbitSocketMessage, useUpbitSocket } from '../hooks/useUpbitSocket';

interface CoinInfo {
  code: string;
  price: number;
  change: number;
}

const trackedCoins = ['KRW-BTC', 'KRW-ETH', 'KRW-XRP'];

export default function CoinListSidebar() {
  const [coinList, setCoinList] = useState<CoinInfo[]>([]);

  const handleTicker = useCallback((data: UpbitSocketMessage) => {
    if (
      data.type !== 'ticker' ||
      !data.code ||
      data.trade_price === undefined ||
      data.signed_change_rate === undefined
    ) {
      return;
    }

    const code = data.code;
    const price = data.trade_price;
    const signedChangeRate = data.signed_change_rate;

    setCoinList((prevList) => {
      const changePercent = signedChangeRate * 100;
      const updatedCoin = {
        code,
        price,
        change: changePercent,
      };

      const existing = prevList.find((item) => item.code === code);

      if (!existing) {
        return [...prevList, updatedCoin];
      }

      return prevList.map((item) =>
        item.code === code ? updatedCoin : item
      );
    });
  }, []);

  useUpbitSocket(handleTicker, {
    type: 'ticker',
    codes: trackedCoins,
    throttleMs: 500,
  });

  return (
    <div className="bg-white rounded-md shadow border p-4">
      <h3 className="text-center font-semibold text-gray-700 mb-2">시세 목록</h3>
      <div className="divide-y text-sm">
        {coinList.map((coin) => {
          const symbol = coin.code.replace('KRW-', '');
          const changeClass =
            coin.change > 0
              ? 'text-green-600 text-xs'
              : coin.change < 0
              ? 'text-red-600 text-xs'
              : 'text-gray-500 text-xs';

          return (
            <div key={coin.code} className="py-2 flex justify-between items-center">
              <span className="font-medium">{symbol}</span>
              <div className="text-right">
                <p className="font-semibold">{coin.price.toLocaleString()} 원</p>
                <p className={changeClass}>
                  {coin.change > 0 ? '+' : ''}
                  {coin.change.toFixed(2)}%
                </p>
              </div>
            </div>
          );
        })}
      </div>
    </div>
  );
}
