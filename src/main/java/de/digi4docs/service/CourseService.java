package de.digi4docs.service;

import de.digi4docs.dto.CourseOverviewResult;
import de.digi4docs.model.Course;
import de.digi4docs.model.CourseGroup;
import de.digi4docs.model.User;
import de.digi4docs.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

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
        List<Course> courses = courseRepository.findAll();
        courses.sort(Comparator.comparing(Course::getTitle));
        return courses;
    }

    public List<Course> findAllActiveUngrouped() {
        return courseRepository.findAllByIsActiveTrueAndCourseGroupsIsNullOrderByTitle();
    }

    public List<Course> findAllActiveGroupedOfUser() {
        User currentUser = userService.findCurrentUser();
        Set<CourseGroup> courseGroups = currentUser.getCourseGroups();
        if (courseGroups.isEmpty()) {
            return Collections.emptyList();
        }

        return courseRepository.findAllByIsActiveTrueAndCourseGroupsInOrderByTitle(courseGroups);
    }

    public List<CourseOverviewResult> findCourseOverview(List<Integer> moduleIds) {
        return courseRepository.findCourseOverview(moduleIds);
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

    public void removeCourseGroup(CourseGroup courseGroup) {
        List<Course> courseGroupUsers = courseRepository.findAllByCourseGroups(courseGroup);
        courseGroupUsers.forEach(user -> {
            user.getCourseGroups()
                .remove(courseGroup);
            courseRepository.save(user);
        });
    }
}
