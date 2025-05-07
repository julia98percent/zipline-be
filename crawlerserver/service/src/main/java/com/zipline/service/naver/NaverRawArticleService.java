package com.zipline.service.naver;

import com.zipline.service.task.dto.TaskResponseDto;

public interface NaverRawArticleService {

	/**
	 * 모든 지역에 대한 원본 매물 정보를 수집합니다.
	 */
	TaskResponseDto crawlAndSaveRawArticles(Boolean useProxy);

	/**
	 * 특정 지역의 원본 매물 정보를 수집하고 저장합니다.
	 *
	 * @param cortarNo 지역 코드
	 */
	TaskResponseDto crawlAndSaveRawArticlesForRegion(Boolean useProxy, Long cortarNo);
}
