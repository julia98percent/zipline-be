--liquibase formatted sql
--changeset jungwoo_shin:create-migrations-table dbms:mariadb

CREATE TABLE IF NOT EXISTS migrations (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    cortar_no BIGINT NOT NULL UNIQUE,
    naver_status VARCHAR(20),
    naver_last_migrated_at DATETIME,
    zigbang_status VARCHAR(20),
    zigbang_last_migrated_at DATETIME
);