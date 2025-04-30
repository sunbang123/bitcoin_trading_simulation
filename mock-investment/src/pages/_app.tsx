// src/pages/_app.tsx
import '../styles/globals.css'; // Tailwind 스타일 적용

import type { AppProps } from 'next/app';

export default function App({ Component, pageProps }: AppProps) {
  return <Component {...pageProps} />;
}
