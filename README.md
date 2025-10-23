# 💰 MoYeo
개인과 모임이 함께 사용할 수 있는 외화·한화 통합 관리, 모임통장, 환전·결제, 여행 정산 기능을 지원하는 종합 금융 플랫폼입니다.

## 📑 목차

- [프로젝트 소개](#-프로젝트-소개)
- [주요 기능](#-주요-기능)
- [기술 스택](#️-기술-스택)
- [프로젝트 구조](#-프로젝트-구조)

## 🚀 프로젝트 소개

여행이나 공동 소비 상황에서 투명하고 편리한 자금 관리 경험을 제공하는 것을 목표로 합니다.


## 📱 화면

![Main Screen]( )

*MoYeo 메인 화면*



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


