import { useState } from 'react';
import Layout from '../components/Layout';
import CandleChart from '../components/CandleChart';
import TickerCard from '../components/TickerCard';

export default function Dashboard() {
  // 선택된 마켓 상태
  const [selectedMarket, setSelectedMarket] = useState('KRW-BTC');
  
  // 타임프레임 상태 추가
  const [selectedTimeframe, setSelectedTimeframe] = useState<'minutes' | 'days' | 'weeks' | 'months'>('days');
  
  // 마켓 옵션 목록
  const marketOptions = [
    { value: 'KRW-BTC', label: '비트코인 (BTC)' },
    { value: 'KRW-ETH', label: '이더리움 (ETH)' },
    { value: 'KRW-XRP', label: '리플 (XRP)' },
    { value: 'KRW-SOL', label: '솔라나 (SOL)' },
    { value: 'KRW-ADA', label: '카르다노 (ADA)' }
  ];
  
  // 타임프레임 옵션 목록
  const timeframeOptions = [
    { value: 'minutes', label: '1분봉' },
    { value: 'days', label: '일봉' },
    { value: 'weeks', label: '주봉' },
    { value: 'months', label: '월봉' }
  ];
  
  return (
    <Layout>
      <div className="container mx-auto p-4">
        <h1 className="text-2xl font-bold mb-4">암호화폐 대시보드</h1>
        
        {/* 티커 카드 그리드 */}
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <TickerCard symbol="KRW-BTC" />
          <TickerCard symbol="KRW-ETH" />
          <TickerCard symbol="KRW-XRP" />
        </div>
        
        {/* 차트 섹션 */}
        <div className="bg-gray-900 p-4 rounded-lg shadow-lg mb-6">
          {/* 차트 선택 옵션 */}
          <div className="flex flex-wrap gap-4 mb-4">
            {/* 마켓 선택 */}
            <div className="mb-2">
              <label className="block text-sm font-medium mb-1">마켓 선택</label>
              <select 
                value={selectedMarket}
                onChange={(e) => setSelectedMarket(e.target.value)}
                className="p-2 bg-gray-800 border border-gray-700 rounded text-white text-sm"
              >
                {marketOptions.map(option => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>
            
            {/* 타임프레임 선택 - 새로 추가 */}
            <div className="mb-2">
              <label className="block text-sm font-medium mb-1">타임프레임</label>
              <select 
                value={selectedTimeframe}
                onChange={(e) => setSelectedTimeframe(e.target.value as any)}
                className="p-2 bg-gray-800 border border-gray-700 rounded text-white text-sm"
              >
                {timeframeOptions.map(option => (
                  <option key={option.value} value={option.value}>
                    {option.label}
                  </option>
                ))}
              </select>
            </div>
          </div>
          
          {/* 캔들 차트 - 선택된 timeframe 전달 */}
          <CandleChart 
            symbol={selectedMarket} 
            timeframe={selectedTimeframe} 
          />
          
          {/* 타임프레임 선택 정보 표시 */}
          <div className="mt-2 text-sm text-gray-400">
            {selectedTimeframe === 'weeks' ? 
              '* 주봉 차트는 최근 52주 데이터를 표시합니다.' : 
              selectedTimeframe === 'months' ? 
              '* 월봉 차트는 최근 24개월 데이터를 표시합니다.' : 
              selectedTimeframe === 'minutes' ? 
              '* 1분봉 차트는 최근 200개 캔들을 표시합니다.' : 
              '* 일봉 차트는 최근 90일 데이터를 표시합니다.'
            }
          </div>
        </div>
        
        {/* 추가 정보 섹션 */}
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-4">
          <div className="bg-gray-900 p-4 rounded-lg shadow-lg">
            <h2 className="text-xl font-bold mb-2">최근 거래</h2>
            {/* 여기에 최근 거래 정보 컴포넌트 추가 */}
            <p className="text-gray-400">거래 정보 컴포넌트가 표시됩니다</p>
          </div>
          
          <div className="bg-gray-900 p-4 rounded-lg shadow-lg">
            <h2 className="text-xl font-bold mb-2">시장 개요</h2>
            {/* 여기에 시장 개요 컴포넌트 추가 */}
            <p className="text-gray-400">시장 개요 컴포넌트가 표시됩니다</p>
          </div>
        </div>
      </div>
    </Layout>
  );
}