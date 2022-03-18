package de.digidoc.service;

import de.digidoc.model.Role;
import de.digidoc.model.User;
import de.digidoc.repository.SubjectRepository;
import de.digidoc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Component
public class UserService {
    private UserRepository userRepository;
    private SubjectRepository subjectRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       SubjectRepository subjectRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.subjectRepository = subjectRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllTeachers() {
        return userRepository.findDistinctByRolesRoleOrRolesRoleOrderByLastnameAscFirstnameAsc(Role.TEACHER, Role.ADMIN);
    }

    public User add(User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public User save(User user, boolean changePassword) {
        if (changePassword) {
            user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        }
        return userRepository.save(user);
    }

    public void delete(User user) {
        user.getSubjects().forEach(subject -> subject.getUsers().remove(user));
        userRepository.delete(user);
    }
}
