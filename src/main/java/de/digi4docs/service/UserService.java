package de.digi4docs.service;

import de.digi4docs.dto.StudentCountResult;
import de.digi4docs.model.CourseGroup;
import de.digi4docs.model.Role;
import de.digi4docs.model.User;
import de.digi4docs.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserService {
    private UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
            BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Optional<User> findActiveUserByEmail(String email) {
        return userRepository.findByEmailAndIsActiveTrue(email);
    }

    public Optional<User> findById(int id) {
        return userRepository.findById(id);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public List<User> findAllTeachers() {
        return userRepository.findDistinctByRolesRoleOrRolesRoleOrderByLastnameAscFirstnameAsc(Role.TEACHER,
                Role.ADMIN);
    }

    public List<User> findAllActiveTeachers() {
        return userRepository.findDistinctByRolesRoleOrRolesRoleAndIsActiveTrueOrderByLastnameAscFirstnameAsc(
                Role.TEACHER, Role.ADMIN);
    }

    public User findCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext()
                                                   .getAuthentication();
        org.springframework.security.core.userdetails.User principal =
                (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        return userRepository.findByEmail(principal.getUsername());
    }

    public List<StudentCountResult> findStudentCountGroupedByYears() {
        return userRepository.findStudentCountGroupedByYear();
    }

    public boolean hasCurrentUserRole(Role role) {
        User currentUser = findCurrentUser();
        return hasUserRole(currentUser, role);
    }

    public boolean hasUserRole(User user, Role role) {
        return 0 < user.getRoles()
                       .stream()
                       .filter(userRole -> role.equals(userRole.getRole()))
                       .count();
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
        user.getSubjects()
            .forEach(subject -> subject.getUsers()
                                       .remove(user));
        userRepository.delete(user);
    }

    public void removeCourseGroup(CourseGroup courseGroup) {
        List<User> courseGroupUsers = userRepository.findAllByCourseGroups(courseGroup);
        courseGroupUsers.forEach(user -> {
            user.getCourseGroups()
                .remove(courseGroup);
            userRepository.save(user);
        });
    }
}
