package com.zipline.dto.publicItem;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.JsonNode;
import com.zipline.entity.publicItem.NaverRawArticle;
import com.zipline.entity.enums.MigrationStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 네이버 원본 매물 정보 DTO
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NaverRawArticleDTO {
    private String articleId;
    private Long cortarNo;
    private String rawData;
    private MigrationStatus migrationStatus;
    
    /**
     * JsonNode에서 DTO를 생성합니다.
     */
    public static NaverRawArticleDTO fromJsonNode(JsonNode node, Long cortarNo) {
        return NaverRawArticleDTO.builder()
            .articleId(node.path("atclNo").asText())
            .cortarNo(cortarNo)
            .rawData(node.toString())
            .migrationStatus(MigrationStatus.PENDING)
            .build();
    }
    
    /**
     * DTO를 엔티티로 변환합니다.
     */
    public NaverRawArticle toEntity() {
        return NaverRawArticle.builder()
            .articleId(this.articleId)
            .cortarNo(this.cortarNo)
            .rawData(this.rawData)
            .createdAt(LocalDateTime.now())
            .migrationStatus(this.migrationStatus)
            .build();
    }
}
