package de.digi4docs.service;

import de.digi4docs.model.Module;
import de.digi4docs.model.User;
import de.digi4docs.repository.ModuleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class ModuleService {
    private final ModuleRepository moduleRepository;
    private final UserService userService;
    private final TaskService taskService;

    @Autowired
    public ModuleService(ModuleRepository moduleRepository, UserService userService, TaskService taskService) {
        this.moduleRepository = moduleRepository;
        this.userService = userService;
        this.taskService = taskService;
    }

    public Optional<Module> findById(int id) {
        return moduleRepository.findById(id);
    }

    public List<Module> findAllByCourse(Integer courseId) {
        return moduleRepository.findAllByCourseIdOrderByOrderPositionAscTitleAsc(courseId);
    }

    public List<Module> findAllByParentModule(Integer moduleId) {
        return moduleRepository.findAllByParentIdOrderByOrderPositionAscTitleAsc(moduleId);
    }

    public Integer findNextOrder(Module module) {
        Optional<Module> optionalModule;
        if (null == module.getParent()) {
            optionalModule = moduleRepository.findFirstByCourseIdOrderByOrderPositionDesc(module.getCourse().getId());
        } else {
            optionalModule = moduleRepository.findFirstByParentIdOrderByOrderPositionDesc(module.getParent().getId());
        }

        if (optionalModule.isEmpty() || null == optionalModule.get().getOrderPosition()) {
            return 1;
        }

        return optionalModule.get().getOrderPosition() + 1;
    }

    public void orderDown(Module module) {
        if (null == module.getOrderPosition()) {
            save(module);
            return;
        }

        int currentOrderPosition = module.getOrderPosition();
        Optional<Module> optionalNextModule;
        if (null == module.getParent()) {
            optionalNextModule = moduleRepository.findFirstByCourseIdAndOrderPositionGreaterThanOrderByOrderPositionAsc(module.getCourse().getId(), currentOrderPosition);
        } else {
            optionalNextModule = moduleRepository.findFirstByParentIdAndOrderPositionGreaterThanOrderByOrderPositionAsc(module.getParent().getId(), currentOrderPosition);
        }

        optionalNextModule.ifPresent(value -> changeOrderPositions(module, value, currentOrderPosition));
    }

    public void orderUp(Module module) {
        if (null == module.getOrderPosition()) {
            save(module);
            return;
        }

        int currentOrderPosition = module.getOrderPosition();
        Optional<Module> optionalNextModule;
        if (null == module.getParent()) {
            optionalNextModule = moduleRepository.findFirstByCourseIdAndOrderPositionLessThanOrderByOrderPositionDesc(module.getCourse().getId(), currentOrderPosition);
        } else {
            optionalNextModule = moduleRepository.findFirstByParentIdAndOrderPositionLessThanOrderByOrderPositionDesc(module.getParent().getId(), currentOrderPosition);
        }

        optionalNextModule.ifPresent(value -> changeOrderPositions(module, value, currentOrderPosition));
    }

    private void changeOrderPositions(Module currentModule, Module changeModule, int currentOrderPosition) {
        int newOrderPosition = changeModule.getOrderPosition();
        changeModule.setOrderPosition(null);
        moduleRepository.save(changeModule);
        currentModule.setOrderPosition(newOrderPosition);
        moduleRepository.save(currentModule);
        changeModule.setOrderPosition(currentOrderPosition);
        moduleRepository.save(changeModule);
    }

    public Module save(Module module) {
        User currentUser = userService.findCurrentUser();

        if (null == module.getCreatedBy()) {
            module.setCreatedBy(currentUser);
            module.setCreatedAt(LocalDateTime.now());
        }

        if (null == module.getOrderPosition()) {
            module.setOrderPosition(findNextOrder(module));
        }

        module.setEditedBy(currentUser);
        module.setEditedAt(LocalDateTime.now());

        return moduleRepository.save(module);
    }

    public void delete(Module module) {
        moduleRepository.delete(module);
    }
}
