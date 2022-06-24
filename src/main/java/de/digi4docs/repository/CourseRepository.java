package de.digi4docs.repository;

import de.digi4docs.model.Course;
import de.digi4docs.model.CourseGroup;
import de.digi4docs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {
    List<Course> findAllByIsActiveTrueAndCourseGroupsIsNullOrderByTitle();

    List<Course> findAllByIsActiveTrueAndCourseGroupsInOrderByTitle(Set<CourseGroup> courseGroups);

    List<Course> findAllByCourseGroups(CourseGroup courseGroup);
}
