# REST API 문서

## 인증

본 프로젝트는 **Session 기반 인증**을 사용합니다. 로그인 성공 시 서버가 세션을 생성하고 쿠키(세션 ID)를 자동으로 전송합니다. 이후 모든 API 요청 시 브라우저가 자동으로 쿠키를 전송합니다.

> **참고**: JavaScript에서 `fetch` 사용 시 `credentials: 'include'` 옵션이 필요합니다.

### UserID와 Username

본 프로젝트는 **UserID(로그인 ID)**와 **Username(사용자명)**을 분리하여 관리합니다:

- **UserID (`userId`)**: 로그인 시 사용하는 고유 ID (필수, unique, not null)
  - Java 필드명: `userId` (카멜케이스)
  - 데이터베이스 컬럼명: `userid` (스네이크 케이스)
- **Username (`username`)**: 화면에 표시되는 사용자명 (필수, unique, not null)
- 콘텐츠의 `created_by`, `last_modified_by`에는 **Username**만 저장됩니다 (UserID가 아닌 사용자명)

## 인증 API

### 회원가입

사용자 계정을 생성합니다.

**요청**
```
POST /api/auth/signup
Content-Type: application/json

{
  "userId": "testuser",
  "username": "테스트사용자",
  "password": "password123"
}
```

**요청 본문**
- `userId` (필수): 로그인 시 사용할 고유 ID (3-50자, unique, not null)
- `username` (필수): 화면에 표시될 사용자명 (최대 50자, unique, not null)
- `password` (필수): 비밀번호 (6자 이상)

**응답**
- **성공 (201 Created)**
```json
{
  "id": 1,
  "userId": "testuser",
  "username": "테스트사용자",
  "role": "USER"
}
```

- **실패 (409 Conflict)** - UserID 중복
```json
{
  "timestamp": "2026-03-06T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "UserID already exists: testuser",
  "path": "/api/auth/signup"
}
```

- **실패 (400 Bad Request)** - Validation 실패
```json
{
  "timestamp": "2026-03-06T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "fieldErrors": {
    "username": "Username is required",
    "password": "Password must be at least 6 characters"
  }
}
```

### 로그인

사용자 인증을 수행하고 세션을 생성합니다.

**요청**
```
POST /api/auth/login
Content-Type: application/json

{
  "userId": "admin",
  "password": "password"
}
```

**요청 본문**
- `userId` (필수): 로그인 ID
- `password` (필수): 비밀번호

**응답**
- **성공 (200 OK)**
```json
{
  "id": 1,
  "userId": "admin",
  "username": "관리자",
  "role": "ADMIN"
}
```

> **참고**: 로그인 성공 시 서버가 세션을 생성하고 쿠키(세션 ID)를 자동으로 전송합니다. 이후 모든 API 요청 시 브라우저가 자동으로 쿠키를 전송합니다.

- **실패 (401 Unauthorized)** - 인증 실패
```json
{
  "timestamp": "2026-03-06T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Bad credentials",
  "path": "/api/auth/login"
}
```

### 현재 사용자 정보 조회

현재 로그인한 사용자의 정보를 조회합니다.

**요청**
```
GET /api/auth/me
```

**응답**
- **성공 (200 OK)**
```json
{
  "userId": "admin",
  "username": "관리자",
  "role": "ADMIN"
}
```

> **참고**: 
> - `userId`: 로그인 시 사용하는 고유 ID (필수)
> - `username`: 화면에 표시되는 사용자명 (필수)

- **실패 (401 Unauthorized)** - 인증되지 않은 사용자
```json
{
  "message": "인증되지 않은 사용자입니다."
}
```

### UserID 중복 확인

회원가입 시 UserID의 중복 여부를 확인합니다.

**요청**
```
GET /api/auth/check-userid/{userid}
```

**경로 변수**
- `userid`: 확인할 UserID (경로 변수명은 소문자)

**응답**
- **성공 (200 OK)**
```json
{
  "exists": false,
  "available": true
}
```

### 로그아웃

현재 세션을 무효화하여 로그아웃합니다.

**요청**
```
POST /api/auth/logout
```

**응답**
- **성공 (200 OK)**
```json
{
  "message": "로그아웃되었습니다."
}
```

## 콘텐츠 API

모든 콘텐츠 API는 인증이 필요합니다. 세션 기반 인증이므로 로그인 후 쿠키(세션 ID)가 자동으로 전송됩니다.

### 콘텐츠 목록 조회 (페이징)

콘텐츠 목록을 페이징하여 조회합니다.

**요청**
```
GET /api/contents?page=0&size=10&sort=createdDate,desc
```

**쿼리 파라미터**
- `page`: 페이지 번호 (0부터 시작, 기본값: 0)
- `size`: 페이지 크기 (기본값: 10)
- `sort`: 정렬 기준 (예: `createdDate,desc`)

**응답**
- **성공 (200 OK)**
```json
{
  "content": [
    {
      "id": 1,
      "title": "제목",
      "description": "내용",
      "viewCount": 10,
      "createdDate": "2026-03-06T10:00:00",
      "createdBy": "관리자",
      "lastModifiedDate": "2026-03-06T11:00:00",
      "lastModifiedBy": "관리자"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 10,
    "sort": {
      "sorted": true,
      "unsorted": false
    }
  },
  "totalElements": 1,
  "totalPages": 1,
  "last": true,
  "first": true,
  "numberOfElements": 1,
  "size": 10,
  "number": 0
}
```

### 콘텐츠 상세 조회

특정 콘텐츠의 상세 정보를 조회합니다.

