package de.digidoc.service;

import de.digidoc.model.Course;
import de.digidoc.model.Module;
import de.digidoc.model.User;
import de.digidoc.model.UserTask;
import de.digidoc.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class CourseService {
    private CourseRepository courseRepository;
    private UserService userService;
    private UserTaskService userTaskService;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserService userService, UserTaskService userTaskService) {
        this.courseRepository = courseRepository;
        this.userService = userService;
        this.userTaskService = userTaskService;
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

    public List<Course> findAllPersonal() {
        List<UserTask> userTasks = userTaskService.findByUser();

        if (userTasks.isEmpty()) {
            return new ArrayList<>();
        }

        Map<Integer, Course> courseMap = new HashMap<>();

        userTasks.forEach(userTask -> {
            Module module = userTask.getTask().getModule();
            while (null != module.getParent()) {
                module = module.getParent();
            }

            if (null != module.getCourse() && !courseMap.containsKey(module.getCourse().getId())) {
                courseMap.put(module.getCourse().getId(), module.getCourse());
            }
        });

        List<Course> courses = new ArrayList<>(courseMap.values());
        courses.sort(Comparator.comparing(Course::getTitle));

        return courses;
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
