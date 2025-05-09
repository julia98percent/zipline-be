-- JSON 추출 함수
DELIMITER //

CREATE FUNCTION GetJsonValue(json_data TEXT, json_path VARCHAR(255))
RETURNS TEXT DETERMINISTIC
BEGIN
    DECLARE result TEXT;
    -- 문자열 값에서 따옴표를 제거하기 위해 JSON_UNQUOTE 사용
    SET result = JSON_UNQUOTE(JSON_EXTRACT(json_data, json_path));

    -- 존재하지 않는 경로에 대해 NULL대신 빈문자열 반환
    RETURN COALESCE(result, '');
END //


-- 숫자 값을 안전하게 파싱하는 함수
CREATE FUNCTION SafeToDouble(str VARCHAR(255))
RETURNS DOUBLE DETERMINISTIC
BEGIN
    DECLARE result DOUBLE DEFAULT NULL;
    IF str REGEXP '^[0-9]+(\.[0-9]+)?$' THEN
        SET result = CAST(str AS DOUBLE);
    END IF;

    RETURN result;
END //

CREATE FUNCTION SafeToLong(str VARCHAR(255))
RETURNS BIGINT DETERMINISTIC
BEGIN
    DECLARE result BIGINT DEFAULT 0;

    IF str REGEXP '^[0-9]+$' THEN
        SET result = CAST(str AS SIGNED);
    END IF;

    RETURN result;
END //

DELIMITER ;

-- 단일 매물 마이그레이션 함수
DELIMITER //

CREATE PROCEDURE MigrateNaverRawArticle(IN article_id BIGINT)
BEGIN
    DECLARE raw_data TEXT;
    DECLARE article_no VARCHAR(255);
    DECLARE region_code VARCHAR(255);
    DECLARE trad_tp_nm VARCHAR(50);
    DECLARE category_val VARCHAR(20);
    DECLARE price_val BIGINT DEFAULT 0;
    DECLARE deposit_val BIGINT DEFAULT 0;
    DECLARE monthly_rent_val BIGINT DEFAULT 0;
    DECLARE error_msg TEXT;
    DECLARE cortar_no_val BIGINT;

    -- 매물이 존재여부 확인
    DECLARE article_exists INT DEFAULT 0;

    -- 예외처리
    DECLARE CONTINUE HANDLER FOR SQLEXCEPTION
    BEGIN
        GET DIAGNOSTICS CONDITION 1 error_msg = MESSAGE_TEXT;
        -- 에러로그 기록
        UPDATE naver_raw_articles
        SET migration_status = 'FAILED',
            migration_error = error_msg,
            migrated_at = NOW()
        WHERE id = article_id;
    END;

    -- 로우 데이터 셋
    SELECT raw_data, article_id, cortar_no INTO raw_data, article_no, cortar_no_val
    FROM naver_raw_articles
    WHERE id = article_id;

    -- 지역 코드 셋
    SET region_code = CAST(cortar_no_val AS CHAR);

    -- 추출타입 셋 카테고리 셋
    SET trad_tp_nm = GetJsonValue(raw_data, '$.tradTpNm');

    CASE trad_tp_nm
        WHEN '매매' THEN
            SET category_val = 'SALE';
            SET price_val = SafeToLong(GetJsonValue(raw_data, '$.prc'));
            SET deposit_val = NULL;
            SET monthly_rent_val = NULL;
        WHEN '전세' THEN
            SET category_val = 'DEPOSIT';
            SET price_val = NULL;
            SET deposit_val = SafeToLong(GetJsonValue(raw_data, '$.prc'));
            SET monthly_rent_val = NULL;
        WHEN '월세' THEN
            SET category_val = 'MONTHLY';
            SET price_val = NULL;
            SET deposit_val = SafeToLong(GetJsonValue(raw_data, '$.prc'));
            SET monthly_rent_val = SafeToLong(GetJsonValue(raw_data, '$.rentPrc'));
        ELSE
            SET category_val = 'SALE';
            SET price_val = 0;
            SET deposit_val = NULL;
            SET monthly_rent_val = NULL;
    END CASE;

    --이미 존재하는지 체크
    SELECT COUNT(*) INTO article_exists
    FROM property_articles
    WHERE article_id = article_no;

    IF article_exists > 0 THEN
        -- 기존기록 업데이트
        UPDATE property_articles
        SET region_code = region_code,
            category = category_val,
            building_name = GetJsonValue(raw_data, '$.atclNm'),
            description = GetJsonValue(raw_data, '$.atclFetrDesc'),
            building_type = GetJsonValue(raw_data, '$.rletTpNm'),
            price = price_val,
            deposit = deposit_val,
            monthly_rent = monthly_rent_val,
            longitude = SafeToDouble(GetJsonValue(raw_data, '$.lng')),
            latitude = SafeToDouble(GetJsonValue(raw_data, '$.lat')),
            supply_area = SafeToDouble(GetJsonValue(raw_data, '$.spc1')),
            exclusive_area = SafeToDouble(GetJsonValue(raw_data, '$.spc2')),
            platform = 'NAVER',
            updated_at = NOW()
        WHERE article_id = article_no;
    ELSE
        -- 새기록 삽입
        INSERT INTO property_articles (
            article_id,
            region_code,
            category,
            building_name,
            description,
            building_type,
            price,
            deposit,
            monthly_rent,
            longitude,
            latitude,
            supply_area,
            exclusive_area,
            platform,
            platform_url,
            created_at,
            updated_at
        ) VALUES (
            article_no,
            region_code,
            category_val,
            GetJsonValue(raw_data, '$.atclNm'),
            GetJsonValue(raw_data, '$.atclFetrDesc'),
            GetJsonValue(raw_data, '$.rletTpNm'),
            price_val,
            deposit_val,
            monthly_rent_val,
            SafeToDouble(GetJsonValue(raw_data, '$.lng')),
            SafeToDouble(GetJsonValue(raw_data, '$.lat')),
            SafeToDouble(GetJsonValue(raw_data, '$.spc1')),
            SafeToDouble(GetJsonValue(raw_data, '$.spc2')),
            'NAVER',
            NOW(),
            NOW()
        );
    END IF;

    -- 완료로 표시
    UPDATE naver_raw_articles
    SET migration_status = 'COMPLETED',
        migration_error = NULL,
        migrated_at = NOW()
    WHERE id = article_id;
