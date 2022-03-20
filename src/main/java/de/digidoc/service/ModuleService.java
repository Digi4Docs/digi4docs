package de.digidoc.service;

import de.digidoc.model.Module;
import de.digidoc.model.User;
import de.digidoc.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ModuleService {
    private ModuleRepository moduleRepository;
    private UserService userService;

    @Autowired
    public ModuleService(ModuleRepository moduleRepository, UserService userService) {
        this.moduleRepository = moduleRepository;
        this.userService = userService;
    }

    public Optional<Module> findById(int id) {
        return moduleRepository.findById(id);
    }

    public List<Module> findAllByCourse(Integer courseId) {
        return moduleRepository.findAllByCourseIdOrderByTitle(courseId);
    }

    public List<Module> findAllActiveByCourse(Integer courseId) {
        return moduleRepository.findAllByCourseIdAndIsActiveTrueOrderByTitle(courseId);
    }

    public List<Module> findAllByParentModule(Integer moduleId) {
        return moduleRepository.findAllByParentIdOrderByTitle(moduleId);
    }

    public List<Module> findAllActiveByParentModule(Integer moduleId) {
        return moduleRepository.findAllByParentIdAndIsActiveTrueOrderByTitle(moduleId);
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
