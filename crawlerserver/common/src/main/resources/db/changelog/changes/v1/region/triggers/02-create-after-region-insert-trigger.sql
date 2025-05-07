--liquibase formatted sql
--changeset jungwoo_shin:create-after-region-insert-trigger endDelimiter:// dbms:mariadb

CREATE TRIGGER after_region_insert
AFTER INSERT ON regions
FOR EACH ROW
BEGIN
    IF NEW.level = 3 THEN
        INSERT IGNORE INTO migrations (cortar_no, naver_status, zigbang_status)
        VALUES (NEW.cortar_no, 'NEW', 'NEW');
    END IF;

    INSERT IGNORE INTO crawls (cortar_no, naver_status, zigbang_status)
    VALUES (NEW.cortar_no, 'NEW', 'NEW');
END//