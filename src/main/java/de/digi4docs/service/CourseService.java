package de.digi4docs.service;

import de.digi4docs.model.Course;
import de.digi4docs.model.User;
import de.digi4docs.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Component
public class CourseService {
    private CourseRepository courseRepository;
    private UserService userService;
    private TaskService taskService;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserService userService, TaskService taskService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.taskService = taskService;
    }

    public Optional<Course> findById(int id) {
        return courseRepository.findById(id);
    }

    public List<Course> findAll() {
        List<Course> courses = courseRepository.findAll();
        courses.sort(Comparator.comparing(Course::getTitle));
        return courses;
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
