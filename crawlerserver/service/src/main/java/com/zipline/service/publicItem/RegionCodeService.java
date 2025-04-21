package com.zipline.service.publicItem;

import java.util.List;

import com.zipline.domain.dto.publicitem.RegionDTO;

public interface RegionCodeService {

	/**
	 * 네이버 부동산 API에서 지역 정보를 계층적으로 수집하고 저장합니다.
	 * 레벨 1 (시/도), 레벨 2 (시/군/구), 레벨 3 (읍/면/동)
	 */
	void crawlAndSaveRegions();

	/**
	 * JSON 응답을 파싱하여 RegionDTO 리스트로 변환합니다.
	 *
	 * @param jsonResponse JSON 문자열
	 * @return 파싱된 RegionDTO 리스트
	 */
	List<RegionDTO> parseRegions(String jsonResponse);
}
