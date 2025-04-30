import React from 'react';

const AdvertisementBanner: React.FC = () => {
  return (
    <div className="bg-yellow-100 rounded-md shadow border p-4 text-center">
      <p className="text-sm text-yellow-800 font-semibold mb-2">📢 프로모션 안내</p>
      <p className="text-xs text-yellow-700">
        첫 거래 수수료 0%!<br />
        신규 가입 시 5,000원 쿠폰 지급 🎁
      </p>
    </div>
  );
};

export default AdvertisementBanner;
