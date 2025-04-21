package com.zipline.infrastructure.publicItem.repository;

import com.zipline.domain.entity.publicitem.PropertyArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface PropertyArticleViewRepository extends JpaRepository<PropertyArticle, Long>, JpaSpecificationExecutor<PropertyArticle> {
    /**
     * 지역 코드로 매물 페이지 조회
     */
    Page<PropertyArticle> findByRegionCode(String regionCode, Pageable pageable);
    
    /**
     * 건물 이름으로 매물 페이지 조회
     */
    Page<PropertyArticle> findByBuildingNameContaining(String buildingName, Pageable pageable);
    
    /**
     * 지역 코드와 건물 이름으로 매물 페이지 조회
     */
    Page<PropertyArticle> findByRegionCodeAndBuildingNameContaining(String regionCode, String buildingName, Pageable pageable);
    
    /**
     * 건물 유형으로 매물 페이지 조회
     */
    Page<PropertyArticle> findByBuildingType(String buildingType, Pageable pageable);
}

