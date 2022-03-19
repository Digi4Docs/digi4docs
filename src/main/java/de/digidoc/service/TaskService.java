package de.digidoc.service;

import de.digidoc.model.Task;
import de.digidoc.model.User;
import de.digidoc.repository.TaskRepository;
import de.digidoc.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class TaskService {
    private TaskRepository taskRepository;
    private UserRepository userRepository;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    public Optional<Task> findById(int id) {
        return taskRepository.findById(id);
    }

    public List<Task> findAllByModule(Integer moduleId) {
        return taskRepository.findAllByModuleIdOrderByTitle(moduleId);
    }

    public Task save(Task task) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        User currentUser = userRepository.findByEmail(principal.getUsername());

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
