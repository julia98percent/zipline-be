package com.zipline.service.publicitem;

import com.zipline.entity.publicitem.PropertyArticle;
import com.zipline.global.exception.publicitem.PublicItemException;
import com.zipline.global.exception.publicitem.errorcode.PublicItemErrorCode;
import com.zipline.repository.publicitem.PropertyArticleViewRepository;
import com.zipline.repository.publicitem.PropertyArticleViewSpecification;
import com.zipline.service.publicitem.dto.PropertyArticlePageResponseDTO;
import com.zipline.service.publicitem.dto.PropertyArticleSearchDTO;
import com.zipline.service.publicitem.dto.PropertyArticleViewDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PropertyArticleViewServiceimpl implements PropertyArticleViewService {

	private final PropertyArticleViewRepository propertyArticleViewRepository;

	/**
	 * 매물 목록을 검색 조건에 따라 페이징하여 조회
	 *
	 * @param searchDTO 검색 조건
	 * @return 페이징된 매물 목록
	 */
	@Transactional(readOnly = true)
	public PropertyArticlePageResponseDTO searchPropertyArticles(PropertyArticleSearchDTO searchDTO) {
		try {
			// 검색 파라미터 유효성 검증
			validateSearchParameters(searchDTO);

			// 정렬 방향 설정
			Sort.Direction direction = Sort.Direction.DESC;
			if (searchDTO.getSortDirection() != null && searchDTO.getSortDirection().equalsIgnoreCase("asc")) {
				direction = Sort.Direction.ASC;
			}

			// 정렬 필드 설정 (기본값: createdAt)
			String sortBy = "createdAt";
			if (searchDTO.getSortBy() != null && !searchDTO.getSortBy().isEmpty()) {
				sortBy = searchDTO.getSortBy();
			}

			// 페이지 설정
			int page = searchDTO.getPage() != null ? searchDTO.getPage() : 0;
			int size = searchDTO.getSize() != null ? searchDTO.getSize() : 10;
			Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

			// 검색 조건 설정
			Specification<PropertyArticle> spec = buildSpecification(searchDTO);

			// 검색 실행
			log.info("매물 검색 실행: 페이지={}, 사이즈={}, 정렬={} {}", page, size, sortBy, direction);
			Page<PropertyArticle> articlePage = propertyArticleViewRepository.findAll(spec, pageable);
			log.info("매물 검색 결과: 총 {}개 항목, 총 {}페이지", articlePage.getTotalElements(), articlePage.getTotalPages());

			// DTO 변환
			Page<PropertyArticleViewDTO> dtoPage = articlePage.map(PropertyArticleViewDTO::from);
			return PropertyArticlePageResponseDTO.from(dtoPage);
		} catch (PublicItemException e) {
			log.error("매물 검색 파라미터 오류: {}", e.getMessage());
			throw new PublicItemException(PublicItemErrorCode.PUBLIC_ITEM_PARAM_ERROR);
		} catch (Exception e) {
			log.error("매물 검색 중 오류 발생: {}", e.getMessage(), e);
			throw new PublicItemException(PublicItemErrorCode.PUBLIC_ITEM_ERROR);
			//throw new RuntimeException("매물 검색 중 오류가 발생했습니다.", e);
		}
	}

	/**
	 * 검색 파라미터 유효성 검증
	 */
	private void validateSearchParameters(PropertyArticleSearchDTO searchDTO) {
		if (searchDTO.getMinPrice() != null && searchDTO.getMaxPrice() != null &&
				searchDTO.getMinPrice() > searchDTO.getMaxPrice()) {
			throw new PublicItemException(PublicItemErrorCode.INVALID_PRICE_RANGE);
		}

		if (searchDTO.getMinDeposit() != null && searchDTO.getMaxDeposit() != null &&
				searchDTO.getMinDeposit() > searchDTO.getMaxDeposit()) {
			throw new PublicItemException(PublicItemErrorCode.INVALID_DEPOSIT_RANGE);
		}

		if (searchDTO.getMinMonthlyRent() != null && searchDTO.getMaxMonthlyRent() != null &&
				searchDTO.getMinMonthlyRent() > searchDTO.getMaxMonthlyRent()) {
			throw new PublicItemException(PublicItemErrorCode.INVALID_MONTHLY_RENT_RANGE);
		}

		if (searchDTO.getMinArea() != null && searchDTO.getMaxArea() != null &&
				searchDTO.getMinArea() > searchDTO.getMaxArea()) {
			throw new PublicItemException(PublicItemErrorCode.INVALID_AREA_RANGE);
		}
	}

	/**
	 * 검색 조건 Specification 생성
	 */
	private Specification<PropertyArticle> buildSpecification(PropertyArticleSearchDTO searchDTO) {
		Specification<PropertyArticle> spec = Specification.where(null);

		if (searchDTO.getRegionCode() != null) {
			spec = spec.and(PropertyArticleViewSpecification.hasRegionCode(searchDTO.getRegionCode()));
		}

		if (searchDTO.getBuildingName() != null) {
			spec = spec.and(PropertyArticleViewSpecification.buildingNameContains(searchDTO.getBuildingName()));
		}

		if (searchDTO.getCategory() != null) {
			spec = spec.and(PropertyArticleViewSpecification.hasCategory(searchDTO.getCategory()));
		}

		if (searchDTO.getMinPrice() != null || searchDTO.getMaxPrice() != null) {
			spec = spec.and(
				PropertyArticleViewSpecification.priceBetween(searchDTO.getMinPrice(), searchDTO.getMaxPrice()));
		}

		if (searchDTO.getMinDeposit() != null || searchDTO.getMaxDeposit() != null) {
			spec = spec.and(
				PropertyArticleViewSpecification.depositBetween(searchDTO.getMinDeposit(), searchDTO.getMaxDeposit()));
		}

		if (searchDTO.getMinMonthlyRent() != null || searchDTO.getMaxMonthlyRent() != null) {
			spec = spec.and(PropertyArticleViewSpecification.monthlyRentBetween(searchDTO.getMinMonthlyRent(),
				searchDTO.getMaxMonthlyRent()));
		}

		if (searchDTO.getMinArea() != null || searchDTO.getMaxArea() != null) {
			spec = spec.and(
				PropertyArticleViewSpecification.exclusiveAreaBetween(searchDTO.getMinArea(), searchDTO.getMaxArea()));
		}

		if (searchDTO.getStartDate() != null || searchDTO.getEndDate() != null) {
			spec = spec.and(
				PropertyArticleViewSpecification.createdAtBetween(searchDTO.getStartDate(), searchDTO.getEndDate()));
		}

		if (searchDTO.getBuildingType() != null) {
			spec = spec.and((root, query, criteriaBuilder) ->
				criteriaBuilder.equal(root.get("buildingType"), searchDTO.getBuildingType()));
		}

		return spec;
	}

	/**
	 * 지역 코드로 매물 목록 조회
	 *
	 * @param regionCode 지역 코드
	 * @param pageable 페이징 정보
	 * @return 페이징된 매물 목록
	 */
	@Transactional(readOnly = true)
	public PropertyArticlePageResponseDTO getPropertyArticlesByRegion(String regionCode, Pageable pageable) {
		try {
			log.info("지역 코드 {} 매물 조회 시작", regionCode);
			Page<PropertyArticle> articlePage = propertyArticleViewRepository.findByRegionCode(regionCode, pageable);
			log.info("지역 코드 {} 매물 조회 결과: 총 {}개 항목", regionCode, articlePage.getTotalElements());

			Page<PropertyArticleViewDTO> dtoPage = articlePage.map(PropertyArticleViewDTO::from);
			return PropertyArticlePageResponseDTO.from(dtoPage);
		} catch (Exception e) {
			log.error("지역 코드 {} 매물 조회 중 오류 발생: {}", regionCode, e.getMessage(), e);
			throw new RuntimeException("지역별 매물 조회 중 오류가 발생했습니다.", e);
		}
	}
}

