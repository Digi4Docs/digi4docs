package de.digi4docs.repository;

import de.digi4docs.dto.TaskDoneCountResult;
import de.digi4docs.dto.TaskReviewRow;
import de.digi4docs.model.TaskStatus;
import de.digi4docs.model.UserTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserTaskRepository extends JpaRepository<UserTask, Integer> {

    Optional<UserTask> findByTaskIdAndUserId(int taskId, int userId);

    List<UserTask> findByTaskIdInAndUserId(List<Integer> taskIds, int userId);

    List<UserTask> findByIdIn(List<Integer> userTaskIds);

    List<UserTask> findByUserId(int userId);

    @Query("SELECT t.title as title, m.title as moduleTitle, m.subTitle as moduleSubTitle, u.id as userId, CONCAT(u" +
            ".lastname, ', ', u.firstname) as userName, u.classYear as classYear, u.classNumber as classNumber, u" +
            ".classIdentifier as classIdentifier,CONCAT(u2.lastname, ', ', u2" +
            ".firstname) as teacherName, ut.id as userTaskId, ut.status as userTaskStatus, ut.transmittedAt as " +
            "transmittedAt, t.title as taskTitle " +
            "FROM UserTask ut JOIN Task t ON t.id = ut.task.id " +
            "JOIN Module m ON m.id = t.module.id " +
            "JOIN User u ON u.id = ut.user.id " +
            "JOIN User u2 ON u2.id = ut.teacher.id " +
            "WHERE ut.status = 'TRANSMITTED' and u2.id = :teacherId")
    Page<TaskReviewRow> findTransmitted(int teacherId, Pageable pageable);

    @Query("SELECT t.title as title, m.title as moduleTitle, m.subTitle as moduleSubTitle, u.id as userId, CONCAT(u" +
            ".lastname, ', ', u.firstname) as userName, u.classYear as classYear, u.classNumber as classNumber, u" +
            ".classIdentifier as classIdentifier,CONCAT(u2.lastname, ', ', u2" +
            ".firstname) as teacherName, ut.id as userTaskId, ut.status as userTaskStatus, ut.transmittedAt as " +
            "transmittedAt, t.title as taskTitle " +
            "FROM UserTask ut JOIN Task t ON t.id = ut.task.id " +
            "JOIN Module m ON m.id = t.module.id " +
            "JOIN User u ON u.id = ut.user.id " +
            "JOIN User u2 ON u2.id = ut.teacher.id " +
            "WHERE (ut.status = 'DONE' or ut.status = 'REJECTED') and u2.id = :teacherId")
    Page<TaskReviewRow> findDone(int teacherId, Pageable pageable);

    @Query("SELECT t.title as title, m.title as moduleTitle, m.subTitle as moduleSubTitle, u.id as userId, CONCAT(u" +
            ".lastname, ', ', u.firstname) as userName, u.classYear as classYear, u.classNumber as classNumber, u" +
            ".classIdentifier as classIdentifier,CONCAT(u2.lastname, ', ', u2" +
            ".firstname) as teacherName, ut.id as userTaskId, ut.status as userTaskStatus, ut.transmittedAt as " +
            "transmittedAt, t.title as taskTitle " +
            "FROM UserTask ut JOIN Task t ON t.id = ut.task.id " +
            "JOIN Module m ON m.id = t.module.id " +
            "JOIN User u ON u.id = ut.user.id " +
            "JOIN User u2 ON u2.id = ut.teacher.id " +
            "WHERE ut.status = :taskStatus")
    Page<TaskReviewRow> findDtoByStatus(TaskStatus taskStatus, Pageable pageable);

    @Query("SELECT t.title as title, m.title as moduleTitle, m.subTitle as moduleSubTitle, u.id as userId, CONCAT(u" +
            ".lastname, ', ', u.firstname) as userName, u.classYear as classYear, u.classNumber as classNumber, u" +
            ".classIdentifier as classIdentifier,CONCAT(u2.lastname, ', ', u2" +
            ".firstname) as teacherName, ut.id as userTaskId, ut.status as userTaskStatus, ut.transmittedAt as " +
            "transmittedAt, t.title as taskTitle " +
            "FROM UserTask ut JOIN Task t ON t.id = ut.task.id " +
            "JOIN Module m ON m.id = t.module.id " +
            "JOIN User u ON u.id = ut.user.id " +
            "JOIN User u2 ON u2.id = ut.teacher.id " +
            "WHERE ut.status = 'DONE' OR ut.status = 'REJECTED'")
    Page<TaskReviewRow> findDone(Pageable pageable);

    List<UserTask> findByUserIdAndStatus(int userId, TaskStatus taskStatus);

    @Query("SELECT COUNT(u.id) as total, u.classYear as year, ut.task.id as task FROM UserTask ut, User u, UserRole " +
            "ur WHERE ut.user.id = u.id AND ur.user.id = u.id AND ut.task.id IN (:taskIds) AND ur.role = 'STUDENT' " +
            "AND ut.status = 'DONE' AND ut.doneAt >= :start AND ut.doneAt <= :end AND u.classYear IS NOT NULL AND u" +
            ".isActive = true GROUP BY u.classYear, ut.task")
    List<TaskDoneCountResult> findTaskCountGroupedByYear(List<Integer> taskIds, LocalDateTime start, LocalDateTime end);

    @Query("SELECT t.id FROM UserTask ut JOIN Task t ON ut.task.id = t.id AND ut.status = 'DONE' AND ut" +
            ".user.id = :userId")
    List<Integer> findAllDoneTaskIds(Integer userId);
}
