package de.digi4docs.controller;

import de.digi4docs.model.Module;
import de.digi4docs.model.*;
import de.digi4docs.service.ConfigService;
import de.digi4docs.service.CourseService;
import de.digi4docs.service.UserService;
import de.digi4docs.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class CertificateController extends AbstractController {
    @Autowired
    private ConfigService configService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/public/certificate/{courseId}")
    public String certificate(@PathVariable int courseId, Model model) {
        User currentUser = userService.findCurrentUser();
        return showPage(courseId, model, currentUser, true);
    }

    @PreAuthorize("hasAuthority('ADMIN') or  hasAuthority('USERS')")
    @GetMapping("/public/certificate/{courseId}/{userId}")
    public String certificate(@PathVariable int courseId, @PathVariable int userId, Model model) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return "redirect:/home";
        }

        return showPage(courseId, model, userOptional.get(), false);
    }

    private String showPage(int courseId, Model model, User user, boolean isCurrentUserPage) {
        Optional<Course> courseOptional = courseService.findById(courseId);

        if (courseOptional.isEmpty()) {
            return "redirect:/home";
        }

        Course course = courseOptional.get();
        model.addAttribute("course", course);
        model.addAttribute("user", user);
        model.addAttribute("certificateFooter", configService.getCertificateFooter());


        LinkedList<Module> courseModules = getCourseModules(course);

        List<Integer> taskIds = courseModules.stream()
                                             .map(Module::getTasks)
                                             .flatMap(Collection::stream)
                                             .map(Task::getId)
                                             .collect(Collectors.toList());

        Map<Integer, UserTask> userTaskMap = userTaskService.findByTasks(taskIds, user)
                                                            .stream()
                                                            .collect(Collectors.toMap(userTask -> userTask.getTask()
                                                                                                          .getId(),
                                                                    userTask -> userTask));
        model.addAttribute("userTaskMap", userTaskMap);


        if (isCurrentUserPage) {
            getBreadcrumbs(true).put("/public/course/" + course.getId(), course.getTitle());
        } else {
            getBreadcrumbs(true).put("/course/" + course.getId(), course.getTitle());
        }
        getBreadcrumbs().put("/public/certificate/" + course.getId(), "Zertifikat");
        showBreadcrumbs(model);

        return "certificate/certificate";
    }

    private LinkedList<Module> getCourseModules(Course course) {
        LinkedList<Module> courseModules = new LinkedList<>();

        addModules(course.getModules(), courseModules);

        return courseModules;
    }

    private void addModules(List<Module> modules, LinkedList<Module> courseModules) {
        for (Module module : modules) {
            courseModules.add(module);
            if (!module.getModules()
                       .isEmpty()) {
                addModules(module.getModules(), courseModules);
            }
        }
    }
}
