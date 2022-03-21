package de.digidoc.controller;

import de.digidoc.model.Course;
import de.digidoc.model.Role;
import de.digidoc.service.CourseService;
import de.digidoc.service.UserService;
import de.digidoc.util.ProgressCountProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @Autowired
    private ProgressCountProvider progressCountProvider;

    @GetMapping(value = {"/home", "/"})
    public String home(Model model) {
        model.addAttribute("currentUser", userService.findCurrentUser());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isStudent = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()).contains(Role.STUDENT.toString());
        if (isStudent) {
            model.addAttribute("courses", courseService.findAllActive());
            Map<Course, Integer> coursePersonTaskCount = progressCountProvider.getPersonalCourseTaskCountMap();
            model.addAttribute("personalCourses", coursePersonTaskCount);
            model.addAttribute("courseTaskCounts", progressCountProvider.getGeneralCourseTaskCountMap(coursePersonTaskCount.keySet()));
        }

        return "home";
    }
}