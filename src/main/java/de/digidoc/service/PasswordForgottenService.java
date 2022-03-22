package de.digidoc.service;

import de.digidoc.model.PasswordForgotten;
import de.digidoc.repository.PasswordForgottenRepository;
import de.digidoc.util.HashGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class PasswordForgottenService {
    private final PasswordForgottenRepository passwordForgottenRepository;
    private HashGenerator hashGenerator;

    @Autowired
    public PasswordForgottenService(PasswordForgottenRepository passwordForgottenRepository, HashGenerator hashGenerator) {
        this.passwordForgottenRepository = passwordForgottenRepository;
        this.hashGenerator = hashGenerator;
    }

    public Optional<PasswordForgotten> findByHash(String hash) {
        return passwordForgottenRepository.findByHash(hash);
    }

    public Optional<PasswordForgotten> findByUserId(int userId) {
        return passwordForgottenRepository.findByUserId(userId);
    }

    public PasswordForgotten save(PasswordForgotten passwordForgotten) {
        passwordForgotten.setExpirationAt(LocalDateTime.now().plusHours(1));
        passwordForgotten.setHash(hashGenerator.generateRandomHash());
        return passwordForgottenRepository.save(passwordForgotten);
    }

    public void delete(PasswordForgotten passwordForgotten) {
        passwordForgottenRepository.delete(passwordForgotten);
    }
}
