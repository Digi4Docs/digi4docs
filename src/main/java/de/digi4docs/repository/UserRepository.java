package de.digi4docs.repository;

import de.digi4docs.dto.StudentCountResult;
import de.digi4docs.model.CourseGroup;
import de.digi4docs.model.Role;
import de.digi4docs.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    Optional<User> findByEmailAndIsActiveTrue(String email);

    List<User> findDistinctByRolesRoleOrRolesRoleOrderByLastnameAscFirstnameAsc(Role teacherRole, Role adminRole);

    List<User> findDistinctByRolesRoleOrRolesRoleAndIsActiveTrueOrderByLastnameAscFirstnameAsc(Role teacherRole,
            Role adminRole);

    @Query("SELECT COUNT(u.id) as total, u.classYear as year FROM User u, UserRole ur WHERE ur.user.id = u.id AND ur" +
            ".role = 'STUDENT' AND u.isActive = true AND u.classYear IS NOT NULL GROUP BY u.classYear")
    List<StudentCountResult> findStudentCountGroupedByYear();

    List<User> findAllByCourseGroups(CourseGroup courseGroup);
}
