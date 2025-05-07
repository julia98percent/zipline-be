package com.zipline.global.task;

import com.zipline.global.task.enums.TaskType;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Task<T> {
    private String id;
    private TaskType type;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String errorMessage;
    private T targetEntity;

    public static <U> Task<U> createTask(TaskType taskType, U targetEntity) {
        return Task.<U>builder()
                .type(taskType)
                .startTime(LocalDateTime.now())
                .targetEntity(targetEntity)
                .build();
    }

    public static Task<?> createTask(TaskType taskType) {
        return createTask(taskType, null);
    }
}