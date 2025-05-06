package com.zipline.global.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import com.zipline.global.exception.task.TaskException;
import com.zipline.global.exception.task.errorcode.TaskErrorCode;
import com.zipline.global.task.enums.TaskStatus;
import com.zipline.global.task.enums.TaskType;
import org.springframework.stereotype.Component;

/**
 * 마이그레이션 작업 관리 구현체
 */
@Component
public class TaskManagerImpl implements TaskManager {
    private final Map<TaskType, Task> taskRegistry = new ConcurrentHashMap<>();
    private final Map<TaskType, AtomicBoolean> taskLocks = new ConcurrentHashMap<>();

    @Override
    public boolean isTaskRunning(TaskType taskType) {
        Task task = taskRegistry.get(taskType);
        return task != null && task.getStatus() == TaskStatus.RUNNING;
    }

    @Override
    public Task createTask(TaskType taskType) {
        taskLocks.putIfAbsent(taskType, new AtomicBoolean(false));
        AtomicBoolean taskLock = taskLocks.get(taskType);

        if (!taskLock.compareAndSet(false, true)) {
            if (isTaskRunning(taskType)) {
                throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
            }
        }

        try {
            if (isTaskRunning(taskType)) {
                throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
            }

            Task task = Task.createTask(taskType);
            taskRegistry.put(taskType, task);
            return task;
        } finally {
            taskLock.set(false);
        }
    }

    @Override
    public Task createRegionalTask(TaskType taskType, Long targetRegion) {
        taskLocks.putIfAbsent(taskType, new AtomicBoolean(false));
        AtomicBoolean taskLock = taskLocks.get(taskType);

        if (!taskLock.compareAndSet(false, true)) {
            if (isTaskRunning(taskType)) {
                throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
            }
        }

        try {
            if (isTaskRunning(taskType)) {
                throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
            }

            Task task = Task.createTask(taskType, targetRegion);
            taskRegistry.put(taskType, task);
            return task;
        } finally {
            taskLock.set(false);
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

        taskLocks.putIfAbsent(taskType, new AtomicBoolean(false));
        AtomicBoolean taskLock = taskLocks.get(taskType);

        if (taskLock.compareAndSet(false, true)) {
            try {
                taskRegistry.put(taskType, updatedTask);
            } finally {
                taskLock.set(false);
            }
        } else {
            throw new TaskException(TaskErrorCode.TASK_OPERATION_IN_PROGRESS);
        }
    }

    public void removeTask(TaskType taskType) {
        if (!taskRegistry.containsKey(taskType)) {
            throw new IllegalArgumentException("해당 타입의 Task가 존재하지 않습니다: " + taskType);
        }

        taskLocks.putIfAbsent(taskType, new AtomicBoolean(false));
        AtomicBoolean taskLock = taskLocks.get(taskType);

        if (taskLock.compareAndSet(false, true)) {
            try {
                taskRegistry.remove(taskType);
                taskLocks.remove(taskType);
            } finally {
                taskLock.set(false);
            }
        } else {
            throw new TaskException(TaskErrorCode.TASK_OPERATION_IN_PROGRESS);
        }
    }

    public void updateTaskStatus(TaskType taskType, TaskStatus newStatus) {
        Task task = taskRegistry.get(taskType);
        if (task == null) {
            throw new TaskException(TaskErrorCode.TASK_NOT_FOUND);
        }

        taskLocks.putIfAbsent(taskType, new AtomicBoolean(false));
        AtomicBoolean taskLock = taskLocks.get(taskType);

        if (taskLock.compareAndSet(false, true)) {
            try {
                task.setStatus(newStatus);
            } finally {
                taskLock.set(false);
            }
        } else {
            throw new TaskException(TaskErrorCode.TASK_OPERATION_IN_PROGRESS);
        }
    }
}