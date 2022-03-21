package de.digidoc.service;

import de.digidoc.model.Course;
import de.digidoc.model.User;
import de.digidoc.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class CourseService {
    private CourseRepository courseRepository;
    private UserService userService;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserService userService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
    }

    public Optional<Course> findById(int id) {
        return courseRepository.findById(id);
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public List<Course> findAllActive() {
        return courseRepository.findAllByIsActiveTrueOrderByTitle();
    }


    public Course save(Course course) {
        User currentUser = userService.findCurrentUser();

        if (null == course.getCreatedBy()) {
            course.setCreatedBy(currentUser);
            course.setCreatedAt(LocalDateTime.now());
        }

        course.setEditedBy(currentUser);
        course.setEditedAt(LocalDateTime.now());

        return courseRepository.save(course);
    }

    public void delete(Course course) {
        courseRepository.delete(course);
    }
}
