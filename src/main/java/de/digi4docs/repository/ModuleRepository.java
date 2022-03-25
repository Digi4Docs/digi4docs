package de.digi4docs.repository;

import de.digi4docs.model.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Integer> {

    List<Module> findAllByCourseIdOrderByTitle(Integer courseId);

    List<Module> findAllByParentIdOrderByTitle(Integer moduleId);
}
