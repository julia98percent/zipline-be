package com.zipline.service.zigbang;

import com.zipline.service.task.dto.TaskResponseDto;

public interface ZigBangArticleService {
	TaskResponseDto crawlAndSaveRawArticles(Boolean useProxy);
}
