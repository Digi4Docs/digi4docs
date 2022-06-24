package de.digi4docs.service;

import de.digi4docs.model.CourseGroup;
import de.digi4docs.repository.CourseGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class CourseGroupService {
    private CourseGroupRepository courseGroupRepository;

    @Autowired
    public CourseGroupService(CourseGroupRepository courseGroupRepository) {
        this.courseGroupRepository = courseGroupRepository;
    }

    public Optional<CourseGroup> findById(int id) {
        return courseGroupRepository.findById(id);
    }

    public List<CourseGroup> findAll() {
        return courseGroupRepository.findAll();
    }

    public List<CourseGroup> findAllActive() {
        return courseGroupRepository.findAllByIsActiveTrueOrderByName();
    }

    public CourseGroup save(CourseGroup courseGroup) {
        return courseGroupRepository.save(courseGroup);
    }

    public void delete(CourseGroup courseGroup) {
        courseGroupRepository.delete(courseGroup);
    }
}
