package com.zipline.service.publicItem;

import com.fasterxml.jackson.databind.JsonNode;

public interface NaverRawArticleService {

	/**
	 * 특정 레벨의 모든 지역에 대한 원본 매물 정보를 수집합니다.
	 *
	 * @param level 지역 레벨
	 */
	void crawlAndSaveRawArticlesByLevel(int level);

	/**
	 * 특정 지역의 원본 매물 정보를 수집하고 저장합니다.
	 *
	 * @param cortarNo 지역 코드
	 */
	void crawlAndSaveRawArticlesForRegion(Long cortarNo);

	/**
	 * 원본 매물 정보를 데이터베이스에 저장합니다.
	 *
	 * @param articleNode 매물 정보 JSON 노드
	 * @param cortarNo 지역 코드
	 */
	void saveRawArticle(JsonNode articleNode, Long cortarNo);
}
