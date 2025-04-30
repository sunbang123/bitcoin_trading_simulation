// tailwind.config.js
/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
      "./src/**/*.{js,ts,jsx,tsx}", // Tailwind가 클래스 사용하는 파일을 감지할 위치
    ],
    theme: {
      extend: {}, // 커스터마이징하고 싶을 때 여기에 추가
    },
    plugins: [], // 플러그인 추가 가능
  };
  