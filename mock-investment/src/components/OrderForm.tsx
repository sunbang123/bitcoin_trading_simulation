import React, { useState } from 'react';

const OrderForm: React.FC = () => {
  const [isBuy, setIsBuy] = useState(true);
  const [price, setPrice] = useState('');
  const [amount, setAmount] = useState('');
  const [percentage, setPercentage] = useState(0);

  const handlePercentageClick = (value: number) => {
    setPercentage(value);
    // 실제 금액 연산은 생략 (잔고 필요)
  };

  return (
    <div className="bg-white rounded-md shadow border p-4">
      {/* 탭 */}
      <div className="flex space-x-2 mb-4">
        <button
          className={`flex-1 py-2 rounded text-sm font-semibold ${
            isBuy ? 'bg-blue-500 text-white' : 'bg-gray-200 text-gray-700'
          }`}
          onClick={() => setIsBuy(true)}
        >
          매수
        </button>
        <button
          className={`flex-1 py-2 rounded text-sm font-semibold ${
            !isBuy ? 'bg-red-500 text-white' : 'bg-gray-200 text-gray-700'
          }`}
          onClick={() => setIsBuy(false)}
        >
          매도
        </button>
      </div>

      {/* 입력창 */}
      <div className="space-y-3">
        <div>
          <label className="text-sm text-gray-600">주문가격</label>
          <input
            type="text"
            value={price}
            onChange={(e) => setPrice(e.target.value)}
            className="w-full mt-1 px-2 py-1 border rounded text-sm"
            placeholder="가격 입력"
          />
        </div>
        <div>
          <label className="text-sm text-gray-600">주문수량</label>
          <input
            type="text"
            value={amount}
            onChange={(e) => setAmount(e.target.value)}
            className="w-full mt-1 px-2 py-1 border rounded text-sm"
            placeholder="수량 입력"
          />
        </div>

        {/* 비율 버튼 */}
        <div className="flex justify-between">
          {[25, 50, 100].map((v) => (
            <button
              key={v}
              onClick={() => handlePercentageClick(v)}
              className="px-2 py-1 bg-gray-100 rounded text-sm hover:bg-gray-200"
            >
              {v}%
            </button>
          ))}
        </div>

        {/* 주문 버튼 */}
        <button
          className={`w-full py-2 rounded font-semibold text-sm ${
            isBuy ? 'bg-blue-600 text-white' : 'bg-red-600 text-white'
          }`}
        >
          {isBuy ? '매수 주문' : '매도 주문'}
        </button>
      </div>
    </div>
  );
};

export default OrderForm;
