package de.digi4docs.repository;

import de.digi4docs.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findAllByModuleIdOrderByOrderPositionAscTitleAsc(Integer moduleId);

    Optional<Task> findFirstByModuleIdOrderByOrderPositionDesc(Integer moduleId);

    Optional<Task> findFirstByModuleIdAndOrderPositionGreaterThanOrderByOrderPositionAsc(Integer moduleId, Integer orderPosition);

    Optional<Task> findFirstByModuleIdAndOrderPositionLessThanOrderByOrderPositionDesc(Integer moduleId, Integer orderPosition);
}
