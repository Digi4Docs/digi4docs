package de.digi4docs.repository;

import de.digi4docs.model.PasswordForgotten;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordForgottenRepository extends JpaRepository<PasswordForgotten, Integer> {
    Optional<PasswordForgotten> findByHash(String hash);

    Optional<PasswordForgotten> findByUserId(int userId);
}
