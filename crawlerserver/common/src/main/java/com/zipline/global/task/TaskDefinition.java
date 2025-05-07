package com.zipline.global.task;

import com.zipline.global.task.enums.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskDefinition {
    private TaskType type;
    private String description;
    private Task task;

    public static TaskDefinition of(TaskType type, String description, Task task) {
        return builder()
                .type(type)
                .description(description)
                .task(task)
                .build();
    }
}