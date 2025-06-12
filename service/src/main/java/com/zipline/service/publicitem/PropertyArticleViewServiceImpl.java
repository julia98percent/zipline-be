package com.zipline.service.publicitem;

import com.zipline.entity.publicitem.PropertyArticle;
import com.zipline.global.exception.publicitem.PublicItemException;
import com.zipline.global.exception.publicitem.errorcode.PublicItemErrorCode;
import com.zipline.global.request.PageRequestDTO;
import com.zipline.repository.publicitem.PropertyArticleViewRepository;
import com.zipline.service.publicitem.dto.PropertyArticlePageResponseDTO;
import com.zipline.service.publicitem.dto.PropertyArticleSearchDTO;
import com.zipline.service.publicitem.dto.PropertyArticleViewDTO;
import com.zipline.service.publicitem.spec.PropertyArticleSpecificationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyArticleViewServiceImpl implements PropertyArticleViewService {

	private final PropertyArticleViewRepository propertyArticleViewRepository;
	private final PropertyArticleSpecificationBuilder specificationBuilder;

	//TODO: 추후 Region Repository 와 specificationBuilder 활용해서 부모 지역코드로도 검색 가능하게 변경
	@Override
	@Transactional(readOnly = true)
	public PropertyArticlePageResponseDTO searchPropertyArticles(PageRequestDTO pageRequestDTO, PropertyArticleSearchDTO searchDTO) {
		try {
			validateSearchParameters(searchDTO);
			Specification<PropertyArticle> spec = specificationBuilder.buildSpecification(searchDTO);
			Page<PropertyArticle> articlePage = propertyArticleViewRepository.findAll(spec, pageRequestDTO.toPageable());

			return convertToResponseDTO(articlePage);

		} catch (PublicItemException e) {
			log.error("매물 검색 파라미터 오류: {}", e.getMessage());
			throw new PublicItemException(PublicItemErrorCode.PUBLIC_ITEM_PARAM_ERROR);

		} catch (Exception e) {
			log.error("매물 검색 중 오류 발생: {}", e.getMessage(), e);
			throw new PublicItemException(PublicItemErrorCode.PUBLIC_ITEM_ERROR);
		}
	}

	@Override
	@Transactional(readOnly = true)
	public PropertyArticlePageResponseDTO getPropertyArticlesByRegion(PageRequestDTO pageRequestDTO, String regionCode) {
		try {
			Page<PropertyArticle> articlePage = propertyArticleViewRepository.findByRegionCode(regionCode, pageRequestDTO.toPageable());
			return convertToResponseDTO(articlePage);
		} catch (Exception e) {
			log.error("지역 코드 {} 매물 조회 중 오류 발생: {}", regionCode, e.getMessage(), e);
			throw new PublicItemException(PublicItemErrorCode.PUBLIC_ITEM_ERROR);
		}
	}

	private void validateSearchParameters(PropertyArticleSearchDTO searchDTO) {
		validateRange(searchDTO.getMinPrice(), searchDTO.getMaxPrice(),
				PublicItemErrorCode.INVALID_PRICE_RANGE);

		validateRange(searchDTO.getMinDeposit(), searchDTO.getMaxDeposit(),
				PublicItemErrorCode.INVALID_DEPOSIT_RANGE);

		validateRange(searchDTO.getMinMonthlyRent(), searchDTO.getMaxMonthlyRent(),
				PublicItemErrorCode.INVALID_MONTHLY_RENT_RANGE);

		validateRange(searchDTO.getMinExclusiveArea(), searchDTO.getMaxExclusiveArea(),
				PublicItemErrorCode.INVALID_EXCLUSIVE_AREA_RANGE);

    	validateRange(searchDTO.getMinSupplyArea(), searchDTO.getMaxSupplyArea(),
       			PublicItemErrorCode.INVALID_SUPPLY_AREA_RANGE);
	}

	private void validateRange(Number min, Number max, PublicItemErrorCode errorCode) {
		if (min != null && max != null && min.doubleValue() > max.doubleValue()) {
			throw new PublicItemException(errorCode);
		}
	}

	private PropertyArticlePageResponseDTO convertToResponseDTO(Page<PropertyArticle> articlePage) {
		Page<PropertyArticleViewDTO> dtoPage = articlePage.map(PropertyArticleViewDTO::from);
		return PropertyArticlePageResponseDTO.from(dtoPage);
	}
}
