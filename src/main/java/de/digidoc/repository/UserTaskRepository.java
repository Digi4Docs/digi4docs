package de.digidoc.repository;

import de.digidoc.model.UserTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.Optional;

@Controller
public interface UserTaskRepository extends JpaRepository<UserTask, Integer> {

    Optional<UserTask> findByTaskIdAndUserId(int taskId, int userId);

    List<UserTask> findByTaskIdInAndUserId(List<Integer> taskIds, int userId);

    List<UserTask> findByUserId( int userId);
}
