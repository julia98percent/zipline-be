package com.zipline.service.task.dto;


import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ProgressDto {
    private long totalCount;
    private long processedCount;
    private long successCount;
    private long failedCount;
    private double progressPercentage;

    public static ProgressDto of(
            long totalCount,
            long processedCount,
            long successCount,
            long failedCount
    ) {
        double percentage = totalCount > 0
                ? (double) processedCount / totalCount * 100
                : 0;

        return ProgressDto.builder()
                .totalCount(totalCount)
                .processedCount(processedCount)
                .successCount(successCount)
                .failedCount(failedCount)
                .progressPercentage(Math.round(percentage * 100) / 100.0)
                .build();
    }
}
