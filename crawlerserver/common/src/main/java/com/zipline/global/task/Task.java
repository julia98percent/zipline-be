package com.zipline.global.task;

import com.zipline.global.task.dto.ProgressDto;
import com.zipline.global.task.enums.TaskStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class  Task {
    private String id;
    private String type;
    private TaskStatus status;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String errorMessage;
    private Long targetRegion;
    private ProgressDto progress;

    public static Task createTask(String taskType, Long targetRegion) {
        return Task.builder()
                .id(UUID.randomUUID().toString())
                .type(taskType)
                .status(TaskStatus.QUEUED)
                .startTime(LocalDateTime.now())
                .targetRegion(targetRegion)
                .progress(ProgressDto.of(0, 0, 0, 0))
                .build();
    }

    public static Task createTask(String taskType) {
        return Task.builder()
                .id(UUID.randomUUID().toString())
                .type(taskType)
                .status(TaskStatus.QUEUED)
                .startTime(LocalDateTime.now())
                .progress(ProgressDto.of(0, 0, 0, 0))
                .build();
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

    public void updateProgress(long total, long processed, long success, long failed) {
        this.progress = ProgressDto.of(total, processed, success, failed);
    }
}