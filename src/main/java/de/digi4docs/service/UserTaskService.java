package de.digi4docs.service;

import de.digi4docs.dto.TaskDoneCountResult;
import de.digi4docs.dto.TaskReviewRow;
import de.digi4docs.model.TaskStatus;
import de.digi4docs.model.User;
import de.digi4docs.model.UserTask;
import de.digi4docs.repository.UserTaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
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
        return findByTasks(taskIds, currentUser);
    }

    public List<UserTask> findByTasks(List<Integer> taskIds, User user) {
        return userTaskRepository.findByTaskIdInAndUserId(taskIds, user.getId());
    }

    public List<UserTask> findByIds(List<Integer> userTaskIds) {
        return userTaskRepository.findByIdIn(userTaskIds);
    }

    public List<UserTask> findByUser() {
        User currentUser = userService.findCurrentUser();
        return userTaskRepository.findByUserId(currentUser.getId());
    }

    public List<UserTask> findByUser(int userId) {
        return userTaskRepository.findByUserId(userId);
    }

    public List<TaskReviewRow> findTransmittedOfCurrentUser() {
        User currentUser = userService.findCurrentUser();
        List<TaskReviewRow> userTasks =
                userTaskRepository.findDtoByTeacherIdAndStatus(currentUser.getId(), TaskStatus.TRANSMITTED);
        userTasks.sort(Comparator.comparing(TaskReviewRow::getTransmittedAt, Comparator.nullsLast(
                Comparator.naturalOrder())));
        return userTasks;
    }

    public List<TaskReviewRow> findAllTransmitted() {
        List<TaskReviewRow> userTasks = userTaskRepository.findDtoByStatus(TaskStatus.TRANSMITTED);
        userTasks.sort(Comparator.comparing(TaskReviewRow::getTransmittedAt, Comparator.nullsLast(
                Comparator.naturalOrder())));
        return userTasks;
    }

    public List<TaskReviewRow> findDoneOfCurrentUser() {
        User currentUser = userService.findCurrentUser();
        List<TaskReviewRow> userTasks =
                userTaskRepository.findDtoByTeacherIdAndStatus(currentUser.getId(), TaskStatus.DONE);
        userTasks.addAll(userTaskRepository.findDtoByTeacherIdAndStatus(currentUser.getId(), TaskStatus.REJECTED));
        userTasks.sort(Comparator.comparing(TaskReviewRow::getTransmittedAt, Comparator.nullsLast(
                Comparator.naturalOrder())));
        return userTasks;
    }

    public List<TaskReviewRow> findAllDone() {
        List<TaskReviewRow> tasks = userTaskRepository.findDtoByStatus(TaskStatus.DONE);
        tasks.addAll(userTaskRepository.findDtoByStatus(TaskStatus.REJECTED));
        tasks.sort(Comparator.comparing(TaskReviewRow::getTransmittedAt, Comparator.nullsLast(
                Comparator.naturalOrder())));
        return tasks;
    }

    public List<TaskDoneCountResult> findDoneTaskCount(List<Integer> taskIds, String start, String end) {
        LocalDateTime localDateStart = LocalDate.parse(start)
                                                .atStartOfDay();
        LocalDateTime localDateEnd = LocalDate.parse(end)
                                              .atTime(LocalTime.MAX);
        return userTaskRepository.findTaskCountGroupedByYear(taskIds, localDateStart, localDateEnd);
    }

    public List<UserTask> findRejectedOfCurrentUser() {
        User currentUser = userService.findCurrentUser();
        List<UserTask> userTasks = userTaskRepository.findByUserIdAndStatus(currentUser.getId(), TaskStatus.REJECTED);
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

    public List<UserTask> setAllDone(List<UserTask> userTasks) {
        for (UserTask userTask : userTasks) {
            userTask.setStatus(TaskStatus.DONE);
            userTask.setDoneAt(LocalDateTime.now());
        }
        return userTaskRepository.saveAll(userTasks);
    }
}
