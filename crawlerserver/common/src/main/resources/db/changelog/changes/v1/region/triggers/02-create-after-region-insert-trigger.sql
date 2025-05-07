--liquibase formatted sql
--changeset jungwoo_shin:create-after-region-insert-trigger endDelimiter:// dbms:mariadb

CREATE TRIGGER after_region_insert
AFTER INSERT ON regions
FOR EACH ROW
BEGIN
    DECLARE migration_exists INT DEFAULT 0;
    IF NEW.level = 3 THEN
        SELECT COUNT(*) INTO migration_exists
        FROM migrations
        WHERE cortar_no = NEW.cortar_no;

        IF migration_exists = 0 THEN
            INSERT INTO migrations (
                cortar_no,
                naver_status,
                zigbang_status
            ) VALUES (
                NEW.cortar_no,
                'NEW',
                'NEW'
            );
        END IF;
    END IF;
END//