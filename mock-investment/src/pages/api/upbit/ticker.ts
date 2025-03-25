import type { NextApiRequest, NextApiResponse } from "next";

// 업비트 시세 API → 클라이언트에서 호출될 엔드포인트
export default async function handler(req: NextApiRequest, res: NextApiResponse) {
  // 쿼리 파라미터에서 마켓 코드 가져오기 (기본값: KRW-BTC)
  const market = req.query.market || "KRW-BTC";

  try {
    // 업비트 REST API 호출
    const response = await fetch(`https://api.upbit.com/v1/ticker?markets=${market}`);

    // 응답을 JSON으로 변환
    const data = await response.json();

    // 첫 번째 결과 반환 (보통 1개만 요청하니까)
    res.status(200).json(data[0]);
  } catch (error) {
    // 오류 발생 시 500 에러 반환
    res.status(500).json({ error: "Failed to fetch data from Upbit" });
  }
}
