package de.digi4docs.repository;

import de.digi4docs.dto.CourseOverviewResult;
import de.digi4docs.model.Course;
import de.digi4docs.model.CourseGroup;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findAllByIsActiveTrueAndCourseGroupsIsNullOrderByTitle();

    List<Course> findAllByIsActiveTrueAndCourseGroupsInOrderByTitle(Set<CourseGroup> courseGroups);

    List<Course> findAllByCourseGroups(CourseGroup courseGroup);

    @Query("SELECT u.id as userId, u.firstname as firstname, u.lastname as lastname, m.id as moduleId, m.title as " +
            "title, " +
            "SUM(CASE WHEN ut.status='TRANSMITTED' THEN 1 ELSE 0 END) as countTasksTransmitted," +
            "SUM(CASE WHEN ut.status='REJECTED' THEN 1 ELSE 0 END) as countTasksRejected," +
            "SUM(CASE WHEN ut.status='DONE' THEN 1 ELSE 0 END) as countTasksDone " +
            "FROM UserTask ut JOIN Task t ON ut.task.id = t.id " +
            "JOIN Module m ON m.id = t.module.id " +
            "JOIN User u ON u.id = ut.user.id " +
            "WHERE m.id IN (:moduleIds) " +
            "AND u.isActive = true " +
            "GROUP BY u.id, u.firstname, u.lastname, m.id, m.title")
    List<CourseOverviewResult> findCourseOverview(List<Integer> moduleIds);
}
