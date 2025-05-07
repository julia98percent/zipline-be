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
    private T target;

    @FunctionalInterface
    public interface RunnableWithArg<T> {
        void run(T target);
    }

    public static <T> TaskDefinition<T> of(TaskType type, String description, RunnableWithArg<T> task, T target) {
        return new TaskDefinition<>(type, description, task, target);
    }
}
