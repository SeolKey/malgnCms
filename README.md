# CMS Backend API

2026 신입 Back-End 개발자 코딩 과제 - 간단한 CMS REST API

## 기술 스택

- **Java 25**
- **Spring Boot 4.0.3**
- **Spring Security** (인증 및 권한 관리)
- **Spring Data JPA** (데이터베이스 접근)
- **H2 Database** (파일 기반, 영구 저장)
- **Lombok** (보일러 코드 감소)
- **Session 기반 인증**
  - Spring Security 기본 세션 인증
  - 쿠키 기반 세션 관리
- **Bootstrap 5** (프론트엔드 UI)

## 주요 기능

### 인증 및 권한 관리
- **Session 기반 인증**
  - Spring Security 기본 세션 인증 방식
  - 쿠키 기반 세션 관리 (브라우저 자동 처리)
  - 세션에 사용자 인증 정보 저장
  - 모든 API 요청 시 쿠키(세션 ID) 자동 포함
- **Role 기반 권한 관리** (ADMIN, USER)
- **회원가입 및 로그인** 기능
- **로그아웃** 기능 (토큰 삭제)
- **사용자명 중복 확인** API

### 콘텐츠 관리
- 콘텐츠 CRUD 작업
- 페이징 지원 (페이지 번호 버튼 포함)
- 조회수 자동 증가 기능
- 수정자 정보 자동 기록 (lastModifiedBy)
- 권한 기반 접근 제어:
  - 작성자 본인만 수정/삭제 가능
  - ADMIN은 모든 콘텐츠 수정/삭제 가능
  - 프론트엔드에서 권한에 따라 버튼 표시/숨김

### 프론트엔드
- Bootstrap 5 다크 모드 UI
- 반응형 디자인
- 로그인 상태에 따른 네비게이션 바 자동 업데이트
- 권한에 따른 Edit/Delete 버튼 표시 제어
- 페이징 UI (페이지 번호, Previous/Next 버튼)

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

프로젝트 실행 시 다음 계정이 자동으로 생성됩니다 (`DataInitializer`에 의해):

- **Admin 계정**
  - Username: `admin`
  - Password: `password`
  - Role: `ADMIN`
  - 권한: 모든 콘텐츠 수정/삭제 가능

- **User 계정**
  - Username: `user1`, `user2`
  - Password: `password`
  - Role: `USER`
  - 권한: 본인이 작성한 콘텐츠만 수정/삭제 가능

> **참고**: 계정이 이미 존재하면 자동 생성되지 않습니다. 데이터베이스 파일(`./data/cmsdb.mv.db`)을 삭제하면 초기화됩니다.

## 로그인 방식

본 프로젝트는 **Session 기반 인증 방식**을 사용하여 인증을 구현했습니다.

### 선택한 인증 방식: Session 기반 인증

**이유:**
- 구현이 간단하고 이해하기 쉬움
- Spring Security의 기본 인증 방식 활용
- 서버에서 세션 관리로 보안 제어 용이
- 웹 브라우저에서 쿠키 기반 자동 처리

### 인증 흐름

1. **로그인 요청**
   - 클라이언트가 `/api/auth/login`에 username과 password를 전송
   - Spring Security의 `AuthenticationManager`가 자격 증명 검증
   - 검증 성공 시 서버에 세션 자동 생성 (쿠키에 세션 ID 저장)

2. **세션 생성**
   - 서버가 세션을 생성하고 세션 ID를 쿠키로 클라이언트에 전송
   - 이후 요청마다 브라우저가 자동으로 쿠키(세션 ID) 전송
   - 세션에는 사용자 인증 정보가 저장됨

3. **API 요청 시 인증**
   - 클라이언트가 모든 API 요청 시 쿠키(세션 ID) 자동 포함
   - Spring Security가 세션에서 사용자 인증 정보 확인
   - 인증된 사용자 정보를 SecurityContext에 설정

