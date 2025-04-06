import axios from 'axios';
import type { NextApiRequest, NextApiResponse } from 'next';

export default async function handler(req: NextApiRequest, res: NextApiResponse) {
  console.log('캔들 API 요청 받음:', req.method);
  console.log('쿼리 파라미터:', req.query);

  // GET 요청만 허용
  if (req.method !== 'GET') {
    console.log('잘못된 HTTP 메서드:', req.method);
    return res.status(405).json({ error: 'Method not allowed' });
  }

  try {
    const { market, unit, count } = req.query;

    // 필수 파라미터 확인
    if (!market) {
      console.log('필수 파라미터 누락: market');
      return res.status(400).json({ error: 'Market parameter is required' });
    }

    // 업비트 API 엔드포인트 결정
    let endpoint = '';
    const unitStr = String(unit || 'days').toLowerCase();
    
    switch (unitStr) {
      case 'minutes':
        endpoint = '/candles/minutes/1';
        break;
      case 'days':
        endpoint = '/candles/days';
        break;
      case 'weeks':
        endpoint = '/candles/weeks';
        break;
      case 'months':
        endpoint = '/candles/months';
        break;
      default:
        endpoint = '/candles/days';
    }
    
    console.log('선택된 엔드포인트:', endpoint);

    // 현재 시간 생성 (to 파라미터로 최신 데이터 가져오기 위함)
    const now = new Date();
    const formattedDate = now.toISOString();
    
    // 업비트 API 요청 파라미터
    const params: any = {
      market: market,
      count: count || (unitStr === 'weeks' ? 52 : unitStr === 'months' ? 24 : 90),
      to: formattedDate  // 현재 시간을 기준으로 데이터 요청
    };
    
    console.log('업비트 API 요청 파라미터:', params);
    console.log('업비트 API URL:', `https://api.upbit.com/v1${endpoint}`);

    // 업비트 API 호출
    console.log('업비트 API 호출 시작...');
    const response = await axios.get(`https://api.upbit.com/v1${endpoint}`, {
      params,
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json'
      }
    });
    
    console.log('업비트 API 응답 상태:', response.status);
    console.log('응답 데이터 개수:', Array.isArray(response.data) ? response.data.length : 'N/A');
    
    if (Array.isArray(response.data) && response.data.length > 0) {
      console.log('첫 번째 캔들 데이터 샘플:', response.data[0]);
      console.log('마지막 캔들 데이터 샘플:', response.data[response.data.length - 1]);
      
      // 데이터 날짜 범위 계산
      const firstDate = new Date(response.data[response.data.length - 1].candle_date_time_utc);
      const lastDate = new Date(response.data[0].candle_date_time_utc);
      console.log('데이터 날짜 범위:', 
        `${firstDate.toISOString().split('T')[0]} ~ ${lastDate.toISOString().split('T')[0]}`);
    } else {
      console.log('응답 데이터가 비어있거나 배열이 아닙니다:', response.data);
    }

    // 성공 응답
    return res.status(200).json(response.data);
  } catch (error: any) {
    console.error('업비트 API 오류:', error.message);
    
    if (error.response) {
      console.error('응답 상태:', error.response.status);
      console.error('응답 데이터:', error.response.data);
      
      // 업비트 API 에러 응답 형식 기록
      console.error('상세 에러 정보:', {
        status: error.response.status,
        statusText: error.response.statusText,
        headers: error.response.headers,
        data: error.response.data,
        requestConfig: {
          url: error.config?.url,
          method: error.config?.method,
          params: error.config?.params,
          headers: error.config?.headers
        }
      });
    } else if (error.request) {
      console.error('요청은 보냈으나 응답이 없음:', error.request);
    } else {
      console.error('에러 설정 중 오류 발생:', error.message);
    }
    
    // 에러 응답
    return res.status(error.response?.status || 500).json({
      error: error.response?.data || 'Internal server error',
      message: error.message,
      timestamp: new Date().toISOString(),
      path: req.url,
      query: req.query
    });
  }
}