END //

DELIMITER ;

-- 인서트 트리거
DELIMITER //
CREATE TRIGGER after_naver_raw_article_insert
AFTER INSERT ON naver_raw_articles
FOR EACH ROW
BEGIN
    -- PENDING 상태이거나 NULL인 경우에만 마이그레이션 호출
    IF NEW.migration_status = 'PENDING' OR NEW.migration_status IS NULL THEN
        CALL MigrateNaverRawArticle(NEW.id);
    END IF;
END //

-- 업데이트 트리거
CREATE TRIGGER after_naver_raw_article_update
AFTER UPDATE ON naver_raw_articles
FOR EACH ROW
BEGIN
    -- 펜딩으로 변경시 트리거
    IF NEW.migration_status = 'PENDING' AND (OLD.migration_status != 'PENDING' OR OLD.migration_status IS NULL) THEN
        CALL MigrateNaverRawArticle(NEW.id);
    END IF;
END //

DELIMITER ;

-- 수동 트리거 함수
DELIMITER //

CREATE PROCEDURE MigrateAllPendingArticles()
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE article_id BIGINT;
    DECLARE cur CURSOR FOR SELECT id FROM naver_raw_articles WHERE migration_status = 'PENDING';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO article_id;
        IF done THEN
            LEAVE read_loop;
        END IF;

        CALL MigrateNaverRawArticle(article_id);

        -- 데이터베이스 과부하 방지를 위한 지연
        DO SLEEP(0.01);
    END LOOP;

    CLOSE cur;
END //

-- 지역 코드 기반으로 마이그레이션 하는 함수
CREATE PROCEDURE MigrateArticlesByRegion(IN region_code BIGINT)
BEGIN
    DECLARE done INT DEFAULT FALSE;
    DECLARE article_id BIGINT;
    DECLARE cur CURSOR FOR SELECT id FROM naver_raw_articles
                         WHERE cortar_no = region_code
                           AND migration_status = 'PENDING';
    DECLARE CONTINUE HANDLER FOR NOT FOUND SET done = TRUE;

    OPEN cur;

    read_loop: LOOP
        FETCH cur INTO article_id;
        IF done THEN
            LEAVE read_loop;
        END IF;

        CALL MigrateNaverRawArticle(article_id);

        -- 과부하 방지를 위한 지연
        DO SLEEP(0.01);
    END LOOP;

    CLOSE cur;
END //

-- 실패 재실행 프로시져
CREATE PROCEDURE RetryFailedMigrations()
BEGIN
    -- 실패한 함수들 펜딩으로 변경
    UPDATE naver_raw_articles
    SET migration_status = 'PENDING',
        migration_error = NULL,
        migrated_at = NULL
    WHERE migration_status = 'FAILED'

    -- 재실행
    CALL MigrateAllPendingArticles();
END //

DELIMITER ;

-- 상태확인 뷰
CREATE OR REPLACE VIEW migration_statistics AS
SELECT
    COUNT(*) as total_count,
    SUM(CASE WHEN migration_status = 'PENDING' THEN 1 ELSE 0 END) as pending_count,
    SUM(CASE WHEN migration_status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_count,
    SUM(CASE WHEN migration_status = 'FAILED' THEN 1 ELSE 0 END) as failed_count
FROM naver_raw_articles;

-- 지역별 상태 확인 뷰
CREATE OR REPLACE VIEW migration_statistics_by_region AS
SELECT
    cortar_no,
    COUNT(*) as total_count,
    SUM(CASE WHEN migration_status = 'PENDING' THEN 1 ELSE 0 END) as pending_count,
    SUM(CASE WHEN migration_status = 'COMPLETED' THEN 1 ELSE 0 END) as completed_count,
    SUM(CASE WHEN migration_status = 'FAILED' THEN 1 ELSE 0 END) as failed_count
FROM naver_raw_articles
GROUP BY cortar_no;
