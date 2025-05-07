package com.zipline.global.task;

import com.zipline.global.task.enums.TaskType;
import lombok.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class TaskDefinition<T> {
    private TaskType type;
    private String description;
    private RunnableWithArg<T> task;
    private T targetEntity;

    @FunctionalInterface
    public interface RunnableWithArg<T> {
        void run(T targetEntity);
    }

    public static <T> TaskDefinition<T> of(TaskType type, String description, RunnableWithArg<T> task, T targetEntity) {
        return new TaskDefinition<>(type, description, task, targetEntity);
    }
}
