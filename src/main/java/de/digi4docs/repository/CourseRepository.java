package de.digi4docs.repository;

import de.digi4docs.model.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findAllByIsActiveTrueOrderByTitle();
}
