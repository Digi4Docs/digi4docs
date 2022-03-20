package de.digidoc.controller;

import de.digidoc.model.UserTask;
import de.digidoc.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.Optional;

@Controller
public class TaskReviewController {
    @Autowired
    private UserTaskService userTaskService;

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/tasks")
    public String tasks(Model model) {
        model.addAttribute("userTasks", userTaskService.findTransmittedToUser());
        model.addAttribute("showAll", false);
        model.addAttribute("showDone", false);
        return "taskReview/tasks";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/tasks/all")
    public String tasksAll(Model model) {
        model.addAttribute("userTasks", userTaskService.findAllTransmitted());
        model.addAttribute("showAll", true);
        model.addAttribute("showDone", false);
        return "taskReview/tasks";
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/tasks/done")
    public String tasksDone(Model model) {
        model.addAttribute("userTasks", userTaskService.findDoneToUser());
        model.addAttribute("showAll", false);
        model.addAttribute("showDone", true);
        return "taskReview/tasks";
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/tasks/task/{id}")
    public String tasksDone(@PathVariable int id, Model model) {
        Optional<UserTask> userTaskOptional = userTaskService.findById(id);
        if (userTaskOptional.isEmpty()){
            return "redirect: /tasks";
        }
        model.addAttribute("userTask", userTaskOptional.get());
        return "taskReview/task";
    }
}
