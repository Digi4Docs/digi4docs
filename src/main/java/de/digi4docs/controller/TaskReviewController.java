package de.digi4docs.controller;

import de.digi4docs.form.TaskReviewForm;
import de.digi4docs.form.TasksReviewMultipleForm;
import de.digi4docs.model.Role;
import de.digi4docs.model.TaskStatus;
import de.digi4docs.model.User;
import de.digi4docs.model.UserTask;
import de.digi4docs.service.SubjectService;
import de.digi4docs.service.UserService;
import de.digi4docs.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
public class TaskReviewController extends AbstractController {

    private static int FORM_ACTION_SAVE = 1;
    private static int FORM_ACTION_DONE = 2;
    private static int FORM_ACTION_REJECT = 3;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTaskService userTaskService;

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/tasks")
    public String tasks(Model model, TasksReviewMultipleForm tasksReviewMultipleForm) {
        model.addAttribute("userTasks", userTaskService.findTransmittedOfCurrentUser());
        model.addAttribute("showAll", false);
        model.addAttribute("showDone", false);

        getBreadcrumbs(true).put("/tasks", "Meine Aufgaben");
        showBreadcrumbs(model);

        return "taskReview/tasks";
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/tasks/edit")
    public String tasksEdit(Model model, TasksReviewMultipleForm tasksReviewMultipleForm) {
        model.addAttribute("editTasks", true);
        return tasks(model, tasksReviewMultipleForm);
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @PostMapping("/tasks/edit")
    public String tasksEditSubmit(TasksReviewMultipleForm tasksReviewMultipleForm) {
        List<UserTask> userTasks = userTaskService.findByIds(tasksReviewMultipleForm.getUserTaskIds());
        if (null != userTasks && !userTasks.isEmpty()) {
            userTaskService.setAllDone(userTasks);
        }

        return "redirect:/tasks";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/tasks/all")
    public String tasksAll(Model model) {
        model.addAttribute("userTasks", userTaskService.findAllTransmitted());
        model.addAttribute("showAll", true);
        model.addAttribute("showDone", false);

        getBreadcrumbs(true).put("/tasks/all", "Alle offenen Aufgaben");
        showBreadcrumbs(model);

        return "taskReview/tasks";
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/tasks/done")
    public String tasksDone(Model model) {
        model.addAttribute("userTasks", userTaskService.findDoneOfCurrentUser());
        model.addAttribute("showAll", false);
        model.addAttribute("showDone", true);

        getBreadcrumbs(true).put("/tasks/done", "Meine erledigten Aufgaben");
        showBreadcrumbs(model);

        return "taskReview/tasks";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/tasks/done-all")
    public String tasksAllDone(Model model) {
        model.addAttribute("userTasks", userTaskService.findAllDone());
        model.addAttribute("showAll", true);
        model.addAttribute("showDone", true);

        getBreadcrumbs(true).put("/tasks/done-all", "Alle erledigten Aufgaben");
        showBreadcrumbs(model);

        return "taskReview/tasks";
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/tasks/task/{id}")
    public String task(@PathVariable int id, TaskReviewForm taskReviewForm, Model model) {
        return showTaskPage(id, taskReviewForm, model, true);
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @PostMapping(value = "/tasks/task/{id}", params = "save")
    public String saveTask(@PathVariable int id, @Valid TaskReviewForm taskReviewForm, BindingResult bindingResult,
            Model model) {
        return saveTask(id, taskReviewForm, bindingResult, model, FORM_ACTION_SAVE);
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @PostMapping(value = "/tasks/task/{id}", params = "done")
    public String doneTask(@PathVariable int id, @Valid TaskReviewForm taskReviewForm, BindingResult bindingResult,
            Model model) {
        return saveTask(id, taskReviewForm, bindingResult, model, FORM_ACTION_DONE);
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @PostMapping(value = "/tasks/task/{id}", params = "reject")
    public String rejectTask(@PathVariable int id, @Valid TaskReviewForm taskReviewForm, BindingResult bindingResult,
            Model model) {
        return saveTask(id, taskReviewForm, bindingResult, model, FORM_ACTION_REJECT);
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @PostMapping(value = "/tasks/task/{id}", params = "back")
    public String getBackTask(@PathVariable int id, @Valid TaskReviewForm taskReviewForm, BindingResult bindingResult,
            Model model) {

        Optional<UserTask> userTaskOptional = userTaskService.findById(id);
        if (userTaskOptional.isEmpty()) {
            return "redirect:/tasks";
        }

        UserTask userTask = userTaskOptional.get();
        userTask.setStatus(TaskStatus.TRANSMITTED);
        save(userTask, model);
        return "redirect:/tasks/task/" + id;
    }

    private String showTaskPage(int id, TaskReviewForm taskReviewForm, Model model, boolean initFormData) {
        Optional<UserTask> userTaskOptional = userTaskService.findById(id);
        if (userTaskOptional.isEmpty()) {
            return "redirect:/tasks";
        }

        UserTask userTask = userTaskOptional.get();

        boolean isAdmin = userService.hasCurrentUserRole(Role.ADMIN);
        User currentUser = userService.findCurrentUser();
        boolean isOwnTask = userTask.getTeacher()
                                    .getId()
                                    .equals(currentUser.getId());
        model.addAttribute("isOwnTask", isOwnTask);

        if (!isAdmin && !isOwnTask) {
            return "redirect:/tasks";
        }


        if (initFormData) {
            taskReviewForm.setTeacherId(userTask.getTeacher()
                                                .getId());
            taskReviewForm.setSubjectId(userTask.getSubject()
                                                .getId());
            taskReviewForm.setSolution(userTask.getSolution());
            taskReviewForm.setDate(userTask.getDate()
                                           .toString());
            taskReviewForm.setComment(userTask.getComment());
        }

        model.addAttribute("userTask", userTask);
        model.addAttribute("subjects", subjectService.findAllActive());
        model.addAttribute("teachers", userService.findAllActiveTeachers());
        model.addAttribute("allowedForAdmins", isAdmin);

        return "taskReview/task";
    }

    private String saveTask(@PathVariable int id, @Valid TaskReviewForm taskReviewForm, BindingResult bindingResult,
            Model model, int formAction) {
        if (userService.hasCurrentUserRole(Role.ADMIN) && bindingResult.hasErrors()) {
            return task(id, taskReviewForm, model);
        }

        Optional<UserTask> userTaskOptional = userTaskService.findById(id);
        if (userTaskOptional.isEmpty()) {
            return "redirect:/tasks";
        }

        UserTask userTask = userTaskOptional.get();
        userTask.setComment(taskReviewForm.getComment());
        if (userService.hasCurrentUserRole(Role.ADMIN)) {
            userTask.setDate(LocalDate.parse(taskReviewForm.getDate()));
            userTask.setTeacher(userService.findById(taskReviewForm.getTeacherId())
                                           .get());
            userTask.setSubject(subjectService.findById(taskReviewForm.getSubjectId())
                                              .get());
            userTask.setSolution(taskReviewForm.getSolution());
        }

        if (formAction == FORM_ACTION_DONE) {
            userTask.setStatus(TaskStatus.DONE);
            userTask.setDoneAt(LocalDateTime.now());
        }

        if (formAction == FORM_ACTION_REJECT) {
            userTask.setStatus(TaskStatus.REJECTED);
            userTask.setRejectedAt(LocalDateTime.now());
        }

        boolean successful = save(userTask, model);

        if (formAction == FORM_ACTION_SAVE || !successful) {
            return showTaskPage(id, taskReviewForm, model, false);
        }

        return "redirect:/tasks/task/" + id;
    }

    private boolean save(UserTask userTask, Model model) {
        boolean successful = false;
        try {
            userTaskService.save(userTask);
            model.addAttribute("success", true);
            successful = true;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return successful;
    }
}
