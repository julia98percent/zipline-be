package com.zipline.global.task;

import com.zipline.global.task.enums.TaskType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class  Task<T> {
    private String id;
    private TaskType type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String errorMessage;
    private T target;

    public static <U> Task<U> createTask(TaskType taskType, U target) {
        return Task.<U>builder()
                .type(taskType)
                .startTime(LocalDateTime.now())
                .target(target)
                .build();
    }

    public static Task<?> createTask(TaskType taskType) {
        return Task.builder()
                .type(taskType)
                .startTime(LocalDateTime.now())
                .build();
    }
}