// 사용자 인증상태 관리 서비스
// API 호출 시 또는 로그인 상태 유지

import { API_BASE_URL } from '@/config/api';

interface TokenResponse {
  accessToken: string;
  refreshToken: string;
}

interface ErrorResponse {
  message?: string;
}

class AuthService {
  private accessToken: string | null = null;
  private tokenKey = 'accessToken';
  private refreshTokenKey = 'refreshToken';

  constructor() {
    if (typeof window !== 'undefined') {
      this.accessToken = localStorage.getItem(this.tokenKey);
    }
  }

  setAccessToken(token: string) {
    this.accessToken = token;
    if (typeof window !== 'undefined') {
      localStorage.setItem(this.tokenKey, token);
    }
  }

  getAccessToken(): string | null {
    return this.accessToken;
  }

  setRefreshToken(token: string) {
    if (typeof window !== 'undefined') {
      localStorage.setItem(this.refreshTokenKey, token);
    }
  }

  getRefreshToken(): string | null {
    if (typeof window !== 'undefined') {
      return localStorage.getItem(this.refreshTokenKey);
    }
    return null;
  }

  clearTokens() {
    this.accessToken = null;
    if (typeof window !== 'undefined') {
      localStorage.removeItem(this.tokenKey);
      localStorage.removeItem(this.refreshTokenKey);
    }
  }

  async login(email: string, password: string): Promise<TokenResponse> {
    const response = await fetch(`${API_BASE_URL}/api/auth/login`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ email, password }),
    });

    if (!response.ok) {
      const error = await response.json().catch(() => null) as ErrorResponse | null;
      throw new Error(error?.message || '이메일 또는 비밀번호가 올바르지 않습니다.');
    }

    const tokens = await response.json() as TokenResponse;

    if (!tokens.accessToken || !tokens.refreshToken) {
      throw new Error('로그인 응답에 인증 토큰이 없습니다.');
    }

    this.setAccessToken(tokens.accessToken);
    this.setRefreshToken(tokens.refreshToken);

    return tokens;
  }

  async refreshAccessToken(): Promise<string> {
    const refreshToken = this.getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    const response = await fetch(`${API_BASE_URL}/api/auth/refresh`, {
      method: 'POST',
      headers: { 'Content-Type': 'application/json' },
      body: JSON.stringify({ refreshToken })
    });

    if (!response.ok) {
      throw new Error('Token refresh failed');
    }

    const data = await response.json();
    this.setAccessToken(data.accessToken);
    this.setRefreshToken(data.refreshToken);

    return data.accessToken;
  }
}

export const authService = new AuthService();
