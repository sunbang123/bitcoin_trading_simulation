import { apiClient } from '@/utils/apiClient';

export type OrderType = 'BUY' | 'SELL';
export type OrderStatus = 'PENDING' | 'COMPLETED';

export interface OrderHistoryResponse {
  orderId: number;
  createdAt: string;
  coinSymbol: string;
  orderType: OrderType;
  price: number;
  quantity: number;
  totalAmount: number;
  orderStatus: OrderStatus;
}

export interface OrderCreateRequest {
  coinSymbol: string;
  quantity: number;
  price: number;
  orderType: OrderType;
  orderMethod: 'MARKET' | 'LIMIT';
}

export interface OrderCreateResponse {
  orderId: number;
  coinSymbol: string;
  orderType: OrderType;
}

class OrderService {
  async getMyOrders(): Promise<OrderHistoryResponse[]> {
    return apiClient.get<OrderHistoryResponse[]>('/api/orders');
  }

  async createOrder(data: OrderCreateRequest): Promise<OrderCreateResponse> {
    return apiClient.post<OrderCreateResponse>('/api/orders', data);
  }
}

export const orderService = new OrderService();
