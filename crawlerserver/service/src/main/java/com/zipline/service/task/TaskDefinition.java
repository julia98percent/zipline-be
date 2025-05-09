package com.zipline.service.task;

import com.zipline.service.task.enums.TaskType;
import lombok.Getter;
import lombok.Builder;
import lombok.AllArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
public class TaskDefinition {
    private final TaskType type;
    private final String description;
    private final Runnable task; // Lambda wrapper for any arguments

    public static TaskDefinition of(TaskType type, String description, Runnable task) {
        return new TaskDefinition(type, description, task);
    }
}