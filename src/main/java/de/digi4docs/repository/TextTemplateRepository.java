package de.digi4docs.repository;

import de.digi4docs.model.TextTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TextTemplateRepository extends JpaRepository<TextTemplate, Integer> {
    List<TextTemplate> findAllByUserIdOrderByText(int userId);

    Optional<TextTemplate> findByIdAndUserId(int id, int userId);
}
