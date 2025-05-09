package com.zipline.service.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.zipline.global.exception.task.TaskException;
import com.zipline.global.exception.task.errorcode.TaskErrorCode;
import com.zipline.service.task.enums.TaskType;
import org.springframework.stereotype.Component;

/**
 * 마이그레이션 작업 관리 구현체 bean이라 싱글톤처럼 작동
 * Thread-safe implementation using ConcurrentHashMap only
 */
@Component
public class TaskManagerImpl implements TaskManager {
    private final Map<TaskType, Task<?>> taskRegistry = new ConcurrentHashMap<>(); // Use Task<?>

    @Override
    public boolean isTaskRunning(TaskType taskType) {
        Task<?> task = taskRegistry.get(taskType);
        return task != null;
    }

    @Override
    public <T> Task<T> createTask(TaskType taskType, T target) {
        if (isTaskRunning(taskType)) {
            throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
        }
        Task<T> newTask = Task.createTask(taskType, target);
        taskRegistry.put(taskType, newTask);
        return newTask;
    }

    @Override
    public Task<?> createTask(TaskType taskType) {
        return createTask(taskType, null);
    }

    @Override
    public Task<?> getTaskByType(TaskType taskType) {
        Task<?> task = taskRegistry.get(taskType);
        if (task == null) {
            throw new TaskException(TaskErrorCode.TASK_NOT_FOUND);
        }
        return task;
    }

    public void removeTask(TaskType taskType) {
        taskRegistry.remove(taskType);
    }
}