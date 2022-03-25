package de.digi4docs.repository;

import de.digi4docs.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {

    List<Task> findAllByModuleIdOrderByTitle(Integer courseId);
}
