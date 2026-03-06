# CMS Backend API

2026 신입 Back-End 개발자 코딩 과제 - 간단한 CMS REST API

## 기술 스택

- Java 25
- Spring Boot 4.0.3
- Spring Security
- Spring Data JPA
- H2 Database (인메모리)
- Lombok
- JWT (JSON Web Token) - 인증 방식

## 주요 기능

### 인증 및 권한 관리
- JWT 기반 인증
- Role 기반 권한 관리 (ADMIN, USER)
- 회원가입 및 로그인

### 콘텐츠 관리
- 콘텐츠 CRUD 작업
- 페이징 지원
- 권한 기반 접근 제어:
  - 작성자 본인만 수정/삭제 가능
  - ADMIN은 모든 콘텐츠 수정/삭제 가능

### 예외 처리
- 전역 예외 처리
- 커스텀 예외 클래스
- 표준화된 에러 응답

## 프로젝트 실행 방법

### 1. 빌드
```bash
./gradlew build
```

### 2. 실행
```bash
./gradlew bootRun
```

애플리케이션은 `http://localhost:8080`에서 실행됩니다.

## 초기 계정

프로젝트 실행 시 다음 계정이 자동으로 생성됩니다:

- **Admin 계정**
  - Username: `admin`
  - Password: `password`
  - Role: `ADMIN`

- **User 계정**
  - Username: `user1`, `user2`
  - Password: `password`
  - Role: `USER`

## API 문서

### 인증 API

#### 회원가입
```
POST /api/auth/signup
Content-Type: application/json

{
  "username": "testuser",
  "password": "password123"
}
```

#### 로그인
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "password"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "type": "Bearer"
}
```

### 콘텐츠 API

모든 콘텐츠 API는 인증이 필요합니다. 요청 헤더에 JWT 토큰을 포함해야 합니다:
```
Authorization: Bearer {token}
```

#### 콘텐츠 목록 조회 (페이징)
```
GET /api/contents?page=0&size=10&sort=createdDate,desc
```

#### 콘텐츠 상세 조회
```
GET /api/contents/{id}
```

#### 콘텐츠 생성
```
POST /api/contents
Content-Type: application/json
Authorization: Bearer {token}

{
  "title": "제목",
  "description": "내용"
}
```

#### 콘텐츠 수정
```
PUT /api/contents/{id}
Content-Type: application/json
Authorization: Bearer {token}

{
  "title": "수정된 제목",
  "description": "수정된 내용"
}
```

#### 콘텐츠 삭제
```
DELETE /api/contents/{id}
Authorization: Bearer {token}
```

## 권한 규칙

1. **콘텐츠 생성**: 모든 인증된 사용자 가능
2. **콘텐츠 수정/삭제**:
   - 작성자 본인만 가능
   - ADMIN 역할은 모든 콘텐츠 수정/삭제 가능
3. **콘텐츠 조회**: 모든 인증된 사용자 가능

## H2 Console

개발 환경에서 H2 데이터베이스 콘솔에 접근할 수 있습니다:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:test`
- Username: `sa`
- Password: (비어있음)

## 프로젝트 구조

```
src/main/java/com/malgn/
├── entity/          # 엔티티 클래스 (Contents, User)
├── repository/      # JPA Repository 인터페이스
├── service/          # 비즈니스 로직
├── controller/      # REST API 컨트롤러
├── dto/             # 데이터 전송 객체
├── security/        # Security 관련 클래스 (JWT)
├── exception/       # 예외 처리 클래스
└── configure/       # 설정 클래스
```

## 사용한 도구 및 참고 자료

- **AI 도구**: Cursor AI Assistant를 사용하여 코드 구조 설계 및 구현 지원
- **참고 자료**: 
  - Spring Boot 4.0 공식 문서
  - Spring Security 공식 문서
  - JWT.io 공식 문서

## 추가 구현 기능

- JWT 기반 인증 시스템
- 전역 예외 처리
- 페이징 처리
- 조회수 자동 증가 기능
- Validation을 통한 입력값 검증
