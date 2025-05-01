package com.zipline.service.migaration;

import com.zipline.service.migaration.dto.MigrationStatisticsDTO;
import com.zipline.domain.entity.naver.NaverRawArticle;

public interface NaverRawArticleMigrationService {

	void NaverMigration();

	void migrateAllArticlesForRegion(Long cortarNo);

	void migrateRawArticlesForRegion(Long cortarNo);

	void migrateRawArticle(NaverRawArticle rawArticle);

	void retryFailedMigrations();

	void retryFailedMigrationsForRegion(Long cortarNo);

	int resetMigrationStatusForRegion(Long cortarNo);

	int resetAndMigrateRegion(Long cortarNo);

	MigrationStatisticsDTO getMigrationStatistics();

	MigrationStatisticsDTO getMigrationStatisticsForRegion(Long cortarNo);
}
