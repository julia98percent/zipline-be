package com.zipline.service.publicitem;

import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.publicitem.dto.PropertyArticlePageResponseDTO;
import com.zipline.service.publicitem.dto.PropertyArticleSearchDTO;
import io.micrometer.core.annotation.Timed;


public interface PropertyArticleViewService {

  @Timed
  PropertyArticlePageResponseDTO searchPropertyArticles(PageRequestDTO pageRequestDTO,
      PropertyArticleSearchDTO searchDTO);

  @Timed
  PropertyArticlePageResponseDTO getPropertyArticlesByRegion(PageRequestDTO pageRequestDTO,
      String regionCode);
}