4. **권한 확인**
   - 각 API 요청마다 세션에서 사용자 정보 추출
   - 작성자 본인 또는 ADMIN 권한 확인 후 작업 수행

### Session 기반 인증

- **세션 사용**: `SessionCreationPolicy.IF_REQUIRED` 설정으로 필요 시 세션 생성
- **서버 저장**: 사용자 인증 정보가 서버 세션에 저장됨
- **쿠키 기반**: 브라우저가 자동으로 세션 쿠키 전송
- **최대 세션 수**: 1개 (동일 사용자의 중복 로그인 방지)

### 세션 관리

- **세션 생성**: 로그인 성공 시 자동 생성
- **세션 만료**: 서버 설정에 따라 자동 만료
- **로그아웃**: `/api/auth/logout` 호출 시 세션 무효화
- **현재 사용자 정보**: `/api/auth/me` API로 확인 가능

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
  "message": "로그인 성공",
  "username": "admin",
  "role": "ADMIN"
}
```

**응답 설명:**
- `message`: 로그인 성공 메시지
- `username`: 로그인한 사용자명
- `role`: 사용자 역할 (ADMIN 또는 USER)

**사용 방법:**
1. 로그인 성공 시 서버가 세션을 생성하고 쿠키(세션 ID)를 자동으로 전송
2. 이후 모든 API 요청 시 브라우저가 자동으로 쿠키 전송
3. JavaScript에서 `fetch` 사용 시 `credentials: 'include'` 옵션 필요
4. 로그아웃 시 `/api/auth/logout` 호출하여 세션 무효화

> **참고**: 세션은 서버에 저장되며, 쿠키를 통해 관리됩니다. 브라우저가 자동으로 쿠키를 처리하므로 별도의 토큰 저장이 필요 없습니다.

#### 사용자명 중복 확인
```
GET /api/auth/check-username/{username}

Response:
{
  "exists": true/false,
  "available": false/true
}
```

### 콘텐츠 API

모든 콘텐츠 API는 인증이 필요합니다. 세션 기반 인증이므로 로그인 후 쿠키(세션 ID)가 자동으로 전송됩니다.

#### 콘텐츠 목록 조회 (페이징)
```
GET /api/contents?page=0&size=10&sort=createdDate,desc
```

#### 콘텐츠 상세 조회
```
GET /api/contents/{id}
```

> **참고**: 상세 조회 시 조회수가 자동으로 증가합니다.

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

> **참고**: 수정 시 `lastModifiedBy` 필드에 수정한 사용자명이 자동으로 기록됩니다.

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
   - 프론트엔드에서 권한에 따라 Edit/Delete 버튼이 표시/숨김 처리됨
3. **콘텐츠 조회**: 모든 인증된 사용자 가능
4. **조회수 증가**: 상세 페이지 조회 시 자동 증가

## 데이터베이스 설정

### H2 데이터베이스
- **타입**: 파일 기반 (영구 저장)
- **파일 위치**: `./data/cmsdb.mv.db`
- **DDL 모드**: `update` (테이블 자동 생성/업데이트)
- **초기 데이터**: 애플리케이션 시작 시 자동 생성 (`DataInitializer`)

### H2 Console
개발 환경에서 H2 데이터베이스 콘솔에 접근할 수 있습니다:
- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/cmsdb`
- Username: `sa`
- Password: (비어있음)

> **참고**: 데이터베이스 파일을 삭제하면 모든 데이터가 초기화되고, 다음 실행 시 초기 계정이 다시 생성됩니다.

## 프로젝트 구조

