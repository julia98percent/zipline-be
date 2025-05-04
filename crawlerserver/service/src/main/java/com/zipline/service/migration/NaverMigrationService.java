package com.zipline.service.migration;

import com.zipline.global.task.dto.TaskResponseDto;
import com.zipline.global.task.enums.TaskType;
import com.zipline.service.migration.dto.MigrationStatisticsDTO;

public interface NaverMigrationService {
	TaskResponseDto startFullMigration();
	TaskResponseDto migrateRegion(Long cortarNo);
	TaskResponseDto getTaskStatus(TaskType TaskType);
	//MigrationStatisticsDTO getMigrationStatistics();
	//MigrationStatisticsDTO getMigrationStatisticsForRegion(Long cortarNo);
}