**요청**
```
GET /api/contents/{id}
```

**경로 변수**
- `id`: 콘텐츠 ID

**응답**
- **성공 (200 OK)**
```json
{
  "id": 1,
  "title": "제목",
  "description": "내용",
  "viewCount": 11,
  "createdDate": "2026-03-06T10:00:00",
  "createdBy": "관리자",
  "lastModifiedDate": "2026-03-06T11:00:00",
  "lastModifiedBy": "관리자"
}
```

> **참고**: 상세 조회 시 조회수가 자동으로 증가합니다.

- **실패 (404 Not Found)** - 콘텐츠를 찾을 수 없음
```json
{
  "timestamp": "2026-03-06T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Content not found with id: 999",
  "path": "/api/contents"
}
```

### 콘텐츠 생성

새로운 콘텐츠를 생성합니다.

**요청**
```
POST /api/contents
Content-Type: application/json

{
  "title": "제목",
  "description": "내용"
}
```

**요청 본문**
- `title` (필수): 콘텐츠 제목 (최대 100자)
- `description` (선택): 콘텐츠 내용
- `viewCount` (선택): 조회수 (지정하지 않으면 자동으로 0으로 설정됨)

**응답**
- **성공 (201 Created)**
```json
{
  "id": 1,
  "title": "제목",
  "description": "내용",
  "viewCount": 0,
  "createdDate": "2026-03-06T10:00:00",
  "createdBy": "관리자",
  "lastModifiedDate": null,
  "lastModifiedBy": null
}
```

> **참고**: 
> - `createdBy`는 서버에서 현재 로그인한 사용자의 **Username(사용자명)**으로 자동 설정됩니다. UserID가 아닌 Username이 저장됩니다.
> - `viewCount`를 요청 본문에 포함하지 않으면 자동으로 0으로 설정됩니다.

- **실패 (400 Bad Request)** - Validation 실패
```json
{
  "timestamp": "2026-03-06T10:30:00",
  "status": 400,
  "error": "Validation Failed",
  "fieldErrors": {
    "title": "Title is required"
  }
}
```

- **실패 (401 Unauthorized)** - 인증되지 않은 사용자
```json
{
  "timestamp": "2026-03-06T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource",
  "path": "/api/contents"
}
```

### 콘텐츠 수정

기존 콘텐츠를 수정합니다.

**요청**
```
PUT /api/contents/{id}
Content-Type: application/json

{
  "title": "수정된 제목",
  "description": "수정된 내용"
}
```

**경로 변수**
- `id`: 콘텐츠 ID

**요청 본문**
- `title` (필수): 콘텐츠 제목
- `description` (선택): 콘텐츠 내용

**응답**
- **성공 (200 OK)**
```json
{
  "id": 1,
  "title": "수정된 제목",
  "description": "수정된 내용",
  "viewCount": 10,
  "createdDate": "2026-03-06T10:00:00",
  "createdBy": "관리자",
  "lastModifiedDate": "2026-03-06T12:00:00",
  "lastModifiedBy": "관리자"
}
```

> **참고**: 수정 시 `lastModifiedBy` 필드에 수정한 사용자의 **Username(사용자명)**이 자동으로 기록됩니다. UserID가 아닌 Username이 저장됩니다.

- **실패 (404 Not Found)** - 콘텐츠를 찾을 수 없음
```json
{
  "timestamp": "2026-03-06T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Content not found with id: 999",
  "path": "/api/contents"
}
```

- **실패 (403 Forbidden)** - 권한 없음
```json
{
  "timestamp": "2026-03-06T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have permission to update this content",
  "path": "/api/contents"
}
```

### 콘텐츠 삭제

콘텐츠를 삭제합니다.

**요청**
```
DELETE /api/contents/{id}
```

**경로 변수**
- `id`: 콘텐츠 ID

**응답**
- **성공 (204 No Content)** - 응답 본문 없음

- **실패 (404 Not Found)** - 콘텐츠를 찾을 수 없음
```json
{
  "timestamp": "2026-03-06T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Content not found with id: 999",
  "path": "/api/contents"
}
```

- **실패 (403 Forbidden)** - 권한 없음
```json
{
  "timestamp": "2026-03-06T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "You don't have permission to delete this content",
  "path": "/api/contents"
}
```

## 권한 규칙

1. **콘텐츠 생성**: 모든 인증된 사용자 가능
2. **콘텐츠 수정/삭제**:
   - 작성자 본인만 가능
   - ADMIN 역할은 모든 콘텐츠 수정/삭제 가능
   - 권한이 없으면 403 Forbidden 응답
3. **콘텐츠 조회**: 모든 인증된 사용자 가능
4. **조회수 증가**: 상세 페이지 조회 시 자동 증가

## 에러 응답 형식

모든 에러 응답은 다음 형식을 따릅니다:

```json
{
  "timestamp": "2026-03-06T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "에러 메시지",
  "path": "/api/contents"
}
```

### HTTP 상태 코드

- `200 OK`: 요청 성공
- `201 Created`: 리소스 생성 성공
- `204 No Content`: 요청 성공 (응답 본문 없음)
- `400 Bad Request`: 잘못된 요청 (Validation 실패 등)
- `401 Unauthorized`: 인증되지 않은 사용자
- `403 Forbidden`: 권한이 없는 사용자
- `404 Not Found`: 리소스를 찾을 수 없음
- `409 Conflict`: 리소스 충돌 (예: 사용자명 중복)
- `500 Internal Server Error`: 서버 내부 오류
