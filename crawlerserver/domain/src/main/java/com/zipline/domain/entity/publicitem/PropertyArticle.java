package com.zipline.domain.entity.publicitem;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.zipline.domain.entity.enums.Category;
import com.zipline.domain.entity.enums.Platform;

import com.zipline.domain.entity.region.Region;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "property_articles")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PropertyArticle {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "article_id")
    private String articleId;

    @Column(name = "region_code")
    private String regionCode;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(name = "building_name")
    private String buildingName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "building_type")
    private String buildingType;

    @Column(name = "price")
    private Long price;

    @Column(name = "deposit")
    private Long deposit;

    @Column(name = "monthly_rent")
    private Long monthlyRent;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "supply_area")
    private Double supplyArea;

    @Column(name = "exclusive_area")
    private Double exclusiveArea;

    @Column(name = "platform")
    @Enumerated(EnumType.STRING)
    private Platform platform;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 네이버 매물 JSON 데이터로부터 PropertyArticle 객체를 생성합니다.
     * 
     * @param articleNode 네이버 매물 JSON 노드
     * @param region 지역 정보
     * @return 생성된 PropertyArticle 객체
     */
    public static PropertyArticle createFromNaverArticle(JsonNode articleNode, Region region) {
        // 거래 유형에 따른 가격 정보와 카테고리 설정
        PriceInfo priceInfo = extractPriceInfo(articleNode);
        return PropertyArticle.builder()
            .articleId(articleNode.path("atclNo").asText())
            .regionCode(region.getCortarNo().toString())
            .category(priceInfo.category)
            .buildingName(articleNode.path("atclNm").asText())
            .description(articleNode.path("atclFetrDesc").asText())
            .buildingType(articleNode.path("rletTpNm").asText())
            .price(priceInfo.price)
            .deposit(priceInfo.deposit)
            .monthlyRent(priceInfo.monthlyRent)
            .longitude(parseDoubleOrNull(articleNode.path("lng").asText()))
            .latitude(parseDoubleOrNull(articleNode.path("lat").asText()))
            .supplyArea(parseDoubleOrNull(articleNode.path("spc1").asText()))
            .exclusiveArea(parseDoubleOrNull(articleNode.path("spc2").asText()))
            .platform(Platform.NAVER)
            .createdAt(LocalDateTime.now())
            .updatedAt(LocalDateTime.now())
            .build();
    }

    /**
     * 네이버 매물 JSON 데이터로부터 PropertyArticle 객체를 생성합니다.
     */
    public static PropertyArticle createFromNaverRawArticle(JsonNode articleNode, String regionCode) {
        String articleId = articleNode.path("atclNo").asText();

        PriceInfo priceInfo = extractPriceInfo(articleNode);
        
        PropertyArticleBuilder builder = PropertyArticle.builder()
            .articleId(articleId)
            .regionCode(regionCode)
            .category(priceInfo.category)
            .buildingName(articleNode.path("atclNm").asText())
            .description(articleNode.path("atclFetrDesc").asText())
            .buildingType(articleNode.path("rletTpNm").asText())
            .price(priceInfo.price)
            .deposit(priceInfo.deposit)
            .monthlyRent(priceInfo.monthlyRent)
            .longitude(parseDoubleOrNull(articleNode.path("lng").asText()))
            .latitude(parseDoubleOrNull(articleNode.path("lat").asText()))
            .supplyArea(parseDoubleOrNull(articleNode.path("spc1").asText()))
            .exclusiveArea(parseDoubleOrNull(articleNode.path("spc2").asText()))
            .platform(Platform.NAVER);
        builder.updatedAt(LocalDateTime.now());
        
        return builder.build();
    }

    /**
     * 거래 유형에 따른 가격 정보와 카테고리를 추출합니다.
     * 
     * @param articleNode 네이버 매물 JSON 노드
     * @return 가격 정보 객체
     */
    private static PriceInfo extractPriceInfo(JsonNode articleNode) {
        String tradTpNm = articleNode.path("tradTpNm").asText();
        Category category;
        Long price = 0L;
        Long deposit = 0L;
        Long monthlyRent = 0L;
        
        // 거래 유형에 따른 가격 정보 설정
        switch (tradTpNm) {
            case "매매":
                category = Category.SALE;
                price = parseLongOrZero(articleNode.path("prc").asText());
                break;
            case "전세":
                category = Category.DEPOSIT;
                deposit = parseLongOrZero(articleNode.path("prc").asText());
                break;
            case "월세":
                category = Category.MONTHLY;
                deposit = parseLongOrZero(articleNode.path("prc").asText());
                monthlyRent = parseLongOrZero(articleNode.path("rentPrc").asText());
                break;
            default:
                category = Category.SALE;
                break;
        }
        
        return new PriceInfo(category, price, deposit, monthlyRent);
    }

    /**
     * 문자열을 Double로 변환합니다. 변환 실패 시 null을 반환합니다.
     */
    private static Double parseDoubleOrNull(String value) {
        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /**
     * 문자열을 Long으로 변환합니다. 변환 실패 시 0을 반환합니다.
     */
    private static Long parseLongOrZero(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            return 0L;
        }
    }

      /**
     * 가격 정보를 담는 내부 클래스
     */
    private static class PriceInfo {
        final Category category;
        final Long price;
        final Long deposit;
        final Long monthlyRent;
        
        PriceInfo(Category category, Long price, Long deposit, Long monthlyRent) {
            this.category = category;
            this.price = price;
            this.deposit = deposit;
            this.monthlyRent = monthlyRent;
        }
    }
}
