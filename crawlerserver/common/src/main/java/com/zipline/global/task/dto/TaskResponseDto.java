package com.zipline.global.task.dto;

import com.zipline.global.task.Task;
import com.zipline.global.task.enums.TaskType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TaskResponseDto {
    private TaskType taskType;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String message;
    private Object targetEntity;
    private ProgressDto progress;

    public static <T> TaskResponseDto fromTask(Task<T> task) {
        return TaskResponseDto.builder()
                .taskType(task.getType())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .message(task.getErrorMessage())
                .targetEntity(task.getTargetEntity())
                .build();
    }
}