package com.zipline.global.task;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import com.zipline.global.exception.task.TaskException;
import com.zipline.global.exception.task.errorcode.TaskErrorCode;
import com.zipline.global.task.enums.TaskStatus;
import com.zipline.global.task.enums.TaskType;
import org.springframework.stereotype.Component;

/**
 * 마이그레이션 작업 관리 구현체 bean이라 싱글톤처럼 작동
 * Thread-safe implementation using ConcurrentHashMap only
 */
@Component
public class TaskManagerImpl implements TaskManager {
    private final Map<TaskType, Task> taskRegistry = new ConcurrentHashMap<>();

    @Override
    public boolean isTaskRunning(TaskType taskType) {
        Task task = taskRegistry.get(taskType);
        return task != null;
    }

    @Override
    public Task createTask(TaskType taskType) {
        Task newTask = Task.createTask(taskType);
        if (isTaskRunning(taskType)) {
            throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
        }taskRegistry.put(taskType, newTask);
        return newTask;
    }

    @Override
    public Task createRegionalTask(TaskType taskType, Long targetRegion) {
        Task newTask = Task.createTask(taskType, targetRegion);
        if (isTaskRunning(taskType)) {
            throw new TaskException(TaskErrorCode.TASK_ALREADY_RUNNING);
        }taskRegistry.put(taskType, newTask);
        return newTask;
    }

    @Override
    public Task getTaskByType(TaskType taskType) {
        Task task = taskRegistry.get(taskType);
        if (task == null) {
            throw new TaskException(TaskErrorCode.TASK_NOT_FOUND);
        }
        return task;
    }

//    public void updateTask(TaskType taskType, Task updatedTask) {
//        if (!taskRegistry.containsKey(taskType)) {
//            throw new IllegalArgumentException("해당 타입의 Task가 존재하지 않습니다: " + taskType);
//        }
//        if (updatedTask.getType() != taskType) {
//            throw new IllegalArgumentException("업데이트할 Task의 타입이 요청 타입과 일치하지 않습니다.");
//        }
//
//        taskRegistry.put(taskType, updatedTask);
//    }

    public void removeTask(TaskType taskType) {
        taskRegistry.remove(taskType);
    }
}