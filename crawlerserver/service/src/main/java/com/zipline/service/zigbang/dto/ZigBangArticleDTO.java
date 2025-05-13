package com.zipline.service.zigbang.dto;

import com.fasterxml.jackson.databind.JsonNode;
import com.zipline.domain.entity.enums.MigrationStatus;
import com.zipline.domain.entity.zigbang.ZigBangArticle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 네이버 원본 매물 정보 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ZigBangArticleDTO {
    private String articleId;
    private String geohash;
    private String rawData;
    private MigrationStatus migrationStatus;
    
    /**
     * JsonNode에서 DTO를 생성합니다.
     */
    public static ZigBangArticleDTO fromJsonNode(JsonNode node, String geohash) {
        return ZigBangArticleDTO.builder()
            .articleId(node.path("atclNo").asText())
            .geohash(geohash)
            .rawData(node.toString())
            .migrationStatus(MigrationStatus.PENDING)
            .build();
    }
    
    /**
     * DTO를 엔티티로 변환합니다.
     */
    public ZigBangArticle toEntity() {
        return ZigBangArticle.builder()
            .articleId(this.articleId)
            .geohash(this.geohash)
            .rawData(this.rawData)
            .createdAt(LocalDateTime.now())
            .migrationStatus(this.migrationStatus)
            .build();
    }
}
