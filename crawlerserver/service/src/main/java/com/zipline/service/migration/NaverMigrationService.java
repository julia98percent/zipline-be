package com.zipline.service.migration;

import com.zipline.service.task.dto.TaskResponseDto;
import com.zipline.service.task.enums.TaskType;

public interface NaverMigrationService {
	TaskResponseDto startFullMigration();
	TaskResponseDto migrateRegion(Long cortarNo);
	TaskResponseDto getTaskStatus(TaskType TaskType);
}
