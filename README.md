# 💰 MoYeo
개인과 모임이 함께 사용할 수 있는 외화·한화 통합 관리, 모임통장, 환전·결제, 여행 정산 기능을 지원하는 종합 금융 플랫폼입니다.

## 📑 목차

- [프로젝트 소개](#-프로젝트-소개)
- [주요 기능](#-주요-기능)
- [기술 스택](#️-기술-스택)
- [프로젝트 구조](#-프로젝트-구조)
- [화면](#-화면)
- [ERD](#-ERD)

## 🚀 프로젝트 소개

<img width="514" height="332" alt="moyeo" src="https://github.com/user-attachments/assets/8a16f89f-4e8f-4e3c-b06c-58024555f72d" />

## ✨ 주요 기능

### 👤 회원 관리
- 휴대폰 본인인증
- 비밀번호·생체인증 로그인
- 계좌 연결(1원 인증)

### 💵 모여 머니
- 한화/외화 머니 충전·출금·환전
- 잔액/거래내역 조회

### 👥 모여 박스 (모임통장)
- 모임 생성·멤버 초대
- 권한 관리
- 입출금 및 정산

### 💱 환율/환전
- WebSocket 기반 실시간 협업 그리기
- 다양한 색상과 선 굵기 선택
- 지우개 모드 지원
- 사용자별 커서 표시
- 캔버스 초기화 기능

### 💱 환율/환전
- 실시간 환율 조회
- 목표 환율 예약 환전

### 🧾 결제 / QR
- QR 기반 결제
- 지출 기록 및 카테고리 관리

### 📊 통계 관리
- 카테고리별 지출 통계
- 지도 기반 지출 위치 표시

### ⚖️ 정산 관리
- 여행 종료 시 자동 정산(1/N 분배)
- 정산 조정

### 🔔 알림 서비스
- 입출금, 결제, 환율 도달 등 푸시 알림


## 🛠️ 기술 스택

### Backend
- **Framework**: Spring Boot 3.5.5
- **Language**: Java 17
- **Build Tool**: Gradle

### Frontend
- **Framework**: Android (Jetpack Compose 기반)
- **Language**: Kotlin
- **Build Tool**: Gradle (Kotlin DSL)


### Infrastructure
- **Container**: Docker, Docker Compose
- **Web Server**: Nginx
- **CI/CD**: Jenkins

## 📁 프로젝트 구조

```
S13P21D108/
├── backend/                # 백엔드 서버 (Spring Boot)
│   └── moyeo/
│       ├── build.gradle    # Spring Boot 빌드 설정
│       ├── settings.gradle
│       ├── Dockerfile
│       ├── docker-compose.yml
│       ├── prometheus.yml
│       └── src/            # 서버 소스코드
│
├── frontend/               # 프론트엔드 (Android App)
│   ├── app/
│   │   ├── src/            # 안드로이드 앱 코드
│   │   ├── build.gradle.kts
│   │   └── google-services.json
│   ├── build.gradle.kts    # 전체 gradle 설정
│   ├── settings.gradle.kts
│   └── gradle/
│       └── wrapper/
│
├── README.md
└── .gitignore

```

## 화면

### 모임박스 화면
<img width="540" height="1200" alt="Screenshot_20250928_234728" src="https://github.com/user-attachments/assets/0a763878-3737-4aca-8196-c1a3e816954c" />
<img width="540" height="1200" alt="Screenshot_20250928_234423" src="https://github.com/user-attachments/assets/651225e8-057e-4e03-8a88-37ed14e30ce2" />
<img width="540" height="1200" alt="Screenshot_20250928_234529" src="https://github.com/user-attachments/assets/6c5c6baf-c77e-4052-bf0a-47f2a05dfb19" />

### 통계 화면
<img width="540" height="1200" alt="Screenshot_20250928_235555" src="https://github.com/user-attachments/assets/a4e5fd17-e83b-4480-8c26-762da08dcbad" />
<img width="540" height="1200" alt="Screenshot_20250928_235546" src="https://github.com/user-attachments/assets/f44dd68c-687b-4c26-ae08-a89f459f0395" />
<img width="540" height="1200" alt="Screenshot_20250928_235442" src="https://github.com/user-attachments/assets/8ff0aa70-9d6d-4ad1-b71b-1c0925728709" />

### 환전
<img width="540" height="1200" alt="Screenshot_20250928_234203" src="https://github.com/user-attachments/assets/54cf7d37-fdf6-40d7-84b7-9ec65af37a66" />
<img width="540" height="1200" alt="Screenshot_20250928_234234" src="https://github.com/user-attachments/assets/5fadd18d-76a1-487b-8696-5c414ce633b5" />
<img width="540" height="1200" alt="Screenshot_20250928_234305" src="https://github.com/user-attachments/assets/d3a27af0-644c-459f-a396-ecff8c44709a" />

### QR 결제
<img width="540" height="1200" alt="Screenshot_20250928_235219" src="https://github.com/user-attachments/assets/e0d7f0fe-2b4a-4765-a27d-f921f57ea703" />

### 정산
<img width="540" height="1200" alt="Screenshot_20250928_234757" src="https://github.com/user-attachments/assets/52ed8acb-9943-4a8a-8afd-f956279b01fc" />
<img width="540" height="1200" alt="Screenshot_20250928_234820" src="https://github.com/user-attachments/assets/0963a56f-5867-47d0-b52c-cb9e6dca99e7" />
<img width="540" height="1200" alt="Screenshot_20250928_235139" src="https://github.com/user-attachments/assets/ef08ae5d-4e29-4d12-9d0e-20d8a0a13a2c" />

## ERD
<img width="1538" height="1275" alt="moyeo_erd" src="https://github.com/user-attachments/assets/f4bd23f1-063a-4245-b07c-43ac9eaea9b0" />

