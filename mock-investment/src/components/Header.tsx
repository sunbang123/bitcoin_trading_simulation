export default function Header() {
    return (
      <header className="bg-blue-100 p-4 flex justify-between items-center">
        <h1 className="text-xl font-bold text-blue-700">🪙 BiTS</h1>
        <nav className="space-x-6 text-sm text-gray-700">
          <a href="#">거래소</a>
          <a href="#">내정보</a>
          <a href="#">랭킹</a>
        </nav>
        <div className="space-x-2">
          <button className="text-sm text-blue-600">로그인</button>
          <button className="text-sm text-blue-600">회원가입</button>
        </div>
      </header>
    );
  }
  