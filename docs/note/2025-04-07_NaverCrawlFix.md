# 네이버 부동산 크롤링 서비스 문제 해결 회고

## 문제
- 지역별 데이터 점진적 저장 실패  
- 서비스 레이어 `@Transactional` 충돌  
- 트랜잭션 없는 쿼리 실행 오류  

## 원인
- `REQUIRES_NEW` 속성 충돌  
- `@Modifying` 쿼리에 트랜잭션 누락  

## 해결 방안 및 장단점

1. **리포지토리 트랜잭션 추가**  
   ```java
   @Modifying
   @Transactional
   @Query("UPDATE Region r SET r.naverStatus = :status WHERE r.cortarNo = :cortarNo")
   void updateNaverStatus(@Param("cortarNo") Long cortarNo, @Param("status") CrawlStatus status);
   ```
   - 장점: 충돌 감소, 독립성 강화  
   - 단점: 연결 비용 증가, 일관성 관리 어려움  

2. **서비스 트랜잭션 제거**  
   - 장점: 코드 간결, 복잡성 감소  
   - 단점: 통합 트랜잭션 유연성 부족  


3. **로깅 강화**  
   ```java
   try {
       log.info("지역 데이터 저장 완료: {}", regionId);
   } catch (Exception e) {
       log.error("저장 중 오류 발생: {}", e.getMessage(), e);
       throw e;
   }
   ```
   - 장점: 디버깅 용이  
   - 단점: 로그 과다 시 부하  

## 결과
- 데이터 안정적 저장  
- 트랜잭션 충돌 감소  
- 오류 복원력 향상  
- 문제 파악 용이  

## 개별 트랜잭션 단점
- 성능 저하 (커넥션 비용 증가)  
- 데이터 일관성 유지 어려움  
- 설계 복잡성 증가  

## 교훈
- 트랜잭션 범위 명확화  
- `@Modifying`에 `@Transactional` 필수  
- 점진적 저장 적용  
- 예외 처리 철저히
- 시퀸스 다이어그램이등을 활용해 트랜잭션이 이루어지는 시점을 명확하고 보기좋게 정리

## 개선 방향
- 재시도 로직 강화  
- 모니터링 구축  
- 트랜잭션 최적화  
---
