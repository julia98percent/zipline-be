package com.zipline.service.naver;

import com.zipline.global.task.dto.TaskResponseDto;

public interface ProxyNaverRawArticleService {
	TaskResponseDto crawlAndSaveRawArticles();
	TaskResponseDto crawlAndSaveRawArticlesForRegion(Long cortarNo);
}