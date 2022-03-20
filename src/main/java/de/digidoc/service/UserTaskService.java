package de.digidoc.service;

import de.digidoc.model.TaskStatus;
import de.digidoc.model.User;
import de.digidoc.model.UserTask;
import de.digidoc.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class UserTaskService {
    private UserTaskRepository userTaskRepository;
    private UserService userService;

    @Autowired
    public UserTaskService(UserTaskRepository userTaskRepository, UserService userService) {
        this.userTaskRepository = userTaskRepository;
        this.userService = userService;
    }

    public Optional<UserTask> findByTask(int taskId) {
        User currentUser = userService.findCurrentUser();
        return userTaskRepository.findByTaskIdAndUserId(taskId, currentUser.getId());
    }

    public List<UserTask> findByTasks(List<Integer> taskIds) {
        User currentUser = userService.findCurrentUser();
        return userTaskRepository.findByTaskIdInAndUserId(taskIds, currentUser.getId());
    }

    public List<UserTask> findByUser() {
        User currentUser = userService.findCurrentUser();
        return userTaskRepository.findByUserId(currentUser.getId());
    }

    public UserTask save(UserTask userTask) {
        if (null == userTask.getUser()) {
            userTask.setUser(userService.findCurrentUser());
        }
        if (null == userTask.getStatus()) {
            userTask.setStatus(TaskStatus.OPEN);
        }

        return userTaskRepository.save(userTask);
    }
}
