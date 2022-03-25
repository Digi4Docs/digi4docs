package de.digidoc.controller;

import de.digidoc.model.Course;
import de.digidoc.model.Role;
import de.digidoc.model.User;
import de.digidoc.model.UserTask;
import de.digidoc.service.CourseService;
import de.digidoc.service.UserService;
import de.digidoc.service.UserTaskService;
import de.digidoc.util.ProgressCountProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {
    @Autowired
    private CourseService courseService;

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
            model.addAttribute("personalCourses", coursePersonTaskCount);
            model.addAttribute("courseTaskCounts", progressCountProvider.getGeneralCourseTaskCountMap(coursePersonTaskCount.keySet()));
        }

        boolean showTeacherTasks = userService.hasUserRole(currentUser, Role.ADMIN) || userService.hasUserRole(currentUser, Role.TEACHER);
        model.addAttribute("showTeacherTasks", showTeacherTasks);
        if (showTeacherTasks) {
            List<UserTask> teacherTasks = userTaskService.findTransmittedOfCurrentUser();
            model.addAttribute("teacherTasks", teacherTasks);
        }

        return "home";
    }
}