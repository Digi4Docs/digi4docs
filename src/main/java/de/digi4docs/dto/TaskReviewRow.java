package de.digi4docs.dto;

import de.digi4docs.model.TaskStatus;

import java.time.LocalDateTime;

public interface TaskReviewRow {
    String getTitle();

    String getModuleTitle();

    String getModuleSubTitle();

    String getTaskTitle();

    String getUserName();

    Integer getUserId();

    Integer getClassYear();

    String getClassIdentifier();

    Integer getClassNumber();

    String getTeacherName();

    Integer getUserTaskId();

    TaskStatus getUserTaskStatus();

    LocalDateTime getTransmittedAt();
}
