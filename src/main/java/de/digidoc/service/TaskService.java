package de.digidoc.service;

import de.digidoc.model.Task;
import de.digidoc.model.User;
import de.digidoc.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class TaskService {
    private TaskRepository taskRepository;
    private UserService userService;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public Optional<Task> findById(int id) {
        return taskRepository.findById(id);
    }

    public List<Task> findAllByModule(Integer moduleId) {
        return taskRepository.findAllByModuleIdOrderByTitle(moduleId);
    }

    public List<Task> findAllActiveByModule(Integer moduleId) {
        return taskRepository.findAllByModuleIdOrderByTitle(moduleId);
    }

    public Task save(Task task) {
        User currentUser = userService.findCurrentUser();

        if (null == task.getCreatedBy()) {
            task.setCreatedBy(currentUser);
            task.setCreatedAt(LocalDateTime.now());
        }

        task.setEditedBy(currentUser);
        task.setEditedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }

    public void delete(Task module) {
        taskRepository.delete(module);
    }
}
