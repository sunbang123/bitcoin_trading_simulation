import React, { useEffect, useMemo, useState } from 'react';
import MyInfo, { UserInfoProps } from '../components/MyInfo';
import { assetService, TotalAssetResponse } from '@/services/assetService';
import { orderService, OrderHistoryResponse } from '@/services/orderService';
import { userService } from '@/services/userService';

interface CurrentUserResponse {
  id: number;
  email: string;
  username: string;
  phoneNumber: string;
  registeredAt: string;
}

const coinNames: Record<string, string> = {
  BTC: '비트코인',
  ETH: '이더리움',
  XRP: '리플',
};

const emptyUserData: UserInfoProps = {
  name: '',
  email: '',
  phone: '',
  joinDate: '',
  totalBalance: 0,
  goalAmount: 0,
  currentAmount: 0,
  goalPercentage: 0,
  assets: [],
  transactions: [],
};

const formatDateTime = (value?: string) => {
  if (!value) return '-';
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return value;

  return date.toLocaleString('ko-KR', {
    year: 'numeric',
    month: '2-digit',
    day: '2-digit',
    hour: '2-digit',
    minute: '2-digit',
  });
};

const toNumber = (value: unknown) => Number(value ?? 0);

const buildUserData = (
  user: CurrentUserResponse,
  asset: TotalAssetResponse,
  orders: OrderHistoryResponse[]
): UserInfoProps => ({
  name: user.username,
  email: user.email,
  phone: user.phoneNumber,
  joinDate: formatDateTime(user.registeredAt),
  totalBalance: toNumber(asset.totalAssetAmount),
  goalAmount: toNumber(asset.krwBalance),
  currentAmount: toNumber(asset.coinAssetAmount),
  goalPercentage: Math.min(100, Math.max(0, toNumber(asset.krwRatio))),
  assets: asset.coinAssets.map((coin) => ({
    name: coinNames[coin.coinSymbol] ?? coin.coinSymbol,
    symbol: coin.coinSymbol,
    amount: toNumber(coin.quantity),
    value: toNumber(coin.evaluatedAmount),
    change: toNumber(coin.profitRate),
    percentage: toNumber(coin.holdingRatio),
  })),
  transactions: orders.map((order) => ({
    date: formatDateTime(order.createdAt),
    currency: order.coinSymbol,
    type: order.orderType === 'BUY' ? 'buy' : 'sell',
    price: toNumber(order.price),
    amount: toNumber(order.quantity),
    total: toNumber(order.totalAmount),
    status: order.orderStatus === 'COMPLETED' ? '완료' : '대기',
  })),
});

const MyInfoPage: React.FC = () => {
  const [userData, setUserData] = useState<UserInfoProps>(emptyUserData);
  const [isLoading, setIsLoading] = useState(true);
  const [errorMessage, setErrorMessage] = useState('');

  useEffect(() => {
    const fetchMyInfo = async () => {
      try {
        setIsLoading(true);
        setErrorMessage('');

        const [user, asset, orders] = await Promise.all([
          userService.getCurrentUser(),
          assetService.getMyAssets(),
          orderService.getMyOrders(),
        ]);

        setUserData(buildUserData(user, asset, orders));
      } catch (error) {
        console.error(error);
        setErrorMessage('내 정보를 불러오지 못했습니다. 로그인 상태와 백엔드 서버 실행 여부를 확인해 주세요.');
      } finally {
        setIsLoading(false);
      }
    };

    fetchMyInfo();
  }, []);

  const props = useMemo(
    () => ({
      ...userData,
      isLoading,
      errorMessage,
    }),
    [errorMessage, isLoading, userData]
  );

  return <MyInfo {...props} />;
};

export default MyInfoPage;
