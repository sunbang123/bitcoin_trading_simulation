import React from 'react';

const mockSellOrders = [
  { price: 136400000, quantity: 0.3 },
  { price: 136390000, quantity: 0.5 },
  { price: 136380000, quantity: 0.4 },
];

const mockBuyOrders = [
  { price: 136360000, quantity: 0.6 },
  { price: 136350000, quantity: 0.9 },
  { price: 136340000, quantity: 1.2 },
];

const OrderBook: React.FC = () => {
  return (
    <div className="bg-white rounded-md shadow border p-4 text-sm">
      <h3 className="text-center font-semibold text-gray-700 mb-2">호가창</h3>

      {/* 매도 */}
      <div className="mb-2">
        {mockSellOrders.map((order, index) => (
          <div key={index} className="flex justify-between text-red-500">
            <span>{order.quantity.toFixed(2)}</span>
            <span>{order.price.toLocaleString()} ₩</span>
          </div>
        ))}
      </div>

      {/* 현재가 라인 */}
      <div className="flex justify-between font-bold py-1 border-t border-b text-blue-600">
        <span>1.00</span>
        <span>136,370,000 ₩</span>
      </div>

      {/* 매수 */}
      <div className="mt-2">
        {mockBuyOrders.map((order, index) => (
          <div key={index} className="flex justify-between text-green-600">
            <span>{order.quantity.toFixed(2)}</span>
            <span>{order.price.toLocaleString()} ₩</span>
          </div>
        ))}
      </div>
    </div>
  );
};

export default OrderBook;
