package de.digidoc.controller;

import de.digidoc.model.Role;
import de.digidoc.service.CourseService;
import de.digidoc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.stream.Collectors;

@Controller
public class HomeController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @GetMapping(value = {"/home", "/"})
    public String home(Model model) {
        model.addAttribute("currentUser", userService.findCurrentUser());

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isStudent = auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()).contains(Role.STUDENT.toString());
        if (isStudent) {
            model.addAttribute("courses", courseService.findAllActive());
            model.addAttribute("personalCourses", courseService.findAllPersonal());
        }

        return "home";
    }
}