import { apiClient } from '@/utils/apiClient';

export interface RankingResponse {
  rank: number;
  username: string;
  totalAssetAmount: number;
  topCoin: string;
  profitRate: number;
}

class RankingService {
  async getRankings(): Promise<RankingResponse[]> {
    return apiClient.get<RankingResponse[]>('/api/rankings');
  }
}

export const rankingService = new RankingService();