```
src/main/java/com/malgn/
├── entity/          # 엔티티 클래스 (Contents, User)
├── repository/      # JPA Repository 인터페이스
├── service/         # 비즈니스 로직
├── controller/      # REST API 컨트롤러
├── dto/             # 데이터 전송 객체
├── security/        # Security 관련 클래스 (JWT)
├── exception/       # 예외 처리 클래스
├── configure/       # 설정 클래스 (SecurityConfiguration)
└── config/          # 초기화 클래스 (DataInitializer)

src/main/resources/
├── templates/       # HTML 템플릿 파일
│   ├── login.html
│   ├── signup.html
│   ├── contents.html
│   ├── content-new.html
│   ├── content-detail.html
│   └── content-edit.html
├── db/sql/         # 데이터베이스 초기화 SQL
│   ├── h2-schema.sql
│   └── h2-data.sql
└── application.yml # 애플리케이션 설정
```

## 사용한 도구 및 참고 자료

- **AI 도구**: Cursor AI Assistant를 사용하여 코드 구조 설계 및 구현 지원
- **참고 자료**: 
  - Spring Boot 4.0 공식 문서
  - Spring Security 공식 문서 (Session 기반 인증)

## 주요 구현 기능

### 백엔드
- ✅ **Session 기반 인증 시스템**
  - Spring Security 기본 세션 인증
  - 쿠키 기반 세션 관리
  - 세션에 사용자 인증 정보 저장
- ✅ **전역 예외 처리** (`GlobalExceptionHandler`)
- ✅ **페이징 처리** (Spring Data JPA Page)
- ✅ **조회수 자동 증가** 기능 (상세 조회 시)
- ✅ **수정자 정보 자동 기록** (`lastModifiedBy` 필드)
- ✅ **Validation**을 통한 입력값 검증
- ✅ **권한 기반 접근 제어** (작성자 본인 또는 ADMIN)
- ✅ **초기 데이터 자동 생성** (`DataInitializer`)
- ✅ **파일 기반 H2 데이터베이스** (데이터 영구 저장)

### 프론트엔드
- ✅ Bootstrap 5 다크 모드 UI
- ✅ 반응형 디자인
- ✅ 로그인/로그아웃 기능
- ✅ Session 기반 인증 상태 관리
- ✅ 현재 사용자 정보 조회 API (`/api/auth/me`)
- ✅ 권한에 따른 버튼 표시 제어
- ✅ 페이징 UI (페이지 번호, Previous/Next)
- ✅ 사용자명 중복 확인
- ✅ 패스워드 확인 기능
- ✅ 로그인 상태에 따른 네비게이션 바 업데이트

## 주요 변경 사항

### 2026-03-05 업데이트
1. **데이터베이스 설정 변경**
   - 인메모리 → 파일 기반 H2 데이터베이스
   - `ddl-auto: create-drop` → `update`
   - 데이터 영구 저장 지원

2. **JWT 토큰 개선**
   - 토큰에 `role` 정보 추가
   - 프론트엔드에서 권한 확인 가능

3. **관리자 권한 강화**
   - ADMIN은 모든 게시글 수정/삭제 가능
   - 수정 시 `lastModifiedBy`에 수정자 정보 기록

4. **조회수 기능 수정**
   - `@Transactional(readOnly = true)` → `@Transactional`로 변경하여 조회수 증가 정상 작동

5. **초기 데이터 자동 생성**
   - `DataInitializer` 클래스 추가
   - 애플리케이션 시작 시 관리자 계정 자동 생성

6. **UI/UX 개선**
   - Bootstrap 5 다크 모드 적용
   - 카드 크기 최적화
   - 페이징 UI 개선 (페이지 번호 버튼 추가)
   - 네비게이션 바 브랜드 이름 변경 (맑은기술)

7. **권한 체크 개선**
   - 프론트엔드에서 JWT 토큰 디코딩하여 권한 확인
   - 작성자 또는 ADMIN만 Edit/Delete 버튼 표시

8. **로그인/로그아웃 기능**
   - 로그인 토큰 저장 문제 해결
   - 로그아웃 기능 추가
   - 로그인 상태에 따른 네비게이션 바 자동 업데이트
