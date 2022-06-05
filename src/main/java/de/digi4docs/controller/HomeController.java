package de.digi4docs.controller;

import de.digi4docs.dto.TaskReviewRow;
import de.digi4docs.model.Module;
import de.digi4docs.model.*;
import de.digi4docs.service.CourseService;
import de.digi4docs.service.ModuleService;
import de.digi4docs.service.UserService;
import de.digi4docs.service.UserTaskService;
import de.digi4docs.util.ProgressCountProvider;
import de.digi4docs.util.RecursiveHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private ProgressCountProvider progressCountProvider;

    @GetMapping(value = {"/home", "/"})
    public String home(Model model) {
        User currentUser = userService.findCurrentUser();
        model.addAttribute("currentUser", currentUser);

        boolean isStudent = userService.hasUserRole(currentUser, Role.STUDENT);
        if (isStudent) {
            model.addAttribute("courses", courseService.findAllActive());

            Map<Course, Integer> coursePersonTaskCount = progressCountProvider.getPersonalCourseTaskCountMap();
            List<Course> finishedCourses = new ArrayList<>();
            coursePersonTaskCount.keySet()
                                 .forEach(course -> {
                                     LinkedList<Module> courseModules = RecursiveHandler.getCourseModules(course);
                                     List<Integer> taskIds = RecursiveHandler.getModulesTaskIds(courseModules);
                                     int userTaskCount =
                                             Math.toIntExact(userTaskService.findByTasks(taskIds, currentUser)
                                                                            .stream()
                                                                            .filter(userTask -> TaskStatus.DONE.equals(
                                                                                    userTask.getStatus()))
                                                                            .count());
                                     if (userTaskCount == taskIds.size()) {
                                         finishedCourses.add(course);
                                     }
                                 });
            finishedCourses.forEach(coursePersonTaskCount::remove);

            model.addAttribute("personalCourses", coursePersonTaskCount);
            model.addAttribute("courseTaskCounts",
                    progressCountProvider.getGeneralCourseTaskCountMap(coursePersonTaskCount.keySet()));

            List<UserTask> rejectedOfCurrentUser = userTaskService.findRejectedOfCurrentUser();
            model.addAttribute("rejectedTasks", rejectedOfCurrentUser);
            model.addAttribute("rejectTaskCourses", RecursiveHandler.getUserTaskCourseMap(rejectedOfCurrentUser));

            model.addAttribute("finishedCourses", finishedCourses);

            // adding special badge modules
            List<Module> finishedBadgeModules = new ArrayList<>();
            moduleService.findAllBadgeModules()
                         .forEach(badgeModule -> {
                             LinkedList<Module> modules = RecursiveHandler.getModuleModules(badgeModule);
                             List<Integer> taskIds = RecursiveHandler.getModulesTaskIds(modules);
                             if (taskIds.size() > 0) {
                                 int userTaskCount =
                                         Math.toIntExact(userTaskService.findByTasks(taskIds, currentUser)
                                                                        .stream()
                                                                        .filter(userTask -> TaskStatus.DONE.equals(
                                                                                userTask.getStatus()))
                                                                        .count());
                                 if (userTaskCount == taskIds.size()) {
                                     finishedBadgeModules.add(badgeModule);
                                 }
                             }
                         });
            model.addAttribute("finishedBadgeModules", finishedBadgeModules);
        }

        boolean showTeacherTasks =
                userService.hasUserRole(currentUser, Role.ADMIN) || userService.hasUserRole(currentUser, Role.TEACHER);
        model.addAttribute("showTeacherTasks", showTeacherTasks);
        if (showTeacherTasks) {
            List<TaskReviewRow> teacherTasks = userTaskService.findTransmittedOfCurrentUser();
            model.addAttribute("teacherTasks", teacherTasks);
        }

        return "home";
    }
}