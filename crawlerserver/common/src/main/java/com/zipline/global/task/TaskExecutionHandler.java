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

        Task<T> task = Task.createTask(definition.getType(), definition.getTargetEntity());
        taskManager.createTask(task.getType());

        CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
            try {
                definition.getTask().run(task.getTargetEntity());
            } catch (Exception e) {
                log.error("{} 실행 중 예외 발생", definition.getDescription(), e);
                throw e;
            }
        }, taskExecutor);

        future.thenRun(() -> {
            log.info("{} 작업 완료 - 태스크 제거", definition.getDescription());
            taskManager.removeTask(definition.getType());
        });

        future.exceptionally(ex -> {
            log.error("{} 작업 실패 - 태스크 제거", definition.getDescription(), ex);
            taskManager.removeTask(definition.getType());
            return null;
        });

        return TaskResponseDto.fromTask(task);
    }
}