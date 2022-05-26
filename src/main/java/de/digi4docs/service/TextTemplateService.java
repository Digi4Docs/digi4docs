package de.digi4docs.service;

import de.digi4docs.model.TextTemplate;
import de.digi4docs.model.User;
import de.digi4docs.repository.TextTemplateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class TextTemplateService {
    private TextTemplateRepository textTemplateRepository;
    private UserService userService;

    @Autowired
    public TextTemplateService(TextTemplateRepository textTemplateRepository, UserService userService) {
        this.textTemplateRepository = textTemplateRepository;
        this.userService = userService;
    }

    public Optional<TextTemplate> findById(int id) {
        User currentUser = userService.findCurrentUser();
        return textTemplateRepository.findByIdAndUserId(id, currentUser.getId());
    }

    public List<TextTemplate> findAll() {
        User currentUser = userService.findCurrentUser();
        return textTemplateRepository.findAllByUserIdOrderByText(currentUser.getId());
    }

    public TextTemplate save(TextTemplate textTemplate) {
        User currentUser = userService.findCurrentUser();
        if (null != textTemplate.getId() && !currentUser.getId()
                                                        .equals(textTemplate.getUser()
                                                                            .getId())) {
            return null;
        }

        textTemplate.setUser(currentUser);

        return textTemplateRepository.save(textTemplate);
    }

    public void delete(TextTemplate textTemplate) {
        User currentUser = userService.findCurrentUser();
        if (currentUser.getId()
                       .equals(textTemplate.getUser()
                                           .getId())) {
            textTemplateRepository.delete(textTemplate);
        }
    }
}
