package de.digidoc.service;

import de.digidoc.model.Module;
import de.digidoc.model.*;
import de.digidoc.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.*;

@Component
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final UserService userService;
    private final UserTaskService userTaskService;

    @Autowired
    public ModuleService(ModuleRepository moduleRepository, UserService userService, UserTaskService userTaskService) {
        this.moduleRepository = moduleRepository;
        this.userService = userService;
        this.userTaskService = userTaskService;
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
        User currentUser = userService.findCurrentUser();

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
