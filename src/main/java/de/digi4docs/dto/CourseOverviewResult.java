package de.digi4docs.dto;

public interface CourseOverviewResult {
    Integer getUserId();

    String getFirstname();

    String getLastname();

    Integer getModuleId();

    String getTitle();

    Integer getCountTasksTransmitted();

    Integer getCountTasksRejected();

    Integer getCountTasksDone();
}
