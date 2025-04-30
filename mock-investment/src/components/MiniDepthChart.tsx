import React from 'react';

const mockDepth = {
  sell: [
    { price: 136400000, volume: 2 },
    { price: 136390000, volume: 1.5 },
    { price: 136380000, volume: 1.2 },
  ],
  buy: [
    { price: 136360000, volume: 2.5 },
    { price: 136350000, volume: 2.1 },
    { price: 136340000, volume: 1.6 },
  ],
};

const MiniDepthChart: React.FC = () => {
  return (
    <div className="bg-white rounded-md shadow border p-4">
      <h3 className="text-center text-sm font-semibold text-gray-700 mb-2">미니 차트</h3>

      {/* 매도 차트 (위쪽) */}
      <div className="space-y-1 mb-4">
        {mockDepth.sell.map((item, i) => (
          <div key={i} className="flex items-center text-red-500 text-xs">
            <div
              className="bg-red-200 h-4 mr-2"
              style={{ width: `${item.volume * 40}px` }}
            />
            <span>{item.price.toLocaleString()} ₩</span>
          </div>
        ))}
      </div>

      {/* 매수 차트 (아래쪽) */}
      <div className="space-y-1">
        {mockDepth.buy.map((item, i) => (
          <div key={i} className="flex items-center text-blue-500 text-xs">
            <div
              className="bg-blue-200 h-4 mr-2"
              style={{ width: `${item.volume * 40}px` }}
            />
            <span>{item.price.toLocaleString()} ₩</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default MiniDepthChart;
