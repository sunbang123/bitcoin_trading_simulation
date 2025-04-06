import { useEffect, useRef, useState } from 'react';
import { createChart, UTCTimestamp, Time } from 'lightweight-charts';
import axios from 'axios';

interface CandleChartProps {
  symbol: string;
  timeframe?: 'minutes' | 'days' | 'weeks' | 'months';
}

// lightweight-charts 호환 캔들 데이터 타입
interface ChartCandle {
  time: Time;
  open: number;
  high: number;
  low: number;
  close: number;
}

interface DebugInfo {
  apiUrl?: string;
  params?: Record<string, any>;
  responseStatus?: number;
  dataCount?: number;
  firstCandle?: any;
  lastCandle?: any;
  error?: string;
  responseData?: any;
  testApiCall?: boolean;
}

const CandleChart = ({ symbol, timeframe = 'days' }: CandleChartProps) => {
  const chartContainerRef = useRef<HTMLDivElement>(null);
  const chartInstanceRef = useRef<any>(null);
  const resizeObserverRef = useRef<ResizeObserver | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [debugInfo, setDebugInfo] = useState<any>(null);

  // 차트 생성 및 데이터 로딩을 처리하는 함수
  const initChart = async () => {
    // 이전 차트가 있으면 제거
    if (chartInstanceRef.current) {
      chartInstanceRef.current.remove();
      chartInstanceRef.current = null;
    }

    // ResizeObserver가 있으면 연결 해제
    if (resizeObserverRef.current) {
      resizeObserverRef.current.disconnect();
      resizeObserverRef.current = null;
    }

    // 디버그 정보 초기화
    setDebugInfo(null);
    
    // chartContainerRef가 없으면 리턴
    if (!chartContainerRef.current) {
      console.error('차트 컨테이너 ref가 없습니다.');
      setError('차트를 렌더링할 수 없습니다. 페이지를 새로고침해 주세요.');
      setIsLoading(false);
      return;
    }

    try {
      setIsLoading(true);
      setError(null);
      
      const chartElement = chartContainerRef.current;
      console.log('차트 컨테이너 크기:', chartElement.clientWidth, 'x', chartElement.clientHeight);
      
      // 차트 생성
      const chart = createChart(chartElement, {
        width: chartElement.clientWidth,
        height: 400,
        layout: {
          background: { color: '#1E1E1E' },
          textColor: '#D9D9D9',
        },
        grid: {
          vertLines: { color: '#2B2B43' },
          horzLines: { color: '#2B2B43' },
        },
      });
      
      // 차트 인스턴스 저장
      chartInstanceRef.current = chart;
      
      // 캔들 시리즈 추가
      const candleSeries = chart.addCandlestickSeries({
        upColor: '#26a69a',
        downColor: '#ef5350',
        borderVisible: false,
        wickUpColor: '#26a69a',
        wickDownColor: '#ef5350',
      });
      
      // API 요청 파라미터
      const params = {
        market: symbol,
        unit: timeframe,
        count: timeframe === 'weeks' ? 52 : timeframe === 'months' ? 24 : 90,
      };
      
      console.log('API 요청 파라미터:', params);
      
      // API에서 캔들 데이터 가져오기
      console.log('캔들 API 호출 시작:', `/api/upbit/candles`, params);
      let response;
      try {
        response = await axios.get(`/api/upbit/candles`, { params });
        console.log('캔들 API 응답 성공:', response.status, response.statusText);
        console.log('첫 번째 캔들 데이터:', response.data[0]);
        console.log('마지막 캔들 데이터:', response.data[response.data.length - 1]);
        
        setDebugInfo({
          apiUrl: '/api/upbit/candles',
          params: params,
          responseStatus: response.status,
          dataCount: response.data?.length || 0,
          firstCandle: response.data?.[0],
          lastCandle: response.data?.[response.data.length - 1]
        });
      } catch (apiError: any) {
        console.error('API 요청 실패 상세 정보:', {
          message: apiError.message,
          status: apiError.response?.status,
          data: apiError.response?.data,
          config: apiError.config
        });
        
        setDebugInfo({
          apiUrl: '/api/upbit/candles',
          params: params,
          error: apiError.message,
          responseStatus: apiError.response?.status,
          responseData: apiError.response?.data
        });
        
        throw new Error(`API 요청 실패: ${apiError.message}`);
      }
      
      // 응답 유효성 검사
      if (!Array.isArray(response.data) || response.data.length === 0) {
        setDebugInfo((prev: DebugInfo | null) => ({
          ...prev,
          error: '유효하지 않은 데이터 형식 또는 빈 데이터'
        }));
        throw new Error('유효하지 않은 데이터 형식이거나 데이터가 없습니다');
      }
      
      // 데이터 변환
      const candleData = response.data.map((item: any) => {
        const timestamp = Math.floor(new Date(item.candle_date_time_utc).getTime() / 1000);
        return {
          time: timestamp as UTCTimestamp,
          open: item.opening_price,
          high: item.high_price,
          low: item.low_price,
          close: item.trade_price,
        };
      });
      
      console.log('변환된 차트 데이터 수:', candleData.length);
      
      // 차트에 데이터 설정
      candleSeries.setData(candleData);
      
      // 차트 시간 범위 맞추기
      chart.timeScale().fitContent();
      
      // ResizeObserver를 사용하여 차트 컨테이너 크기 변화 감지 및 차트 리사이징
      resizeObserverRef.current = new ResizeObserver(() => {
        if (chartElement && chartInstanceRef.current) {
          chartInstanceRef.current.applyOptions({ 
            width: chartElement.clientWidth 
          });
        }
      });
      
      resizeObserverRef.current.observe(chartElement);
      
    } catch (err: any) {
      console.error('차트 생성 오류:', err);
      setError(`차트 생성 실패: ${err.message}`);
    } finally {
      setIsLoading(false);
    }
  };

  // 컴포넌트 마운트/언마운트 및 props 변경 시 처리
  useEffect(() => {
    console.log('차트 초기화 시작, 심볼:', symbol, '타임프레임:', timeframe);
    
    // 차트 초기화
    initChart();
    
    // 클린업 함수
    return () => {
      if (chartInstanceRef.current) {
        chartInstanceRef.current.remove();
        chartInstanceRef.current = null;
      }
      
      if (resizeObserverRef.current) {
        resizeObserverRef.current.disconnect();
        resizeObserverRef.current = null;
      }
    };
  }, [symbol, timeframe]);
  
  // API 직접 호출 테스트 함수 (디버깅용)
  const testApiCall = async () => {
    try {
      setIsLoading(true);
      setError(null);
      console.log('API 직접 호출 테스트 시작...');
      
      const params = {
        market: symbol,
        unit: timeframe,
        count: 10 // 적은 수의 데이터로 테스트
      };
      
      const response = await axios.get(`/api/upbit/candles`, { params });
      console.log('API 테스트 성공:', response.status, response.data);
      
      setDebugInfo({
        testApiCall: true,
        apiUrl: '/api/upbit/candles',
        params: params,
        responseStatus: response.status,
        dataCount: response.data?.length || 0,
        data: response.data
      });
      
      setError(null);
    } catch (err: any) {
      console.error('API 테스트 실패:', err);
      setError(`API 테스트 실패: ${err.message}`);
      
      setDebugInfo({
        testApiCall: true,
        apiUrl: '/api/upbit/candles',
        params: {
          market: symbol,
          unit: timeframe,
          count: 10
        },
        error: err.message,
        responseStatus: err.response?.status,
        responseData: err.response?.data
      });
    } finally {
      setIsLoading(false);
    }
  };
  
  // 로딩 상태 UI
  if (isLoading) {
    return (
      <div className="h-96 flex items-center justify-center">
        <div className="flex flex-col items-center">
          <div className="w-10 h-10 border-4 border-t-blue-500 border-r-transparent border-b-blue-500 border-l-transparent rounded-full animate-spin mb-2"></div>
          <p>차트 로딩 중...</p>
        </div>
      </div>
    );
  }
  
  // 에러 상태 UI
  if (error) {
    return (
      <div className="h-96 flex items-center justify-center text-red-500">
        <div className="flex flex-col items-center">
          <svg xmlns="http://www.w3.org/2000/svg" className="h-10 w-10 mb-2" fill="none" viewBox="0 0 24 24" stroke="currentColor">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 8v4m0 4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z" />
          </svg>
          <p className="text-center mb-3">{error}</p>
          <div className="flex gap-2">
            <button 
              className="px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600"
              onClick={() => initChart()}
            >
              다시 시도
            </button>
            <button 
              className="px-4 py-2 bg-gray-600 text-white rounded hover:bg-gray-700"
              onClick={testApiCall}
            >
              API 테스트
            </button>
          </div>
          
          {/* 디버그 정보 표시 */}
          {debugInfo && (
            <div className="mt-4 p-3 bg-gray-800 rounded text-xs text-left w-full max-w-lg overflow-auto">
              <h4 className="font-bold mb-1">디버그 정보:</h4>
              <pre className="whitespace-pre-wrap">
                {JSON.stringify(debugInfo, null, 2)}
              </pre>
            </div>
          )}
        </div>
      </div>
    );
  }
  
  // 차트 컨테이너
  return (
    <div className="w-full">
      <div 
        ref={chartContainerRef} 
        className="w-full h-96 rounded border border-gray-700"
        style={{ minHeight: '400px' }}
      />
      
      {/* 디버그 버튼 */}
      <div className="mt-2 flex justify-end">
        <button 
          className="text-xs px-2 py-1 bg-gray-700 text-gray-300 rounded hover:bg-gray-600"
          onClick={testApiCall}
        >
          API 테스트
        </button>
      </div>
      
      {/* 디버그 정보 표시 (숨겨진 상태에서 토글 가능) */}
      {debugInfo && (
        <div className="mt-2 p-2 bg-gray-800 rounded text-xs overflow-auto" style={{ maxHeight: '200px' }}>
          <pre className="whitespace-pre-wrap text-gray-300">
            {JSON.stringify(debugInfo, null, 2)}
          </pre>
        </div>
      )}
    </div>
  );
};

export default CandleChart;