package com.zipline.service.task;

import com.zipline.service.task.enums.TaskType;

public interface TaskManager {
    boolean isTaskRunning(TaskType taskType);

    <T> Task<T> createTask(TaskType taskType, T target);

    Task createTask(TaskType taskType);

    Task getTaskByType(TaskType taskType);

    void removeTask(TaskType taskType);
}