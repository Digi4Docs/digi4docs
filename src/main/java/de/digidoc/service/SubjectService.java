package de.digidoc.service;

import de.digidoc.model.Subject;
import de.digidoc.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class SubjectService {
    private SubjectRepository subjectRepository;

    @Autowired
    public SubjectService(SubjectRepository subjectRepository) {
        this.subjectRepository = subjectRepository;
    }

    public Optional<Subject> findById(int id) {
        return subjectRepository.findById(id);
    }

    public List<Subject> findAll() {
        return subjectRepository.findAll();
    }

    public List<Subject> findAllActive() {
        return subjectRepository.findAllByIsActiveTrueOrderByName();
    }

    public Subject save(Subject subject) {
        return subjectRepository.save(subject);
    }

    public void delete(Subject subject) {
        subjectRepository.delete(subject);
    }
}
