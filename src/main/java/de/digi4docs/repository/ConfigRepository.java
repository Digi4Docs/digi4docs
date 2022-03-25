package de.digi4docs.repository;

import de.digi4docs.model.Config;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Controller;

import java.util.Optional;

@Controller
public interface ConfigRepository extends JpaRepository<Config, Integer> {
    Optional<Config> findByConfigKey(String key);
}
