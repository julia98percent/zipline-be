package com.zipline.service.publicitem;

import com.zipline.global.request.PageRequestDTO;
import com.zipline.service.publicitem.dto.PropertyArticlePageResponseDTO;
import com.zipline.service.publicitem.dto.PropertyArticleSearchDTO;
import org.springframework.data.domain.Pageable;


public interface PropertyArticleViewService {
	PropertyArticlePageResponseDTO searchPropertyArticles(PageRequestDTO pageRequestDTO, PropertyArticleSearchDTO searchDTO);
	PropertyArticlePageResponseDTO getPropertyArticlesByRegion(PageRequestDTO pageRequestDTO, String regionCode);
}

