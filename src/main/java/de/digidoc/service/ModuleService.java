package de.digidoc.service;

import de.digidoc.model.Module;
import de.digidoc.model.User;
import de.digidoc.repository.ModuleRepository;
import de.digidoc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ModuleService {
    private ModuleRepository moduleRepository;
    private UserRepository userRepository;

    @Autowired
    public ModuleService(ModuleRepository moduleRepository, UserRepository userRepository) {
        this.moduleRepository = moduleRepository;
        this.userRepository = userRepository;
    }

    public Optional<Module> findById(int id) {
        return moduleRepository.findById(id);
    }

    public List<Module> findAllByCourse(Integer courseId) {
        return moduleRepository.findAllByCourseIdOrderByTitle(courseId);
    }

    public List<Module> findAllByParentModule(Integer moduleId) {
        return moduleRepository.findAllByParentIdOrderByTitle(moduleId);
    }

    public Module save(Module module) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        User currentUser = userRepository.findByEmail(principal.getUsername());

        if (null == module.getCreatedBy()) {
            module.setCreatedBy(currentUser);
            module.setCreatedAt(LocalDateTime.now());
        }

        module.setEditedBy(currentUser);
        module.setEditedAt(LocalDateTime.now());

        return moduleRepository.save(module);
    }

    public void delete(Module module) {
        moduleRepository.delete(module);
    }
}
