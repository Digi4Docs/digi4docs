package de.digidoc.controller;

import de.digidoc.model.Role;
import de.digidoc.model.User;
import de.digidoc.model.UserRole;
import de.digidoc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @GetMapping("/profile")
    public String profile(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        User user = userService.findUserByEmail(principal.getUsername());
        model.addAttribute("user", user);

        List<String> userRoles = CollectionUtils.isEmpty(user.getRoles()) ? Collections.emptyList() :
                user.getRoles().stream().map(UserRole::getRole).map(Role::getDisplayValue).collect(Collectors.toList());
        model.addAttribute("userRoles", userRoles);

        return "profile";
    }

    @PostMapping("/profile")
    public String profileUpdate(@ModelAttribute User form, Model model) {
        model.addAttribute("user", form);

        return "profile";
    }
}
