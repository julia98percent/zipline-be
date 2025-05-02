package com.zipline.global.task.migration;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.zipline.global.exception.task.TaskException;
import com.zipline.global.exception.task.errorcode.TaskErrorCode;
import com.zipline.global.task.Task;
import com.zipline.global.task.TaskManager;
import com.zipline.global.task.enums.TaskStatus;
import org.springframework.stereotype.Component;


//마이그레이션 작업 관리 구현체
@Component
public class MigrationTaskManager implements TaskManager {
    private final Map<String, Task> taskRegistry = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();

    @Override
    public boolean isTaskRunning(String taskType) {
        return taskRegistry.values().stream()
                .anyMatch(task -> task.getType().equals(taskType) &&
                        task.getStatus() == TaskStatus.RUNNING);
    }

    @Override
    public Task createTask(String taskType) {
        lock.lock();
        try {
            if (isTaskRunning(taskType)) {
                throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
            }
            Task task = Task.createTask(taskType);
            taskRegistry.put(task.getId(), task);
            return task;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateTaskStatus(Task task) {
        taskRegistry.put(task.getId(), task);
    }

    @Override
    public Task createRegionalTask(String taskType, Long targetRegion) {
        lock.lock();
        try {
            if (isTaskRunning(taskType)) {
                throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
            }

            Task task = Task.createTask(taskType, targetRegion);
            taskRegistry.put(task.getId(), task);
            return task;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Task getTaskById(String taskId) {
        Task task = taskRegistry.get(taskId);
        if (task == null) {
            throw new TaskException(TaskErrorCode.TASK_NOT_FOUND);
        }
        return task;
    }
}