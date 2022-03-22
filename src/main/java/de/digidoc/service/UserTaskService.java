package de.digidoc.service;

import de.digidoc.model.TaskStatus;
import de.digidoc.model.User;
import de.digidoc.model.UserTask;
import de.digidoc.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Comparator;
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

    public Optional<UserTask> findById(int id) {
        return userTaskRepository.findById(id);
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

    public List<UserTask> findTransmittedOfCurrentUser() {
        User currentUser = userService.findCurrentUser();
        List<UserTask> userTasks = userTaskRepository.findByTeacherIdAndStatus(currentUser.getId(), TaskStatus.TRANSMITTED);
        userTasks.sort(Comparator.comparing(UserTask::getTransmittedAt, Comparator.nullsLast(
                Comparator.naturalOrder())));
        return userTasks;
    }

    public List<UserTask> findAllTransmitted() {
        List<UserTask> userTasks = userTaskRepository.findByStatus(TaskStatus.TRANSMITTED);
        userTasks.sort(Comparator.comparing(UserTask::getTransmittedAt, Comparator.nullsLast(
                Comparator.naturalOrder())));
        return userTasks;
    }

    public List<UserTask> findDoneOfCurrentUser() {
        User currentUser = userService.findCurrentUser();
        List<UserTask> userTasks = userTaskRepository.findByTeacherIdAndStatus(currentUser.getId(), TaskStatus.DONE);
        userTasks.addAll(userTaskRepository.findByTeacherIdAndStatus(currentUser.getId(), TaskStatus.REJECTED));
        userTasks.sort(Comparator.comparing(UserTask::getTransmittedAt, Comparator.nullsLast(
                Comparator.naturalOrder())));
        return userTasks;
    }

    public List<UserTask> findAllDone() {
        List<UserTask> userTasks = userTaskRepository.findByStatus(TaskStatus.DONE);
        userTasks.sort(Comparator.comparing(UserTask::getTransmittedAt, Comparator.nullsLast(
                Comparator.naturalOrder())));
        return userTasks;
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
