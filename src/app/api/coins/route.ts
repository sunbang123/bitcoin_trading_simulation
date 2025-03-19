/* eslint-disable @typescript-eslint/no-unused-vars */
// app/api/coins/route.ts
import { NextResponse } from "next/server";

export async function GET() {
    // 업비트의 비트코인 시세를 가져오는 API URL
    const url = "https://api.upbit.com/v1/ticker?markets=KRW-BTC";

    try {
        const response = await fetch(url);
        const data = await response.json();

        // 필요한 데이터만 반환
        const { trade_price, opening_price, high_price, low_price } = data[0];

        return NextResponse.json({
            currentPrice: trade_price,
            openingPrice: opening_price,
            highPrice: high_price,
            lowPrice: low_price,
        });
    } catch (error) {
        return NextResponse.json({ error: "Failed to fetch data" }, { status: 500 });
    }
}
