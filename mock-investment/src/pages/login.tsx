import { useState } from 'react';
import AuthForm from '../components/AuthForm';
import Layout from '../components/Layout';
import { useRouter } from 'next/router';

export default function LoginPage() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleLogin = async (data: any) => {
    try {
      setIsLoading(true);
      setError(null);
      
      console.log('로그인 시도:', data);
      
      // TODO: 실제 API 연동 (아래는 예시)
      // const response = await fetch('/api/auth/login', {
      //   method: 'POST',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify(data)
      // });
      
      // if (!response.ok) {
      //   const errorData = await response.json();
      //   throw new Error(errorData.message || '로그인에 실패했습니다');
      // }
      
      // const userData = await response.json();
      // 로그인 성공 처리...
      
      // 지금은 임시로 1초 후에 홈으로 리다이렉트
      setTimeout(() => {
        console.log('로그인 성공!');
        router.push('/');
      }, 1000);
      
    } catch (err: any) {
      console.error('로그인 오류:', err);
      setError(err.message || '로그인 중 오류가 발생했습니다');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Layout>
      <div className="container mx-auto p-4 max-w-md">
        <h1 className="text-2xl font-bold mb-6 text-center">로그인</h1>
        
        {error && (
          <div className="mb-4 p-3 bg-red-500 bg-opacity-20 border border-red-500 rounded-md text-red-500">
            {error}
          </div>
        )}
        
        <AuthForm 
          mode="login" 
          onSubmit={handleLogin} 
        />
        
        <div className="mt-4 text-center">
          <p className="text-gray-400">
            계정이 없으신가요? {' '}
            <a href="/register" className="text-blue-500 hover:underline">
              회원가입하기
            </a>
          </p>
        </div>
        
        {isLoading && (
          <div className="mt-4 flex justify-center">
            <div className="w-6 h-6 border-2 border-t-blue-500 border-r-transparent border-b-blue-500 border-l-transparent rounded-full animate-spin"></div>
          </div>
        )}
      </div>
    </Layout>
  );
}