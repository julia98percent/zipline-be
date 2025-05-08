## 문제 상황

Spring Boot 애플리케이션 실행 시 다음과 같은 오류가 발생했습니다:

```
Failed to instantiate [com.zipline.service.naver.crawler.NaverArticleCrawler]: No default constructor found
```

이 오류는 `NaverArticleCrawler` 클래스에 기본 생성자가 없고,  
Lombok의 `@RequiredArgsConstructor`, `@AllArgsConstructor`로만 생성자를 정의했기 때문입니다.

특히 **Spring은 리플렉션을 사용해 빈을 생성**할 때,  
명시적으로 선언된 생성자를 찾지 못하면 실패합니다.

---

## 선택한 해결 방식

> 👉 명시적인 생성자 정의 방식을 채택하여 문제를 해결함

```java
// NaverArticleCrawler.java
public class NaverArticleCrawler {

    private final ObjectMapper objectMapper;
    private final RegionRepository regionRepo;
    private final NaverRawArticleRepository articleRepo;
    private final CrawlRepository crawlRepo;

    @Value("${crawler.recent-days:14}")
    private int recentDays;

    @Value("${crawler.page-size:100}")
    private int pageSize;

    @Value("${crawler.max-retry-count:10}")
    private int maxRetryCount;

    // 명시적 생성자 추가
    public NaverArticleCrawler(
            ObjectMapper objectMapper,
            RegionRepository regionRepo,
            NaverRawArticleRepository articleRepo,
            CrawlRepository crawlRepo) {
        this.objectMapper = objectMapper;
        this.regionRepo = regionRepo;
        this.articleRepo = articleRepo;
        this.crawlRepo = crawlRepo;
    }

    // ...
}
```

```java
// ParallelNaverArticleCrawler.java
public class ParallelNaverArticleCrawler extends NaverArticleCrawler {

    public ParallelNaverArticleCrawler(
            ObjectMapper objectMapper,
            RegionRepository regionRepo,
            NaverRawArticleRepository articleRepo,
            CrawlRepository crawlRepo) {
        super(objectMapper, crawlRepo, regionRepo, articleRepo);
    }

    // ...
}
```

---

## 수정 내용 요약

| 항목 | 설명 |
|------|------|
| 수정 파일 | `NaverArticleCrawler.java` |
| 수정 사항 | Lombok 기반 생성자 제거 후 직접 생성자 정의 |
| 추가된 생성자 매개변수 | `ObjectMapper`, `RegionRepository`, `NaverRawArticleRepository`, `CrawlRepository` |
| 자식 클래스 처리 | `ParallelNaverArticleCrawler`에서 부모 생성자 순서 조정 |

---

## 트러블슈팅 과정

### 1. 문제 확인

- Spring이 `NaverArticleCrawler` 빈 생성 실패
- 원인: 기본 생성자 또는 명확한 주입 생성자 없음

### 2. 분석

- `@RequiredArgsConstructor`, `@AllArgsConstructor`는 컴파일 시점에만 생성자 제공
- Spring은 런타임에 리플렉션으로 생성자 탐색 시 다음 중 하나 필요:
  - 기본 생성자
  - `@Autowired`가 붙은 생성자
  - 명확히 정의된 생성자

→ 따라서 Lombok 의존성 대신 **직접 생성자 정의가 더 안정적**

---

## 최종 검증 방법

1. **빌드 및 실행**
   - ./gradlew :crawlerserver:bootRun
   - Spring이 모든 빈을 정상적으로 등록하고 시작되는지 확인

2. **API 호출 테스트**
   - `GET /api/naver/crawling/all?useProxy=true` 호출
   - 병렬 크롤러와 일반 크롤러 모두 정상 작동 여부 확인

3. **로그 확인**
   - 각 지역별로 병렬 스레드(`Thread-ID`)가 다르게 동작하는지
   - 프록시 사용 시 `ProxyFetcher`, 비사용 시 `DefaultFetcher`가 선택되는지

---

## 결론
- 상속 관계가 있는 여러 구현체를 관리해야 할 때
- Spring이 생성자를 명확히 인식하게 하고 싶을 때 유리합니다.


---

## 관련 파일 목록

| 파일명 | 역할 |
|--------|------|
| `NaverArticleCrawler.java` | 공통 크롤링 로직 |
| `ParallelNaverArticleCrawler.java` | 병렬 크롤링 로직 |
| `CrawlerFactory.java` | 크롤러 전략 선택 |
| `build.gradle` | 컴파일 옵션 포함 여부 확인 (선택사항) |

---