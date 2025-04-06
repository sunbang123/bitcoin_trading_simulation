import { useState } from 'react';
import AuthForm from '../components/AuthForm';
import Layout from '../components/Layout';
import { useRouter } from 'next/router';

export default function RegisterPage() {
  const router = useRouter();
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const handleRegister = async (data: any) => {
    try {
      setIsLoading(true);
      setError(null);
      
      console.log('회원가입 시도:', data);
      
      // TODO: 실제 API 연동 (아래는 예시)
      // const response = await fetch('/api/auth/register', {
      //   method: 'POST',
      //   headers: { 'Content-Type': 'application/json' },
      //   body: JSON.stringify(data)
      // });
      
      // if (!response.ok) {
      //   const errorData = await response.json();
      //   throw new Error(errorData.message || '회원가입에 실패했습니다');
      // }
      
      // 지금은 임시로 1초 후에 로그인 페이지로 리다이렉트
      setTimeout(() => {
        console.log('회원가입 성공!');
        router.push('/login');
      }, 1000);
      
    } catch (err: any) {
      console.error('회원가입 오류:', err);
      setError(err.message || '회원가입 중 오류가 발생했습니다');
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Layout>
      <div className="container mx-auto p-4 max-w-md">
        <h1 className="text-2xl font-bold mb-6 text-center">회원가입</h1>
        
        {error && (
          <div className="mb-4 p-3 bg-red-500 bg-opacity-20 border border-red-500 rounded-md text-red-500">
            {error}
          </div>
        )}
        
        <AuthForm 
          mode="register" 
          onSubmit={handleRegister} 
        />
        
        <div className="mt-4 text-center">
          <p className="text-gray-400">
            이미 계정이 있으신가요? {' '}
            <a href="/login" className="text-blue-500 hover:underline">
              로그인하기
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