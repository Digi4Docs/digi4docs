package de.digidoc.service;

import de.digidoc.model.Course;
import de.digidoc.model.User;
import de.digidoc.repository.CourseRepository;
import de.digidoc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class CourseService {
    private CourseRepository courseRepository;
    private UserRepository userRepository;

    @Autowired
    public CourseService(CourseRepository courseRepository, UserRepository userRepository) {
        this.courseRepository = courseRepository;
        this.userRepository = userRepository;
    }

    public Optional<Course> findById(int id) {
        return courseRepository.findById(id);
    }

    public List<Course> findAll() {
        return courseRepository.findAll();
    }

    public Course save(Course course) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        User currentUser = userRepository.findByEmail(principal.getUsername());

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
