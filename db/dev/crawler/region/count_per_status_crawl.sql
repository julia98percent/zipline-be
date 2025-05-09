SELECT * FROM ziplinedb.crawls;SELECT 
    naver_status,
    COUNT(*) AS status_count
FROM 
    ziplinedb.crawls
GROUP BY 
    naver_status;