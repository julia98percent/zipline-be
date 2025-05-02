package com.zipline.service.migration;

import com.zipline.domain.entity.naver.NaverRawArticle;
import com.zipline.global.task.dto.TaskResponseDto;
import com.zipline.service.migration.dto.MigrationStatisticsDTO;

public interface MigrationService {

	void NaverMigration();

	void migrateAllArticlesForRegion(Long cortarNo);

	void migrateRawArticlesForRegion(Long cortarNo);

	void migrateRawArticle(NaverRawArticle rawArticle);

	int resetMigrationStatusForRegion(Long cortarNo);

	int resetAndMigrateRegion(Long cortarNo);

	MigrationStatisticsDTO getMigrationStatistics();

	MigrationStatisticsDTO getMigrationStatisticsForRegion(Long cortarNo);

	TaskResponseDto startNaverMigration();

	TaskResponseDto migrateRegion(Long regionId);

	TaskResponseDto retryFailedMigrations();

	TaskResponseDto getTaskStatus(String taskId);
}
