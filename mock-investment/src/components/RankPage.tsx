import React, { useEffect, useState } from 'react';
import Image from 'next/image';
import Header from './Header';
import { RankingResponse, rankingService } from '@/services/rankingService';

const formatKrw = (value: number) =>
  new Intl.NumberFormat('ko-KR', {
    style: 'currency',
    currency: 'KRW',
    maximumFractionDigits: 0
  }).format(value);

const formatProfitRate = (value: number) => `${value >= 0 ? '+' : ''}${value.toFixed(2)}%`;

const RankPage: React.FC = () => {
  const [rankings, setRankings] = useState<RankingResponse[]>([]);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    const loadRankings = async () => {
      try {
        setIsLoading(true);
        setErrorMessage('');
        const data = await rankingService.getRankings();
        setRankings(data);
      } catch (error) {
        console.error(error);
        setErrorMessage('랭킹 데이터를 불러오지 못했습니다.');
      } finally {
        setIsLoading(false);
      }
    };

    void loadRankings();
  }, []);

  return (
    <div className="min-h-screen bg-white">
      <Header />

      <div className="relative h-56 w-full">
        <Image src="/images/rank_banner.png" alt="Rank Banner" fill className="object-cover" priority />
      </div>

      <main className="mx-auto max-w-5xl px-4 py-10">
        <h2 className="mb-6 text-center text-3xl font-bold text-purple-700">
          비트코인 모의투자 랭킹
        </h2>

        <div className="overflow-x-auto rounded-lg shadow">
          <table className="w-full border-collapse text-center">
            <thead>
              <tr className="bg-gradient-to-r from-indigo-200 to-purple-200">
                <th className="px-4 py-3">순위</th>
                <th className="px-4 py-3">이름</th>
                <th className="px-4 py-3">보유자산</th>
                <th className="px-4 py-3">대표 코인</th>
                <th className="px-4 py-3">수익률</th>
              </tr>
            </thead>
            <tbody>
              {isLoading && (
                <tr>
                  <td className="px-4 py-8 text-gray-500" colSpan={5}>
                    랭킹 데이터를 불러오는 중입니다.
                  </td>
                </tr>
              )}

              {!isLoading && errorMessage && (
                <tr>
                  <td className="px-4 py-8 text-red-500" colSpan={5}>
                    {errorMessage}
                  </td>
                </tr>
              )}

              {!isLoading && !errorMessage && rankings.length === 0 && (
                <tr>
                  <td className="px-4 py-8 text-gray-500" colSpan={5}>
                    아직 랭킹 데이터가 없습니다.
                  </td>
                </tr>
              )}

              {!isLoading &&
                !errorMessage &&
                rankings.map((user) => (
                  <tr key={`${user.rank}-${user.username}`} className="border-b hover:bg-gray-50">
                    <td className="px-4 py-2 font-semibold">{user.rank === 1 ? '1위' : user.rank}</td>
                    <td className="px-4 py-2">{user.username}</td>
                    <td className="px-4 py-2">{formatKrw(user.totalAssetAmount)}</td>
                    <td className="px-4 py-2">{user.topCoin}</td>
                    <td className={user.profitRate >= 0 ? 'px-4 py-2 text-green-600' : 'px-4 py-2 text-red-500'}>
                      {formatProfitRate(user.profitRate)}
                    </td>
                  </tr>
                ))}
            </tbody>
          </table>
        </div>
      </main>
    </div>
  );
};

export default RankPage;
