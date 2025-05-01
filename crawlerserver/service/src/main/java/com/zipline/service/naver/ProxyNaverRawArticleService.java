package com.zipline.service.naver;

public interface ProxyNaverRawArticleService {

	/**
	 * 특정 레벨의 모든 지역에 대한 원본 매물 정보를 수집합니다.
	 *
	 * @param level 지역 레벨
	 */
	void crawlAndSaveRawArticlesByLevel(int level);

	/**
	 * 특정 지역의 원본 매물 정보를 수집하고 저장합니다. (지역 코드 기준)
	 *
	 * @param cortarNo 지역 코드
	 */
	void crawlAndSaveRawArticlesForRegion(Long cortarNo);
}
