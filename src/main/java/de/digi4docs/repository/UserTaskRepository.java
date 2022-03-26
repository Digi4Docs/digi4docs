package de.digi4docs.repository;

import de.digi4docs.dto.StudentCountResult;
import de.digi4docs.dto.TaskDoneCountResult;
import de.digi4docs.model.TaskStatus;
import de.digi4docs.model.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Controller;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public interface UserTaskRepository extends JpaRepository<UserTask, Integer> {

    Optional<UserTask> findByTaskIdAndUserId(int taskId, int userId);

    List<UserTask> findByTaskIdInAndUserId(List<Integer> taskIds, int userId);

    List<UserTask> findByUserId(int userId);

    List<UserTask> findByTeacherIdAndStatus(int teacherId, TaskStatus taskStatus);

    List<UserTask> findByStatus(TaskStatus taskStatus);

    @Query("SELECT COUNT(u.id) as total, u.classYear as year, ut.task.id as task FROM UserTask ut, User u, UserRole ur WHERE ut.user.id = u.id AND ur.user.id = u.id AND ut.task.id IN (:taskIds) AND ur.role = 'STUDENT' AND ut.status = 'DONE' AND ut.doneAt >= :start AND ut.doneAt <= :end AND u.classYear IS NOT NULL GROUP BY u.classYear, ut.task")
    List<TaskDoneCountResult> findTaskCountGroupedByYear(List<Integer> taskIds, LocalDateTime start, LocalDateTime end);
}
