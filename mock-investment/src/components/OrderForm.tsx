import React, { useState } from 'react';
import { orderService } from '@/services/orderService';

const OrderForm: React.FC = () => {
  const [isBuy, setIsBuy] = useState(true);
  const [price, setPrice] = useState('');  // 문자열 상태로 유지
  const [amount, setAmount] = useState(''); // 문자열 상태로 유지
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [message, setMessage] = useState('');

  // 주문 총액 계산
  const total = (() => {
    const p = parseFloat(price);
    const a = parseFloat(amount);
    return !isNaN(p) && !isNaN(a) ? p * a : 0;
  })();

  // 가격 +/- 버튼
  const handlePriceChange = (diff: number) => {
    const current = parseInt(price.replace(/[^0-9]/g, '')) || 0;
    const updated = Math.max(current + diff, 0);
    setPrice(updated.toString());
  };

  const handleSubmit = async () => {
    const p = parseFloat(price);
    const a = parseFloat(amount);
    if (isNaN(p) || isNaN(a) || p <= 0 || a <= 0) {
      alert('유효한 가격과 수량을 입력해주세요.');
      return;
    }

    try {
      setIsSubmitting(true);
      setMessage('');

      const response = await orderService.createOrder({
        coinSymbol: 'BTC',
        quantity: a,
        price: p,
        orderType: isBuy ? 'BUY' : 'SELL',
        orderMethod: 'LIMIT',
      });

      setMessage(`${response.coinSymbol} ${isBuy ? '매수' : '매도'} 주문이 접수되었습니다. 주문번호: ${response.orderId}`);
      setPrice('');
      setAmount('');
    } catch (error) {
      console.error(error);
      setMessage('주문 접수에 실패했습니다. 로그인 상태와 잔고를 확인해 주세요.');
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="bg-white rounded-md shadow border p-4 text-sm">
      {/* 탭 전환 */}
      <div className="flex mb-4 border-b">
        <button
          className={`flex-1 py-2 font-semibold ${isBuy ? 'text-blue-600 border-b-2 border-blue-600' : 'text-gray-500'}`}
          onClick={() => setIsBuy(true)}
        >
          매수
        </button>
        <button
          className={`flex-1 py-2 font-semibold ${!isBuy ? 'text-red-600 border-b-2 border-red-600' : 'text-gray-500'}`}
          onClick={() => setIsBuy(false)}
        >
          매도
        </button>
      </div>

      {/* 매수가격 */}
      <label className="block mb-1 text-gray-600">매수가격 (KRW)</label>
      <div className="flex items-center mb-3">
        <button onClick={() => handlePriceChange(-1000)} className="px-2 py-1 border rounded-l">-</button>
        <input
          type="text"
          value={price}
          onChange={(e) => setPrice(e.target.value.replace(/[^0-9]/g, ''))}
          className="w-full px-2 py-1 border-t border-b text-right"
          placeholder="가격 입력"
        />
        <button onClick={() => handlePriceChange(1000)} className="px-2 py-1 border rounded-r">+</button>
      </div>

      {/* 수량 입력 */}
      <label className="block mb-1 text-gray-600">주문수량 (BTC)</label>
      <input
        type="text"
        value={amount}
        onChange={(e) => setAmount(e.target.value.replace(/[^0-9.]/g, ''))}
        className="w-full px-2 py-1 border rounded mb-3 text-right"
        placeholder="수량 입력"
      />

      {/* 총액 출력 */}
      <label className="block mb-1 text-gray-600">주문총액 (KRW)</label>
      <input
        type="text"
        value={total.toLocaleString()}
        readOnly
        className="w-full px-2 py-1 border rounded mb-4 bg-gray-50 text-right text-gray-600"
      />

      {/* 주문 버튼 */}
      <button
        onClick={handleSubmit}
        disabled={isSubmitting}
        className={`w-full py-2 rounded font-semibold ${
          isBuy ? 'bg-blue-600 text-white' : 'bg-red-600 text-white'
        } ${isSubmitting ? 'opacity-60 cursor-not-allowed' : ''
        }`}
      >
        {isSubmitting ? '주문 접수 중...' : isBuy ? '매수 주문' : '매도 주문'}
      </button>

      {message && (
        <p className="mt-3 text-xs text-gray-600 leading-relaxed">{message}</p>
      )}
    </div>
  );
};

export default OrderForm;
