package com.zipline.global.task;

import com.zipline.global.task.enums.TaskStatus;
import com.zipline.global.task.enums.TaskType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class  Task {
    private String id;
    private TaskType type;
    private TaskStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String errorMessage;
    private Long targetRegion;

    public static Task createTask(TaskType taskType, Long targetRegion) {
        return Task.builder()
                .type(taskType)
                .status(TaskStatus.RUNNING)
                .startTime(LocalDateTime.now())
                .targetRegion(targetRegion)
                .build();
    }

    public static Task createTask(TaskType taskType) {
        return Task.builder()
                .type(taskType)
                .status(TaskStatus.RUNNING)
                .startTime(LocalDateTime.now())
                .build();
    }

    public void setStatus(TaskStatus newStatus) {
        this.status = newStatus;
    }

    public void markAsRunning() {
        this.status = TaskStatus.RUNNING;
    }

    public void markAsCompleted() {
        this.status = TaskStatus.COMPLETED;
        this.endTime = LocalDateTime.now();
    }

    public void markAsFailed(String errorMessage) {
        this.status = TaskStatus.FAILED;
        this.errorMessage = errorMessage;
        this.endTime = LocalDateTime.now();
    }
}