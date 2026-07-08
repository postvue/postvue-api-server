# Dusol Server

## Tech Stack

- Java 17
- Spring Boot 3.3.1
- Spring Web MVC, Spring WebFlux
- Spring Security
- Spring Data JPA
- Querydsl 5
- PostgreSQL, H2 Test DB
- Flyway
- JWT, JJWT
- Spring Validation
- Spring Mail
- Springdoc OpenAPI Swagger
- AWS SDK S3, Cloudflare R2 compatible storage
- Gradle
- GitHub Actions

## 주요 기능

### 인증/사용자

- 이메일 회원가입, 로그인, 로그아웃
- Access Token, Refresh Token 발급 및 갱신
- 비밀번호 재설정 메일 발송 및 토큰 검증
- 회원 탈퇴와 탈퇴 유저 정리 스케줄링
- 관리자 사용자 조회, 통계, 강제 탈퇴, 영구 삭제

### 방 관리

- 사용자 방 목록/상세 조회
- 관리자 방 생성, 수정, 삭제
- 방 이미지 multipart 업로드
- 편의시설 포함/선택 옵션 관리
- 방 상태, 입주 가능일, 보증금/월세/관리비 등 주거 정보 관리

### 입주/퇴실

- 입주 신청 생성, 수정, 삭제, 상세 조회
- 퇴실 신청 생성, 수정, 삭제, 상세 조회
- 관리자 입주/퇴실 신청 목록 조회
- 관리자 승인/반려 및 거절 사유 관리
- 사용자별 입주/퇴실 신청 상태 조회

### 찜

- 방 찜 추가/삭제
- 사용자 찜 목록 조회
- 방별 찜 여부와 찜 수 조회

### 챗봇

- Spring 서버에서 FastAPI 챗봇 서버로 질문 전달
- 일반 응답과 SSE 스트리밍 응답 지원
- 세션별 대화 기록 저장
- 관리자용 챗봇 대화 조회

## 패키지 구조

```text
src/main/java/com/dusol/dusolserver
├── app
│   ├── auth
│   ├── chatbot
│   ├── cloud
│   ├── dashboard
│   ├── email
│   ├── room
│   ├── roommovein
│   ├── roommoveout
│   ├── scheduler
│   ├── users
│   └── wishlist
├── core
│   ├── config
│   └── security
├── domain
│   ├── amenity
│   ├── chatbot
│   ├── movein
│   ├── moveout
│   ├── resident
│   ├── room
│   ├── roomamenity
│   ├── roommedia
│   ├── serviceusers
│   └── wishlist
└── global
    ├── common
    ├── constant
    ├── exception
    ├── http
    └── util
```

- `app`: 컨트롤러, 서비스, 요청/응답 DTO 등 유스케이스 계층
- `domain`: JPA 엔티티, Repository, 도메인 VO
- `core`: Security, S3, Swagger, Querydsl, WebClient 등 인프라 설정
- `global`: 공통 응답, 예외, 상수, 유틸리티

## 인증/인가 설계

인증은 JWT 기반 stateless 방식입니다. 클라이언트는 `Authorization: Bearer {accessToken}` 헤더로 API를 호출합니다.

Spring Security에서 공개 API와 보호 API를 분리합니다.

- 공개 API
  - 로그인, 회원가입, 토큰 갱신, 비밀번호 재설정
  - 방 목록/상세 조회
  - 챗봇 health check
  - Swagger/OpenAPI 문서
- 사용자 API
  - `/api/v1/**`
  - `ROLE_USER`, `ROLE_ADMIN` 허용
- 관리자 API
  - `/api/v1/admin/**`
  - `ROLE_ADMIN`만 허용

인가 설정은 MockMvc 통합 테스트로 검증합니다.

- 비로그인 사용자의 관리자 API 접근은 `401 Unauthorized`
- 일반 사용자의 관리자 API 접근은 `403 Forbidden`
- 관리자의 관리자 API 접근은 `200 OK`

## DB 및 마이그레이션

운영 DB는 PostgreSQL을 기준으로 구성했습니다.

- Flyway로 DB 스키마 변경 이력 관리
- 운영/로컬 JPA 설정은 `ddl-auto: validate`
- 테스트 환경은 H2와 `ddl-auto: create-drop`
- Querydsl QClass는 Gradle JavaCompile 과정에서 생성

마이그레이션 파일 위치:

```text
src/main/resources/db/migration
```

## 외부 연동

### Cloudflare R2 / S3 Compatible Storage

방 이미지와 입주/퇴실 문서 파일은 S3 호환 스토리지에 업로드합니다. 업로드된 파일 URL은 DB에 저장하고, 파일명은 충돌을 피하기 위해 서버에서 생성합니다.

### FastAPI Chatbot Server

챗봇 기능은 별도 FastAPI 서버와 연동합니다.

- 일반 질문 응답
- SSE 스트리밍 질문 응답
- 대화 세션 ID 관리
- 대화 기록 저장

## 환경 변수

운영 설정은 환경 변수로 주입합니다. `application-*-local.yml`과 `.env`는 git에서 제외합니다.

