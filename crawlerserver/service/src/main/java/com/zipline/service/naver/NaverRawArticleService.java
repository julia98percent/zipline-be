package com.zipline.service.naver;

import com.zipline.service.task.dto.TaskResponseDto;

public interface NaverRawArticleService {
	TaskResponseDto crawlAndSaveRawArticles(Boolean useProxy);
	TaskResponseDto crawlAndSaveRawArticlesForRegion(Boolean useProxy, Long cortarNo);
}
