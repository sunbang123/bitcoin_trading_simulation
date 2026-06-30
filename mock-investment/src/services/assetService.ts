import { apiClient } from '@/utils/apiClient';

export interface CoinAssetResponse {
  coinSymbol: string;
  quantity: number;
  evaluatedAmount: number;
  profitRate: number;
  holdingRatio: number;
}

export interface TotalAssetResponse {
  totalAssetAmount: number;
  krwBalance: number;
  krwRatio: number;
  coinAssetAmount: number;
  coinRatio: number;
  coinAssets: CoinAssetResponse[];
}

class AssetService {
  async getMyAssets(): Promise<TotalAssetResponse> {
    return apiClient.get<TotalAssetResponse>('/api/assets/me');
  }
}

export const assetService = new AssetService();
