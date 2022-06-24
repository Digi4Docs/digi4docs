package de.digi4docs.repository;

import de.digi4docs.model.CourseGroup;
import de.digi4docs.model.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseGroupRepository  extends JpaRepository<CourseGroup, Integer> {
    List<CourseGroup> findAllByIsActiveTrueOrderByName();
}

