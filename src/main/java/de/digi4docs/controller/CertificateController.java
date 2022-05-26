package de.digi4docs.controller;

import de.digi4docs.model.Module;
import de.digi4docs.model.*;
import de.digi4docs.service.ConfigService;
import de.digi4docs.service.CourseService;
import de.digi4docs.service.UserService;
import de.digi4docs.service.UserTaskService;
import de.digi4docs.util.RecursiveHandler;
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
        return showCertificatePage(courseId, model, currentUser, true);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USERS')")
    @GetMapping("/public/certificate/{courseId}/{userId}")
    public String certificate(@PathVariable int courseId, @PathVariable int userId, Model model) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return "redirect:/home";
        }

        return showCertificatePage(courseId, model, userOptional.get(), false);
    }

    @PreAuthorize("hasAuthority('STUDENT')")
    @GetMapping("/public/course-overview/{courseId}")
    public String courseOverview(@PathVariable int courseId, Model model) {
        User currentUser = userService.findCurrentUser();
        return showCourseOverviewPage(courseId, model, currentUser, true);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USERS')")
    @GetMapping("/public/course-overview/{courseId}/{userId}")
    public String courseOverview(@PathVariable int courseId, @PathVariable int userId, Model model) {
        Optional<User> userOptional = userService.findById(userId);
        if (userOptional.isEmpty()) {
            return "redirect:/home";
        }

        return showCourseOverviewPage(courseId, model, userOptional.get(), false);
    }

    private String showCertificatePage(int courseId, Model model, User user, boolean isCurrentUserPage) {
        Optional<Course> courseOptional = courseService.findById(courseId);

        if (courseOptional.isEmpty()) {
            return "redirect:/home";
        }

        Course course = courseOptional.get();
        model.addAttribute("course", course);
        model.addAttribute("user", user);
        model.addAttribute("certificateFooter", configService.getCertificateFooter());


        LinkedList<Module> courseModules = RecursiveHandler.getCourseModules(course);

        List<Integer> taskIds = RecursiveHandler.getCourseTaskIds(courseModules);

        Map<Integer, UserTask> userTaskMap = userTaskService.findByTasks(taskIds, user)
                                                            .stream()
                                                            .filter(userTask -> TaskStatus.DONE.equals(
                                                                    userTask.getStatus()))
                                                            .collect(Collectors.toMap(userTask -> userTask.getTask()
                                                                                                          .getId(),
                                                                    userTask -> userTask));
        model.addAttribute("userTaskMap", userTaskMap);


        List<Integer> emptyModules = new ArrayList<>();
        courseModules.forEach(courseModule -> {
            long numberOfDoneTasks = RecursiveHandler.getTasks(courseModule)
                                                     .stream()
                                                     .filter(task -> userTaskMap.containsKey(task.getId()))
                                                     .count();
            if (numberOfDoneTasks <= 0) {
                emptyModules.add(courseModule.getId());
            }
        });
        model.addAttribute("emptyModules", emptyModules);

        if (isCurrentUserPage) {
            getBreadcrumbs(true).put("/public/course/" + course.getId(), course.getTitle());
        } else {
            getBreadcrumbs(true).put("/course/" + course.getId(), course.getTitle());
        }
        getBreadcrumbs().put("/public/certificate/" + course.getId(), "Zertifikat");
        showBreadcrumbs(model);

        return "certificate/certificate";
    }

    private String showCourseOverviewPage(int courseId, Model model, User user, boolean isCurrentUserPage) {
        Optional<Course> courseOptional = courseService.findById(courseId);

        if (courseOptional.isEmpty()) {
            return "redirect:/home";
        }

        Course course = courseOptional.get();
        model.addAttribute("course", course);

        if (!isCurrentUserPage) {
            model.addAttribute("user", user);
        }


        LinkedList<Module> courseModules = RecursiveHandler.getCourseModules(course);

        List<Integer> taskIds = courseModules.stream()
                                             .map(Module::getTasks)
                                             .flatMap(Collection::stream)
                                             .map(Task::getId)
                                             .collect(Collectors.toList());

        List<UserTask> userTasks = userTaskService.findByTasks(taskIds, user);
        userTasks.stream()
                 .filter(userTask -> userTask.getStatus()
                                             .equals(TaskStatus.DONE));

        Map<Integer, List<UserTask>> doneTasks = userTasks
                .stream()
                .filter(userTask -> TaskStatus.DONE.equals(userTask.getStatus()))
                .collect(Collectors.groupingBy(userTask -> userTask.getTask()
                                                                   .getModule()
                                                                   .getId()));
        model.addAttribute("doneTasks", doneTasks);

        Map<Integer, List<UserTask>> transmittedTasks = userTasks
                .stream()
                .filter(userTask -> TaskStatus.TRANSMITTED.equals(userTask.getStatus()))
                .collect(Collectors.groupingBy(userTask -> userTask.getTask()
                                                                   .getModule()
                                                                   .getId()));
        model.addAttribute("transmittedTasks", transmittedTasks);


        if (isCurrentUserPage) {
            getBreadcrumbs(true).put("/public/course/" + course.getId(), course.getTitle());
        } else {
            getBreadcrumbs(true).put("/course/" + course.getId(), course.getTitle());
        }
        getBreadcrumbs().put("/public/course-overview/" + course.getId(), "Kurs-Übersicht");
        showBreadcrumbs(model);

        return "certificate/courseOverview";
    }
}
