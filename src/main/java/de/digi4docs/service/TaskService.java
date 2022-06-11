package de.digi4docs.service;

import de.digi4docs.model.Task;
import de.digi4docs.model.User;
import de.digi4docs.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserService userService;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserService userService) {
        this.taskRepository = taskRepository;
        this.userService = userService;
    }

    public Optional<Task> findById(int id) {
        return taskRepository.findById(id);
    }

    public List<Task> findAllByModule(Integer moduleId) {
        return taskRepository.findAllByModuleIdOrderByOrderPositionAscTitleAsc(moduleId);
    }

    @Nullable
    public Task findNextTask(Integer moduleId, Integer currentTaskPosition) {
        Optional<Task> task =
                taskRepository.findFirstByModuleIdAndIsActiveTrueAndOrderPositionGreaterThanOrderByOrderPositionAsc(moduleId,
                        currentTaskPosition);
        return task.orElse(null);
    }

    @Nullable
    public Task findPreviousTask(Integer moduleId, Integer currentTaskPosition) {
        Optional<Task> task =
                taskRepository.findFirstByModuleIdAndIsActiveTrueAndOrderPositionLessThanOrderByOrderPositionDesc(moduleId,
                        currentTaskPosition);
        return task.orElse(null);
    }

    public Integer findNextOrder(Integer moduleId) {
        Optional<Task> task = taskRepository.findFirstByModuleIdOrderByOrderPositionDesc(moduleId);
        if (task.isEmpty() || null == task.get()
                                          .getOrderPosition()) {
            return 1;
        }

        return task.get()
                   .getOrderPosition() + 1;
    }

    public void orderDown(Task task) {
        if (null == task.getOrderPosition()) {
            save(task);
            return;
        }

        int currentOrderPosition = task.getOrderPosition();
        Optional<Task> optionalNextTask =
                taskRepository.findFirstByModuleIdAndOrderPositionGreaterThanOrderByOrderPositionAsc(task.getModule()
                                                                                                         .getId(),
                        currentOrderPosition);

        if (optionalNextTask.isPresent()) {
            int newOrderPosition = optionalNextTask.get()
                                                   .getOrderPosition();
            optionalNextTask.get()
                            .setOrderPosition(null);
            taskRepository.save(optionalNextTask.get());
            task.setOrderPosition(newOrderPosition);
            taskRepository.save(task);
            optionalNextTask.get()
                            .setOrderPosition(currentOrderPosition);
            taskRepository.save(optionalNextTask.get());
        }
    }

    public void orderUp(Task task) {
        if (null == task.getOrderPosition()) {
            save(task);
            return;
        }

        int currentOrderPosition = task.getOrderPosition();
        Optional<Task> optionalNextTask =
                taskRepository.findFirstByModuleIdAndOrderPositionLessThanOrderByOrderPositionDesc(task.getModule()
                                                                                                       .getId(),
                        currentOrderPosition);

        if (optionalNextTask.isPresent()) {
            int newOrderPosition = optionalNextTask.get()
                                                   .getOrderPosition();
            optionalNextTask.get()
                            .setOrderPosition(null);
            taskRepository.save(optionalNextTask.get());
            task.setOrderPosition(newOrderPosition);
            taskRepository.save(task);
            optionalNextTask.get()
                            .setOrderPosition(currentOrderPosition);
            taskRepository.save(optionalNextTask.get());
        }
    }

    @Transactional
    public Task save(Task task) {
        User currentUser = userService.findCurrentUser();

        if (null == task.getCreatedBy()) {
            task.setCreatedBy(currentUser);
            task.setCreatedAt(LocalDateTime.now());
        }

        if (null == task.getOrderPosition()) {
            task.setOrderPosition(findNextOrder(task.getModule()
                                                    .getId()));
        }

        task.setEditedBy(currentUser);
        task.setEditedAt(LocalDateTime.now());

        return taskRepository.save(task);
    }

    public void delete(Task task) {
        taskRepository.delete(task);
    }
}
