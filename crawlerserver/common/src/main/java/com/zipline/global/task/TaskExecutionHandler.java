package com.zipline.global.task;

import com.zipline.global.exception.task.TaskException;
import com.zipline.global.exception.task.errorcode.TaskErrorCode;
import com.zipline.global.task.dto.TaskResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskExecutionHandler {

    private final TaskManager taskManager;
    private final TaskExecutor taskExecutor;

    public <T> TaskResponseDto execute(TaskDefinition<T> definition) {
        if (taskManager.isTaskRunning(definition.getType())) {
            throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
        }

        Task<T> task = Task.createTask(definition.getType(), definition.getTarget());
        taskManager.createTask(task.getType());

        try {
            CompletableFuture.runAsync(() -> {
                definition.getTask().run(task.getTarget());
            }, taskExecutor);
        } catch (Exception e) {
            log.error("{} 실행 실패", definition.getDescription(), e);
            taskManager.removeTask(definition.getType());
            throw new RuntimeException(definition.getDescription() + " 실행 실패", e);
        }
        taskManager.removeTask(definition.getType());
        return TaskResponseDto.fromTask(task);
    }
}