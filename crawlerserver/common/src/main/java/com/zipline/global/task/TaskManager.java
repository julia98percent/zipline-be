package com.zipline.global.task;

import com.zipline.global.task.dto.ProgressDto;
import com.zipline.global.task.enums.TaskStatus;
import com.zipline.global.task.enums.TaskType;

// 작업 관리 인터페이스
public interface TaskManager {
    boolean isTaskRunning(TaskType taskType);

    Task createTask(TaskType taskType);

    void updateTaskStatus(TaskType taskType, TaskStatus status);

    Task createRegionalTask(TaskType taskType, Long targetRegion);

    Task getTaskByType(TaskType taskType);

    void removeTask(TaskType taskType);
}