package com.zipline.repository.publicItem;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.zipline.entity.publicItem.PropertyArticle;

@Repository
public interface PropertyArticleRepository extends JpaRepository<PropertyArticle, Long> {
    List<PropertyArticle> findByRegionCode(String regionCode);
    Optional<PropertyArticle> findByArticleId(String articleId);
} 
