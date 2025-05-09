
USE ziplinedb;

SET SQL_SAFE_UPDATES = 0; 

UPDATE crawls
SET naver_status = 'NEW',
    naver_last_crawled_at = NULL;

DELETE FROM naver_raw_articles;

COMMIT;