package com.zipline.global.task;

// 작업 관리 인터페이스
public interface TaskManager {
    boolean isTaskRunning(String taskType);
    Task createTask(String taskType);

    void updateTaskStatus(Task task);

    Task createRegionalTask(String taskType, Long targetRegion);

    Task getTaskById(String taskId);
}