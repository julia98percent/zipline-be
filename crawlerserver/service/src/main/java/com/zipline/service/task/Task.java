package com.zipline.service.task;

import com.zipline.service.task.enums.TaskType;
import lombok.Getter;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
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