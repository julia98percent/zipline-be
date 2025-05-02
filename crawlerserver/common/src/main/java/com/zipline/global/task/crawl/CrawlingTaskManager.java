package com.zipline.global.task.crawl;

import com.zipline.global.exception.task.TaskException;
import com.zipline.global.exception.task.errorcode.TaskErrorCode;
import com.zipline.global.task.Task;
import com.zipline.global.task.enums.TaskStatus;
import org.springframework.stereotype.Component;

import com.zipline.global.task.TaskManager;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 작업 관리 구현체
@Component
public class CrawlingTaskManager implements TaskManager {
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

            String taskId = UUID.randomUUID().toString();
            Task task = Task.builder()
                    .id(taskId)
                    .type(taskType)
                    .status(TaskStatus.QUEUED)
                    .startTime(LocalDateTime.now())
                    .build();

            taskRegistry.put(taskId, task);
            return task;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public void updateTaskStatus(Task task){
        taskRegistry.put(task.getId(), task);
    }

    @Override
    public Task createRegionalTask(String taskType, Long targetRegion) {
        return null;
    }

    @Override
    public Task getTaskById(String taskId) {
        return taskRegistry.get(taskId);
    }
}
