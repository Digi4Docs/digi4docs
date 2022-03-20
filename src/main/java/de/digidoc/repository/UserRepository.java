package de.digidoc.repository;

import de.digidoc.model.Role;
import de.digidoc.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    User findByEmail(String email);

    List<User> findDistinctByRolesRoleOrRolesRoleOrderByLastnameAscFirstnameAsc(Role teacherRole, Role adminRole);

    List<User> findDistinctByRolesRoleOrRolesRoleAndIsActiveTrueOrderByLastnameAscFirstnameAsc(Role teacherRole, Role adminRole);
}
