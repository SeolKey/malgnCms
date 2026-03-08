-- ============================================
-- CMS Backend API - Database Schema (H2 Database)
-- ============================================
-- 데이터베이스: H2 Database
-- 파일 위치: ./data/cmsdb.mv.db
-- 
-- 실행 방법:
-- 1. 애플리케이션 실행 후 H2 콘솔 접속: http://localhost:8080/h2-console
-- 2. JDBC URL: jdbc:h2:file:./data/cmsdb
-- 3. Username: sa
-- 4. Password: (비어있음)
-- 5. 이 SQL 파일의 내용을 복사하여 실행
-- ============================================

-- 기존 테이블 삭제 (필요시)
-- DROP TABLE IF EXISTS contents;
-- DROP TABLE IF EXISTS users;

-- ============================================
-- USERS 테이블 생성
-- 사용자 정보를 저장하는 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    userid VARCHAR(50) NOT NULL,
    username VARCHAR(50) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    CONSTRAINT uk_users_userid UNIQUE (userid),
    CONSTRAINT uk_users_username UNIQUE (username)
);

-- ============================================
-- CONTENTS 테이블 생성
-- 콘텐츠 정보를 저장하는 테이블
-- ============================================
CREATE TABLE IF NOT EXISTS contents (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    description TEXT,
    view_count BIGINT NOT NULL DEFAULT 0,
    created_date TIMESTAMP,
    created_by VARCHAR(50) NOT NULL,
    last_modified_date TIMESTAMP,
    last_modified_by VARCHAR(50),
    CONSTRAINT fk_contents_created_by FOREIGN KEY (created_by) REFERENCES users(username),
    CONSTRAINT fk_contents_last_modified_by FOREIGN KEY (last_modified_by) REFERENCES users(username)
);

-- ============================================
-- 인덱스 생성
-- ============================================
CREATE INDEX IF NOT EXISTS idx_contents_created_date ON contents(created_date DESC);
CREATE INDEX IF NOT EXISTS idx_contents_created_by ON contents(created_by);
CREATE INDEX IF NOT EXISTS idx_users_userid ON users(userid);
CREATE INDEX IF NOT EXISTS idx_users_username ON users(username);

-- ============================================
-- 스키마 확인 쿼리
-- ============================================
-- 테이블 목록 확인
-- SHOW TABLES;

-- users 테이블 구조 확인
-- SHOW COLUMNS FROM users;

-- contents 테이블 구조 확인
-- SHOW COLUMNS FROM contents;
