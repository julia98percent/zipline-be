package com.zipline.global.task.dto;

import com.zipline.global.task.Task;
import com.zipline.global.task.enums.TaskType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class TaskResponseDto {
    private TaskType taskType;
    private String taskStatus;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String message;
    private Long targetRegion;
    private ProgressDto progress;

    public static TaskResponseDto fromTask(Task task) {
        return TaskResponseDto.builder()
                .taskType(task.getType())
                .taskStatus(task.getStatus().name())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .message(getStatusMessage(task))
                .targetRegion(task.getTargetRegion())
                .build();
    }

    private static String getStatusMessage(Task task) {
        switch (task.getStatus()) {
            case QUEUED: return "작업이 대기 중입니다.";
            case RUNNING: return "작업이 실행 중입니다.";
            case COMPLETED: return "작업이 완료되었습니다.";
            case FAILED: return task.getErrorMessage() != null
                    ? "작업이 실패했습니다: " + task.getErrorMessage()
                    : "작업이 실패했습니다.";
            default: return "알 수 없는 상태입니다.";
        }
    }
}