package com.zipline.service.publicItem;

import com.zipline.domain.dto.publicitem.MigrationStatisticsDTO;
import com.zipline.domain.entity.publicitem.NaverRawArticle;

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
