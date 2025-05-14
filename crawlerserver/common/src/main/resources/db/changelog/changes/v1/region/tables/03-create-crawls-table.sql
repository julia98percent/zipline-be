--liquibase formatted sql
--changeset jungwoo_shin:create-crawls-table dbms:mariadb

CREATE TABLE IF NOT EXISTS crawls (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cortar_no BIGINT NOT NULL UNIQUE,
    naver_status VARCHAR(20),
    naver_last_crawled_at DATETIME,
    error_log LONGTEXT
);