```env
JWT_SECRET_KEY=

DB_URL=
DB_USERNAME=
DB_PASSWORD=

CLOUD_S3_CONTENT_BUCKET_ACCESS_KEY=
CLOUD_S3_CONTENT_BUCKET_SECRET_KEY=
CLOUD_S3_CONTENT_BUCKET_ENDPOINT=
CLOUD_S3_CONTENT_BUCKET_PUBLIC_URL=
CLOUD_S3_CONTENT_BUCKET_REGION=
CLOUD_S3_CONTENT_BUCKET_NAME=

DOMAIN_ALLOW_LIST=
PASSWORD_RESET_URL=
VERIFY_EMAIL_URL=
SERVER_URL_API=
SERVER_URL_ADMIN=
SERVER_COOKIE_ALLOW_DOMAIN=

SPRING_MAIL_USERNAME=
SPRING_MAIL_PASSWORD=

CHATBOT_BASE_URL=
```

## 로컬 실행

### 1. Java 버전 확인

```bash
java -version
```

Java 17이 필요합니다.

### 2. 로컬 설정 파일 준비

아래 profile 파일은 로컬 환경에 맞게 준비합니다.

```text
src/main/resources/application-db-local.yml
src/main/resources/application-auth.yml
src/main/resources/application-common-local.yml
src/main/resources/application-config-local.yml
src/main/resources/application-cloud-local.yml
src/main/resources/application-swagger-local.yml
```

로컬 설정 파일은 git에 포함하지 않습니다.

### 3. 애플리케이션 실행

```bash
./gradlew bootRun --args='--spring.profiles.active=local'
```

기본 포트는 `8080`입니다.

## 테스트

전체 테스트 실행:

```bash
./gradlew test
```

테스트 구성:

- 서비스 단위 테스트
- 도메인 테스트
- 유틸/검증 상수 테스트
- Spring context load 테스트
- MockMvc 기반 Security 통합 테스트

현재 주요 검증 범위:

- 인증/관리자 권한 검증
- 이메일 로그인/회원가입/토큰 로직
- 사용자 정보 수정
- 방 목록/상세 조회
- 입주 신청
- 찜 기능
- 챗봇 일반 응답/스트리밍 응답

## CI

GitHub Actions에서 테스트 전용 workflow를 제공합니다.

```text
.github/workflows/test.yml
```

동작 조건:

- `main`, `master`, `develop` 브랜치 push
- pull request

실행 내용:

```bash
./gradlew test
```

배포용 workflow는 별도 production CI/CD 파일에서 관리합니다.

## 주요 API

### Auth

- `POST /api/v1/auth/signup/email`
- `POST /api/v1/auth/login/email`
- `POST /api/v1/auth/renewal/tokens`
- `POST /api/v1/auth/logout`
- `POST /api/v1/auth/member-withdrawal`
- `POST /api/v1/auth/password-reset/request`
- `GET /api/v1/auth/password-reset/validate/{token}`
- `POST /api/v1/auth/password-reset/confirm`

### User

- `GET /api/v1/users/info`
- `PUT /api/v1/users/info`
- `GET /api/v1/admin/users`
- `GET /api/v1/admin/users/{userId}`
- `GET /api/v1/admin/users/stats`
- `DELETE /api/v1/admin/users/{userId}/withdraw`
- `DELETE /api/v1/admin/users/{userId}/permanent`

### Room

- `GET /api/v1/rooms`
- `GET /api/v1/rooms/{roomId}`
- `GET /api/v1/admin/rooms`
- `POST /api/v1/admin/rooms`
- `GET /api/v1/admin/rooms/{roomId}`
- `PUT /api/v1/admin/rooms/{roomId}`
- `DELETE /api/v1/admin/rooms/{roomId}`
- `GET /api/v1/admin/rooms/amenities`

### Move In / Move Out

- `GET /api/v1/rooms/my/movein`
- `GET /api/v1/rooms/my/moveout`
- `POST /api/v1/rooms/movein/{roomId}`
- `GET /api/v1/rooms/movein/{moveInId}`
- `PUT /api/v1/rooms/movein/{moveInId}`
- `DELETE /api/v1/rooms/movein`
- `POST /api/v1/rooms/moveout`
- `GET /api/v1/rooms/moveout/{moveOutId}`
- `PUT /api/v1/rooms/moveout/{moveOutId}`
- `DELETE /api/v1/rooms/moveout`
- `GET /api/v1/admin/rooms/moveins`
- `GET /api/v1/admin/rooms/moveins/{moveinId}`
- `POST /api/v1/admin/rooms/moveins/{moveinId}/approve`
- `GET /api/v1/admin/rooms/moveouts`
- `GET /api/v1/admin/rooms/moveouts/{moveOutId}`
- `POST /api/v1/admin/rooms/moveouts/{moveoutId}/approve`

### Wishlist

- `POST /api/v1/wishlist`
- `DELETE /api/v1/wishlist/{roomId}`
- `GET /api/v1/wishlist`
- `GET /api/v1/wishlist/{roomId}`

### Chatbot

- `POST /api/v1/chatbot/ask`
- `POST /api/v1/chatbot/ask/stream`
- `GET /api/v1/chatbot/health`
- `GET /api/v1/chatbot/conversations`
- `GET /api/v1/chatbot/conversations/{conversationId}`
- `GET /api/v1/chatbot/conversations/session/{sessionId}`

## Swagger

로컬 실행 후 Swagger UI에서 API 문서를 확인할 수 있습니다.

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/api-docs/json
```

## 보안 관리 원칙

- 비밀번호 원문은 저장하지 않고 BCrypt hash만 저장
- 비밀번호 hash는 API 응답으로 내려주지 않음
- local profile 설정과 `.env`는 git에서 제외
- 운영 secret은 GitHub Secrets 또는 서버 환경 변수로 주입
- 관리자 API는 `ROLE_ADMIN`만 접근 가능
- 인증/인가 정책은 MockMvc 통합 테스트로 검증
