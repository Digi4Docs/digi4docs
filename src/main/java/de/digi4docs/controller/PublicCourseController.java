package de.digi4docs.controller;

import de.digi4docs.form.PublicTaskForm;
import de.digi4docs.model.Module;
import de.digi4docs.model.*;
import de.digi4docs.service.*;
import de.digi4docs.util.ProgressCountProvider;
import de.digi4docs.util.RecursiveHandler;
import de.digi4docs.util.UploadUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import java.util.*;
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

        User currentUser = userService.findCurrentUser();
        boolean isCourseEditor =
                userService.hasUserRole(currentUser, Role.ADMIN) || userService.hasUserRole(currentUser, Role.COURSES);

        if (!courseOptional.isPresent() || (!isCourseEditor && !courseOptional.get()
                                                                              .getIsActive())) {
            return "redirect:/home";
        }

        Course course = courseOptional.get();
        model.addAttribute("course", course);
        Map<Integer, Integer> personalModuleTaskCountMap =
                progressCountProvider.getPersonalModuleTaskCountMap(course.getModules());
        model.addAttribute("personalModuleTaskTotal", personalModuleTaskCountMap.values()
                                                                                .stream()
                                                                                .reduce(0, Integer::sum));
        model.addAttribute("personalModuleTaskCounts", personalModuleTaskCountMap);

        Map<Integer, Integer> generalModuleTaskCountMap =
                progressCountProvider.getGeneralModuleTaskCountMap(course.getModules());
        model.addAttribute("generalModuleTaskTotal", generalModuleTaskCountMap.values()
                                                                              .stream()
                                                                              .reduce(0, Integer::sum));
        model.addAttribute("generalModuleTaskCounts", generalModuleTaskCountMap);

        showBreadcrumbs(course, null, model);

        return "public/course";
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/public/course/{id}/module/{moduleId}")
    public String module(@PathVariable int id, @PathVariable int moduleId, Model model) {
        Optional<Course> courseOptional = courseService.findById(id);

        User currentUser = userService.findCurrentUser();
        boolean isModuleEditor =
                userService.hasUserRole(currentUser, Role.ADMIN) || userService.hasUserRole(currentUser, Role.COURSES);

        if (!courseOptional.isPresent() || (!isModuleEditor && !courseOptional.get()
                                                                              .getIsActive())) {
            return "redirect:/home";
        }

        Course course = courseOptional.get();

        Optional<Module> moduleOptional = moduleService.findById(moduleId);

        if (!moduleOptional.isPresent() || (!isModuleEditor && !moduleOptional.get()
                                                                              .getIsActive())) {
            if (null != moduleOptional.get()
                                      .getParent()) {
                return "redirect:/public/course/" + course.getId() + "/module/" + moduleOptional.get()
                                                                                                .getParent()
                                                                                                .getId();
            }
            return "redirect:/public/course/" + course.getId();
        }

        Module module = moduleOptional.get();

        model.addAttribute("course", course);
        model.addAttribute("module", module);
        model.addAttribute("moduleFinished", isModuleFinished(module));

        Module nextModule = null != module.getParent() ? moduleService.findNextModule(module.getParent()
                                                                                            .getId(),
                module.getOrderPosition())
                : moduleService.findNextModule(course, module.getOrderPosition());
        model.addAttribute("nextModule", nextModule);
        Module previousModule = null != module.getParent() ? moduleService.findPreviousModule(module.getParent()
                                                                                                    .getId(),
                module.getOrderPosition())
                : moduleService.findPreviousModule(course, module.getOrderPosition());
        model.addAttribute("previousModule", previousModule);

        List<Module> modulesToCount = new ArrayList<>();
        modulesToCount.add(module);
        if (null != module.getModules()) {
            modulesToCount.addAll(module.getModules());
        }

        Map<Integer, Integer> personalModuleTaskCountMap =
                progressCountProvider.getPersonalModuleTaskCountMap(modulesToCount);
        model.addAttribute("personalModuleTaskTotal", personalModuleTaskCountMap.values()
                                                                                .stream()
                                                                                .reduce(0, Integer::sum));
        model.addAttribute("personalModuleTaskCounts", personalModuleTaskCountMap);


        Map<Integer, Integer> generalModuleTaskCountMap =
                progressCountProvider.getGeneralModuleTaskCountMap(modulesToCount);
        model.addAttribute("generalModuleTaskTotal", generalModuleTaskCountMap.values()
                                                                              .stream()
                                                                              .reduce(0, Integer::sum));
        model.addAttribute("generalModuleTaskCounts", generalModuleTaskCountMap);

        if (!module.getTasks()
                   .isEmpty()) {
            Map<Integer, UserTask> userTasks = userTaskService.findByTasks(module.getTasks()
                                                                                 .stream()
                                                                                 .map(Task::getId)
                                                                                 .collect(Collectors.toList()))
                                                              .stream()
                                                              .collect(Collectors.toMap(userTask -> userTask.getTask()
                                                                                                            .getId(),
                                                                      userTask -> userTask));
            model.addAttribute("userTasks", userTasks);
            model.addAttribute("userTasksDone", userTasks.values()
                                                         .stream()
                                                         .filter(userTask -> TaskStatus.DONE.equals(
                                                                 userTask.getStatus()))
                                                         .count());
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
    @PostMapping(value = "/public/course/{id}/task/{taskId}", params = "transmit",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String transmitTask(@PathVariable int id, @PathVariable int taskId,
            @Valid PublicTaskForm publicTaskForm, BindingResult bindingResult, Model model) {
        return saveTask(id, taskId, publicTaskForm, bindingResult, model, true);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping(value = "/public/course/{id}/task/{taskId}", params = "save",
            consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public String saveTask(@PathVariable int id, @PathVariable int taskId, @Valid PublicTaskForm publicTaskForm,
            BindingResult bindingResult,
            Model model) {
        return saveTask(id, taskId, publicTaskForm, bindingResult, model, false);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @PostMapping(value = "/public/course/{id}/task/{taskId}", params = "delete")
    public String deleteConfirmTask(@PathVariable int id, @PathVariable int taskId,
            @Valid PublicTaskForm publicTaskForm, Model model) {
        model.addAttribute("savedStatus", true);

        Optional<UserTask> userTaskOptional = userTaskService.findByTask(taskId);
        if (userTaskOptional.isEmpty()) {
            model.addAttribute("error", "Löschen nicht möglich");
            return showTaskPage(id, taskId, model, publicTaskForm, false);
        }

        model.addAttribute("userTask", userTaskOptional.get());
        model.addAttribute("courseId", id);

        return "public/delete";
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping(value = "/public/course/{id}/task/{taskId}/delete")
    public String delete(@PathVariable int id, @PathVariable int taskId) {
        Optional<UserTask> userTaskOptional = userTaskService.findByTask(taskId);
        if (userTaskOptional.isEmpty()) {
            return "redirect:/home";
        }

        userTaskService.delete(userTaskOptional.get());


        return "redirect:/public/course/" + id;
    }

    private String saveTask(@PathVariable int id, @PathVariable int taskId, @Valid PublicTaskForm publicTaskForm,
            BindingResult bindingResult, Model model, boolean doTransmit) {

        model.addAttribute("savedStatus", true);

        Optional<Course> courseOptional = courseService.findById(id);
        if (!courseOptional.isPresent() || !courseOptional.get()
                                                          .getIsActive()) {
            return "redirect:/home";
        }

        Optional<Task> taskOptional = taskService.findById(taskId);
        if (!taskOptional.isPresent() || !taskOptional.get()
                                                      .getIsActive()) {
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

        // setup file upload
        if (null == userTask.getStatus() || TaskStatus.OPEN.equals(userTask.getStatus()) || TaskStatus.REJECTED.equals(
                userTask.getStatus())) {
            if (null != publicTaskForm.getFile() && 0 != publicTaskForm.getFile()
                                                                       .getSize()) {
                if (null == userTask.getFile()) {
                    userTask.setFile(new File());
                }

                boolean successfulFileInit = UploadUtils.addUploadData(userTask.getFile(), publicTaskForm.getFile());
                if (!successfulFileInit) {
                    model.addAttribute("error",
                            "Deine Datei kann nicht hochgeladen werden. Beachte, dass die Datei maximal 25MB groß " +
                                    "sein" +
                                    " darf.");
                    return showTaskPage(id, taskId, model, publicTaskForm, false);
                }
            }
        }

        if (!bindingResult.hasFieldErrors("date")) {
            userTask.setDate(LocalDate.parse(publicTaskForm.getDate()));
        }

        if (!bindingResult.hasFieldErrors("teacherId")) {
            userTask.setTeacher(userService.findById(publicTaskForm.getTeacherId())
                                           .get());
        }
        if (!bindingResult.hasFieldErrors("subjectId")) {
            userTask.setSubject(subjectService.findById(publicTaskForm.getSubjectId())
                                              .get());
        }
        if (!bindingResult.hasFieldErrors("solution")) {
            userTask.setSolution(publicTaskForm.getSolution());
        }

        if (bindingResult.hasErrors()) {
            persistTask(userTask, model);
            return task(id, taskId, publicTaskForm, model);
        }

        if (doTransmit) {
            userTask.setStatus(TaskStatus.TRANSMITTED);
            userTask.setTransmittedAt(LocalDateTime.now());
        }

        boolean successful = persistTask(userTask, model);

        if (doTransmit && successful) {
            return "redirect:/public/course/" + courseOptional.get()
                                                              .getId() + "/task/" + task.getId();
        }

        return showTaskPage(id, taskId, model, publicTaskForm, false);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/public/course/{id}/task/{taskId}/file/delete")
    public String deleteFile(@PathVariable int id, @PathVariable int taskId) {
        Optional<UserTask> userTaskOptional = userTaskService.findByTask(taskId);
        if (userTaskOptional.isPresent()) {
            UserTask userTask = userTaskOptional.get();
            userTask.setFile(null);
            userTaskService.save(userTask);
        }
        return "redirect:/public/course/" + id + "/task/" + taskId;
    }

    private boolean persistTask(UserTask userTask, Model model) {
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

    private String showTaskPage(int courseId, int taskId, Model model, PublicTaskForm publicTaskForm,
            boolean initFormData) {
        User currentUser = userService.findCurrentUser();
        boolean isTaskEditor =
                userService.hasUserRole(currentUser, Role.ADMIN) || userService.hasUserRole(currentUser, Role.COURSES);

        Optional<Course> courseOptional = courseService.findById(courseId);
        if (courseOptional.isEmpty() || (!isTaskEditor && !courseOptional.get()
                                                                         .getIsActive())) {
            return "redirect:/home";
        }

        Optional<Task> taskOptional = taskService.findById(taskId);
        if (!taskOptional.isPresent() || (!isTaskEditor && !taskOptional.get()
                                                                        .getIsActive())) {
            return "redirect:/home";
        }

        Course course = courseOptional.get();
        Task task = taskOptional.get();
        Module module = task.getModule();

        if (!module.getIsActive() && !isTaskEditor) {
            return "redirect:/home";
        }

        if (!RecursiveHandler.getCourse(module)
                             .getId()
                             .equals(courseId)) {
            return "redirect:/home";
        }

        Optional<UserTask> userTaskOptional = userTaskService.findByTask(taskId);
        model.addAttribute("taskStatus", userTaskOptional.isPresent() ? userTaskOptional.get()
                                                                                        .getStatus() : TaskStatus.OPEN);
        model.addAttribute("userTask", userTaskOptional.orElse(null));

        if (initFormData) {
            if (userTaskOptional.isPresent()) {
                UserTask userTask = userTaskOptional.get();
                if (null != userTask.getDate()) {
                    publicTaskForm.setDate(userTask.getDate()
                                                   .toString());
                }
                if (null != userTask.getSubject()) {
                    publicTaskForm.setSubjectId(userTask.getSubject()
                                                        .getId());
                }
                if (null != userTask.getTeacher()) {
                    publicTaskForm.setTeacherId(userTask.getTeacher()
                                                        .getId());
                }
                publicTaskForm.setSolution(userTask.getSolution());
            } else {
                publicTaskForm.setDate(LocalDate.now()
                                                .toString());
            }
        }

        model.addAttribute("course", course);
        model.addAttribute("module", module);
        model.addAttribute("task", task);
        model.addAttribute("nextTask", taskService.findNextTask(module.getId(), task.getOrderPosition()));
        model.addAttribute("previousTask", taskService.findPreviousTask(module.getId(), task.getOrderPosition()));

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
                breadcrumbsReversed.put("/public/course/" + course.getId() + "/module/" + module.getId(),
                        module.getTitle());
                module = module.getParent();
            }

            breadcrumbsReversed.put("/public/course/" + course.getId() + "/module/" + module.getId(),
                    module.getTitle());
        }

        breadcrumbsReversed.put("/public/course/" + course.getId(), course.getTitle());

        ArrayList<String> keyList = new ArrayList<>(breadcrumbsReversed.keySet());

        getBreadcrumbs(true);
        for (int i = keyList.size() - 1; i >= 0; i--) {
            getBreadcrumbs().put(keyList.get(i), breadcrumbsReversed.get(keyList.get(i)));
        }
    }

    protected boolean isModuleFinished(Module module) {
        User currentUser = userService.findCurrentUser();
        LinkedList<Module> modules = RecursiveHandler.getModules(module);
        List<Integer> taskIds = RecursiveHandler.getModulesTaskIds(modules);
        if (0 == taskIds.size()) {
            return false;
        }

        int userTaskCount =
                Math.toIntExact(userTaskService.findByTasks(taskIds, currentUser)
                                               .stream()
                                               .filter(userTask -> TaskStatus.DONE.equals(
                                                       userTask.getStatus()))
                                               .count());
        return userTaskCount == taskIds.size();
    }
}
