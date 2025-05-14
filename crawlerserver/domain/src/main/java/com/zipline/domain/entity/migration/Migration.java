package com.zipline.domain.entity.migration;

import com.zipline.domain.entity.enums.MigrationStatus;
import com.zipline.domain.entity.enums.Platform;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Builder
@Entity
@Table(name = "migrations")
public class Migration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "cortar_no", nullable = false, unique = true)
    private Long cortarNo;

    @Column(name = "naver_status")
    @Enumerated(EnumType.STRING)
    private MigrationStatus naverStatus;

    @Column(name = "naver_last_migrated_at")
    private LocalDateTime naverLastMigratedAt;

    @Column(name = "error_log", columnDefinition = "LONGTEXT")
    private String errorLog;


    public Migration(Long id, Long cortarNo, MigrationStatus naverStatus, LocalDateTime naverLastCrawledAt, String errorLog) {
        this.id = id;
        this.cortarNo = cortarNo;
        this.naverStatus = naverStatus;
        this.naverLastMigratedAt = naverLastCrawledAt;
        this.errorLog = errorLog;
    }

    public Migration CreateMigration(Long cortarNo) {
        this.cortarNo = cortarNo;
        this.naverStatus = MigrationStatus.PENDING;
        this.naverLastMigratedAt = null;
        return this;
    }

    public Migration UpdateMigration(Long cortarNo) {
        this.cortarNo = cortarNo;
        this.naverStatus = MigrationStatus.PENDING;
        this.naverLastMigratedAt = null;
        return this;
    }

    public Migration updateNaverMigrationStatus(MigrationStatus status) {
        this.naverStatus = status;
        this.naverLastMigratedAt = LocalDateTime.now();
        return this;
    }

    public Migration appendErrorLog(String newError, int maxLength) {
        String currentLog = this.errorLog != null ? this.errorLog : "";

        String updatedLog = currentLog.isEmpty() ?
                String.format("[%s] %s", LocalDateTime.now(), newError) :
                currentLog + "\n" + String.format("[%s] %s", LocalDateTime.now(), newError);

        if (updatedLog.length() > maxLength) {
            updatedLog = updatedLog.substring(updatedLog.length() - maxLength);
        }

        this.errorLog = updatedLog;
        return this;
    }

    public Migration errorWithLog(Platform platform, String newError, int maxLength, MigrationStatus status) {
        String currentLog = this.errorLog != null ? this.errorLog : "";

        String updatedLog = currentLog.isEmpty() ?
                String.format("[%s] %s", LocalDateTime.now(), newError) :
                currentLog + "\n" + String.format("[%s] %s", LocalDateTime.now(), newError);

        if (updatedLog.length() > maxLength) {
            updatedLog = updatedLog.substring(updatedLog.length() - maxLength);
        }
        if (platform == Platform.NAVER) {
            this.naverStatus = status;
            this.naverLastMigratedAt = LocalDateTime.now();
        }
        this.errorLog = updatedLog;
        return this;
    }
}