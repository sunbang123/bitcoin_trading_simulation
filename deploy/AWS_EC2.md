# AWS EC2 배포

## 권장 인스턴스

- Ubuntu 24.04 LTS
- t3.small 이상
- 보안 그룹 인바운드: SSH 22(본인 IP), HTTP 3000(데모 접속), 필요 시 API 확인용 8080

## 서버 준비

EC2에 Docker Engine과 Docker Compose 플러그인을 설치하고 이 저장소를 복제합니다.

```bash
git clone https://github.com/sunbang123/bitcoin_trading_simulation.git
cd bitcoin_trading_simulation
cp deploy/.env.example .env
```

`.env`의 `JWT_SECRET`과 `APP_ORIGIN`을 실제 값으로 변경합니다. 비밀값은 Git에 커밋하지 않습니다.

## 실행

```bash
docker compose up -d --build
docker compose ps
docker compose logs --tail=100 backend
docker compose logs --tail=100 frontend
```

브라우저에서 `http://EC2_PUBLIC_IP:3000`으로 접속합니다. 프론트엔드의 `/backend/*` 요청은 Docker 네트워크 내부의 Spring 서버로 전달됩니다.

## 검증

1. 회원가입 후 로그인
2. BTC·ETH·XRP 실시간 시세 수신
3. 랭킹 페이지 조회
4. 주문 생성 및 체결 API 확인
5. 내 정보에서 주문 이력과 보유 자산 확인

## 업데이트

```bash
git pull
docker compose up -d --build
```
