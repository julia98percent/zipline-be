package com.zipline.infrastructure.zigbang;

import com.zipline.domain.entity.zigbang.ZigBangArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ZigBangArticleRepository extends JpaRepository<ZigBangArticle, Long> {
}
