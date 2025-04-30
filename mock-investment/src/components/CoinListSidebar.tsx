import React from 'react';

const mockCoins = [
  { name: 'BTC', price: 136400000, change: 1.25 },
  { name: 'ETH', price: 4200000, change: -0.8 },
  { name: 'XRP', price: 700, change: 2.4 },
];

const CoinListSidebar: React.FC = () => {
  return (
    <div className="bg-white rounded-md shadow border p-4">
      <h3 className="text-center font-semibold text-gray-700 mb-2">시세 목록</h3>
      <div className="divide-y text-sm">
        {mockCoins.map((coin, index) => (
          <div key={index} className="py-2 flex justify-between items-center">
            <span className="font-medium">{coin.name}</span>
            <div className="text-right">
              <p className="font-semibold">{coin.price.toLocaleString()} ₩</p>
              <p
                className={
                  coin.change > 0
                    ? 'text-green-600 text-xs'
                    : coin.change < 0
                    ? 'text-red-600 text-xs'
                    : 'text-gray-500 text-xs'
                }
              >
                {coin.change > 0 ? '+' : ''}
                {coin.change.toFixed(2)}%
              </p>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default CoinListSidebar;
