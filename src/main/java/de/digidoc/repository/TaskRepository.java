package de.digidoc.repository;

import de.digidoc.model.Module;
import de.digidoc.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findAllByModuleIdOrderByTitle(Integer courseId);

    List<Task> findAllByModuleIdAndIsActiveTrueOrderByTitle(Integer courseId);
}
