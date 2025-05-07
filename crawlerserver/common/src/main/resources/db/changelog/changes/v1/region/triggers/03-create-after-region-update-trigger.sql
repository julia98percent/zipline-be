--liquibase formatted sql
--changeset jungwoo_shin:create-after-region-update-trigger endDelimiter:// dbms:mariadb

CREATE TRIGGER after_region_update
AFTER UPDATE ON regions
FOR EACH ROW
region_update:BEGIN
  IF NEW.level != 3 THEN
    IF OLD.level = 3 THEN
      DELETE FROM migrations WHERE cortar_no = OLD.cortar_no;
    END IF;
    LEAVE region_update;
  END IF;
  IF NEW.cortar_no != OLD.cortar_no THEN
    DELETE FROM migrations WHERE cortar_no = OLD.cortar_no;
    INSERT INTO migrations (cortar_no, naver_status, zigbang_status)
    VALUES (NEW.cortar_no, 'NEW', 'NEW');
  ELSEIF NEW.center_lat != OLD.center_lat OR NEW.center_lon != OLD.center_lon THEN
    UPDATE migrations
    SET naver_status = 'NEW', zigbang_status = 'NEW',
        naver_last_migrated_at = NULL, zigbang_last_migrated_at = NULL
    WHERE cortar_no = NEW.cortar_no;
  ELSEIF OLD.level != 3 AND NEW.level = 3 THEN
    INSERT INTO migrations (cortar_no, naver_status, zigbang_status)
    VALUES (NEW.cortar_no, 'NEW', 'NEW')
    ON DUPLICATE KEY UPDATE
      naver_status = 'NEW', zigbang_status = 'NEW',
      naver_last_migrated_at = NULL, zigbang_last_migrated_at = NULL;
  END IF;
END//