package de.digidoc.controller;

import de.digidoc.form.PublicTaskForm;
import de.digidoc.model.Module;
import de.digidoc.model.*;
import de.digidoc.service.*;
import de.digidoc.util.ProgressCountProvider;
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
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class PublicCourseController extends AbstractController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private ProgressCountProvider progressCountProvider;

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/public/course/{id}")
    public String course(@PathVariable int id, Model model) {
        Optional<Course> courseOptional = courseService.findById(id);

        if (!courseOptional.isPresent() || !courseOptional.get().getIsActive()) {
            return "redirect:/home";
        }

        Course course = courseOptional.get();
        model.addAttribute("course", course);
        Map<Integer, Integer> personalModuleTaskCountMap = progressCountProvider.getPersonalModuleTaskCountMap(course.getModules());
        model.addAttribute("personalModuleTaskTotal", personalModuleTaskCountMap.values().stream().reduce(0, Integer::sum));
        model.addAttribute("personalModuleTaskCounts", personalModuleTaskCountMap);

        Map<Integer, Integer> generalModuleTaskCountMap = progressCountProvider.getGeneralModuleTaskCountMap(course.getModules());
        model.addAttribute("generalModuleTaskTotal", generalModuleTaskCountMap.values().stream().reduce(0, Integer::sum));
        model.addAttribute("generalModuleTaskCounts", generalModuleTaskCountMap);

        showBreadcrumbs(course, null, model);

        return "public/course";
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/public/course/{id}/module/{moduleId}")
    public String module(@PathVariable int id, @PathVariable int moduleId, Model model) {
        Optional<Course> courseOptional = courseService.findById(id);

        if (!courseOptional.isPresent() || !courseOptional.get().getIsActive()) {
            return "redirect:/home";
        }

        Course course = courseOptional.get();

        Optional<Module> moduleOptional = moduleService.findById(moduleId);

        if (!moduleOptional.isPresent() || !moduleOptional.get().getIsActive()) {
            if (null != moduleOptional.get().getParent()) {
                return "redirect:/public/course/" + course.getId() + "/module/" + moduleOptional.get().getParent().getId();
            }
            return "redirect:/public/course/" + course.getId();
        }

        Module module = moduleOptional.get();

        model.addAttribute("course", course);
        model.addAttribute("module", module);

        Map<Integer, Integer> personalModuleTaskCountMap = progressCountProvider.getPersonalModuleTaskCountMap(module.getModules());
        model.addAttribute("personalModuleTaskTotal", personalModuleTaskCountMap.values().stream().reduce(0, Integer::sum));
        model.addAttribute("personalModuleTaskCounts", personalModuleTaskCountMap);

        Map<Integer, Integer> generalModuleTaskCountMap = progressCountProvider.getGeneralModuleTaskCountMap(module.getModules());
        model.addAttribute("generalModuleTaskTotal", generalModuleTaskCountMap.values().stream().reduce(0, Integer::sum));
        model.addAttribute("generalModuleTaskCounts", generalModuleTaskCountMap);

        if (!module.getTasks().isEmpty()) {
            Map<Integer, UserTask> userTasks = userTaskService.findByTasks(module.getTasks().stream().map(Task::getId).collect(Collectors.toList())).stream().collect(Collectors.toMap(userTask -> userTask.getTask().getId(), userTask -> userTask));
            model.addAttribute("userTasks", userTasks);
            model.addAttribute("userTasksDone", userTasks.values().stream().filter(userTask -> TaskStatus.DONE.equals(userTask.getStatus())).count());
        }

        showBreadcrumbs(course, module, model);

        return "public/module";
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/public/course/{id}/task/{taskId}")
    public String task(@PathVariable int id, @PathVariable int taskId, PublicTaskForm publicTaskForm, Model model) {
        return showTaskPage(id, taskId, model, publicTaskForm, true);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping(value = "/public/course/{id}/task/{taskId}", params = "transmit")
    public String transmitTask(@PathVariable int id, @PathVariable int taskId, @Valid PublicTaskForm publicTaskForm, BindingResult bindingResult, Model model) {
        return saveTask(id, taskId, publicTaskForm, bindingResult, model, true);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping(value = "/public/course/{id}/task/{taskId}", params = "save")
    public String saveTask(@PathVariable int id, @PathVariable int taskId, @Valid PublicTaskForm publicTaskForm, BindingResult bindingResult, Model model) {
        return saveTask(id, taskId, publicTaskForm, bindingResult, model, false);
    }

    private String saveTask(@PathVariable int id, @PathVariable int taskId, @Valid PublicTaskForm publicTaskForm, BindingResult bindingResult, Model model, boolean doTransmit) {
        if (bindingResult.hasErrors()) {
            return task(id, taskId, publicTaskForm, model);
        }

        Optional<Course> courseOptional = courseService.findById(id);
        if (!courseOptional.isPresent() || !courseOptional.get().getIsActive()) {
            return "redirect:/home";
        }

        Optional<Task> taskOptional = taskService.findById(taskId);
        if (!taskOptional.isPresent() || !taskOptional.get().getIsActive()) {
            return "redirect:/home";
        }

        Task task = taskOptional.get();
        Module module = task.getModule();

        if (!module.getIsActive()) {
            return "redirect:/home";
        }

        Optional<UserTask> userTaskOptional = userTaskService.findByTask(taskId);
        UserTask userTask = userTaskOptional.isPresent() ? userTaskOptional.get() : new UserTask();
        if (null == userTask.getTask()) {
            userTask.setTask(task);
        }

        userTask.setDate(LocalDate.parse(publicTaskForm.getDate()));
        userTask.setTeacher(userService.findById(publicTaskForm.getTeacherId()).get());
        userTask.setSubject(subjectService.findById(publicTaskForm.getSubjectId()).get());
        userTask.setSolution(publicTaskForm.getSolution());

        if (doTransmit) {
            userTask.setStatus(TaskStatus.TRANSMITTED);
            userTask.setTransmittedAt(LocalDateTime.now());
        }

        boolean successful = false;
        try {
            userTaskService.save(userTask);
            model.addAttribute("success", true);
            successful = true;
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        if (doTransmit && successful) {
            return "redirect:/public/course/" + courseOptional.get().getId() + "/task/" + task.getId();
        }

        return showTaskPage(id, taskId, model, publicTaskForm, false);
    }

    private String showTaskPage(int courseId, int taskId, Model model, PublicTaskForm publicTaskForm, boolean initFormData) {
        Optional<Course> courseOptional = courseService.findById(courseId);
        if (!courseOptional.isPresent() || !courseOptional.get().getIsActive()) {
            return "redirect:/home";
        }

        Optional<Task> taskOptional = taskService.findById(taskId);
        if (!taskOptional.isPresent() || !taskOptional.get().getIsActive()) {
            return "redirect:/home";
        }

        Course course = courseOptional.get();
        Task task = taskOptional.get();
        Module module = task.getModule();

        if (!module.getIsActive()) {
            return "redirect:/home";
        }

        Optional<UserTask> userTaskOptional = userTaskService.findByTask(taskId);
        model.addAttribute("taskStatus", userTaskOptional.isPresent() ? userTaskOptional.get().getStatus() : TaskStatus.OPEN);
        model.addAttribute("userTask", userTaskOptional.orElse(null));

        if (initFormData) {
            if (userTaskOptional.isPresent()) {
                UserTask userTask = userTaskOptional.get();
                publicTaskForm.setDate(userTask.getDate().toString());
                publicTaskForm.setSubjectId(userTask.getSubject().getId());
                publicTaskForm.setTeacherId(userTask.getTeacher().getId());
                publicTaskForm.setSolution(userTask.getSolution());
            }
        }

        model.addAttribute("course", course);
        model.addAttribute("module", module);
        model.addAttribute("task", task);

        model.addAttribute("subjects", subjectService.findAllActive());
        model.addAttribute("teachers", userService.findAllActiveTeachers());

        initBreadcrumbs(course, module, model);
        getBreadcrumbs().put("/public/course/" + course.getId() + "/task/" + task.getId(), task.getTitle());
        showBreadcrumbs(model);

        return "public/task.html";
    }

    private void showBreadcrumbs(Course course, Module module, Model model) {

        initBreadcrumbs(course, module, model);
        showBreadcrumbs(model);
    }

    private void initBreadcrumbs(Course course, Module module, Model model) {
        Map<String, String> breadcrumbsReversed = new LinkedHashMap<>();

        if (null != module) {
            while (null != module.getParent()) {
                breadcrumbsReversed.put("/public/course/" + course.getId() + "/module/" + module.getId(), module.getTitle());
                module = module.getParent();
            }

            breadcrumbsReversed.put("/public/course/" + course.getId() + "/module/" + module.getId(), module.getTitle());
        }

        breadcrumbsReversed.put("/public/course/" + course.getId(), course.getTitle());

        ArrayList<String> keyList = new ArrayList<>(breadcrumbsReversed.keySet());

        getBreadcrumbs(true);
        for (int i = keyList.size() - 1; i >= 0; i--) {
            getBreadcrumbs().put(keyList.get(i), breadcrumbsReversed.get(keyList.get(i)));
        }
    }
}
