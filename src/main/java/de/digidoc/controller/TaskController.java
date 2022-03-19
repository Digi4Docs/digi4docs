package de.digidoc.controller;

import de.digidoc.form.ModuleForm;
import de.digidoc.form.TaskForm;
import de.digidoc.model.Module;
import de.digidoc.model.Task;
import de.digidoc.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class TaskController extends AbstractModuleController {
    @Autowired
    private TaskService taskService;


    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/module/{id}/tasks")
    public String tasks(@PathVariable int id, Model model) {
        Module parentModule = moduleService.findById(id).get();
        model.addAttribute("parentModule", parentModule);
        model.addAttribute("tasks", taskService.findAllByModule(parentModule.getId()));
        model.addAttribute("course", parentModule.getCourse());

        initBreadcrumbModuleEntries(parentModule);
        getBreadcrumbs().put("/module/" + id + "/tasks", "Aufgaben");
        showBreadcrumbs(model);

        return "task/tasks";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/module/{id}/task")
    public String task(@PathVariable int id, TaskForm taskForm, Model model) {
        Module module = moduleService.findById(id).get();
        model.addAttribute("parentModule", module);

        initBreadcrumbModuleEntries(module).showBreadcrumbs(model);

        return "task/new-task";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/module/{id}/task")
    public String add(@PathVariable int id, @Valid TaskForm taskForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return task(id, taskForm, model);
        }

        Module parentModule = moduleService.findById(id).get();
        Task task = mapFormToTask(new Task(), taskForm);
        task.setModule(parentModule);

        try {
            taskService.save(task);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return task(id, taskForm, model);
        }

        return "redirect:/module/" + parentModule.getId() + "/tasks";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/module/{parentId}/task/{id}")
    public String task(@PathVariable int parentId, @PathVariable int id, TaskForm taskForm, Model model) {
        return showDetailPage(parentId, id, taskForm, model, true);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/module/{parentId}/task/{id}")
    public String update(@PathVariable int parentId, @PathVariable int id, @Valid TaskForm taskForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return showDetailPage(parentId, id, taskForm, model, false);
        }

        Optional<Task> taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            return "redirect:/module/" + parentId + "/tasks";
        }

        Task task = mapFormToTask(taskOptional.get(), taskForm);

        try {
            taskService.save(task);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return showDetailPage(parentId, id, taskForm, model, false);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/module/{parentId}/task/confirm-delete/{id}")
    public String confirmDelete(@PathVariable int parentId, @PathVariable int id, Model model) {
        Optional<Task> taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            return "redirect:/module/" + parentId + "/task";
        }

        Task task = taskOptional.get();
        model.addAttribute("task", task);
        model.addAttribute("parentModule", moduleService.findById(parentId).get());

        initBreadcrumbModuleEntries(task.getModule());
        getBreadcrumbs().put("/module/" + task.getModule().getId() + "/task/" + task.getId(), task.getTitle());
        getBreadcrumbs().put("/module/" + parentId + "/task/confirm-delete/" + id, "Modul löschen");
        showBreadcrumbs(model);

        return "task/delete";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/module/{parentId}/task/delete/{id}")
    public String delete(@PathVariable int parentId, @PathVariable int id) {
        Optional<Task> taskOptional = taskService.findById(id);
        if (taskOptional.isEmpty()) {
            return "redirect:/module/" + parentId + "/tasks";
        }

        Task task = taskOptional.get();
        taskService.delete(task);

        return "redirect:/module/" + parentId + "/tasks";
    }

    private String showDetailPage(int parentId, int id, TaskForm taskForm, Model model, boolean initFormData) {

        Optional<Task> taskOptional = taskService.findById(id);

        if (taskOptional.isEmpty()) {
            return "redirect:/module/" + parentId + "/tasks";
        }

        Task task = taskOptional.get();

        if (initFormData) {
            taskForm.setId(task.getId());
            taskForm.setTitle(task.getTitle());
            taskForm.setDescription(task.getDescription());
            taskForm.setIsActive(task.getIsActive());
        }

        model.addAttribute("task", task);
        model.addAttribute("parentModule", task.getModule());
        model.addAttribute("course", task.getModule().getCourse());

        initBreadcrumbModuleEntries(task.getModule());
        getBreadcrumbs().put("/module/" + task.getModule().getId() + "/task/" + task.getId(), task.getTitle());
        showBreadcrumbs(model);

        return "task/task";
    }

    protected Task mapFormToTask(Task task, TaskForm taskForm) {
        task.setTitle(taskForm.getTitle());
        task.setDescription(taskForm.getDescription());
        task.setIsActive(taskForm.getIsActive());

        return task;
    }
}
