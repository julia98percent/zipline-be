package com.zipline.global.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.zipline.global.exception.task.TaskException;
import com.zipline.global.exception.task.errorcode.TaskErrorCode;
import com.zipline.global.task.enums.TaskStatus;
import com.zipline.global.task.enums.TaskType;
import org.springframework.stereotype.Component;


//마이그레이션 작업 관리 구현체
@Component
public class TaskManagerImpl implements TaskManager {
    private final Map<TaskType, Task> taskRegistry = new ConcurrentHashMap<>();
    private final Lock lock = new ReentrantLock();

    @Override
    public boolean isTaskRunning(TaskType taskType) {
        return taskRegistry.values().stream()
                .anyMatch(task -> task.getType().equals(taskType) &&
                        task.getStatus() == TaskStatus.RUNNING);
    }

    @Override
    public Task createTask(TaskType taskType) {
        lock.lock();
        try {
            if (isTaskRunning(taskType)) {
                throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
            }
            taskRegistry.put(taskType, Task.createTask(taskType));
            return taskRegistry.get(taskType);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Task createRegionalTask(TaskType taskType, Long targetRegion) {
        lock.lock();
        try {
            if (isTaskRunning(taskType)) {
                throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
            }
            Task task = Task.createTask(taskType, targetRegion);
            taskRegistry.put(taskType, Task.createTask(taskType, targetRegion));
            return task;
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Task getTaskByType(TaskType taskType) {
        Task task = taskRegistry.get(taskType);
        if (task == null) {
            throw new TaskException(TaskErrorCode.TASK_NOT_FOUND);
        }
        return task;
    }

     public void updateTask(TaskType taskType, Task updatedTask) {
        if (!taskRegistry.containsKey(taskType)) {
            throw new IllegalArgumentException("해당 타입의 Task가 존재하지 않습니다: " + taskType);
        }
        if (updatedTask.getType() != taskType) {
            throw new IllegalArgumentException("업데이트할 Task의 타입이 요청 타입과 일치하지 않습니다.");
        }
        taskRegistry.put(taskType, updatedTask);
    }

    public void removeTask(TaskType taskType) {
        if (!taskRegistry.containsKey(taskType)) {
            throw new IllegalArgumentException("해당 타입의 Task가 존재하지 않습니다: " + taskType);
        }
        taskRegistry.remove(taskType);
    }

    public void updateTaskStatus(TaskType taskType, TaskStatus newStatus) {
        Task task = taskRegistry.get(taskType);
        if (task != null) {
            task.setStatus(newStatus);
        }
    }
}