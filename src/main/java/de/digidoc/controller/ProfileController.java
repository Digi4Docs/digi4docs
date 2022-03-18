package de.digidoc.controller;

import de.digidoc.form.ChangePasswordForm;
import de.digidoc.form.ProfileForm;
import de.digidoc.model.Role;
import de.digidoc.model.User;
import de.digidoc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class ProfileController {

    @Autowired
    private UserService userService;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;


    @GetMapping("/profile")
    public String profile(ProfileForm profileForm, Model model) {
        User user = loadUser();
        return showProfilePage(profileForm, model, user, true);
    }

    @PostMapping("/profile")
    public String profileUpdate(@Valid ProfileForm profileForm, BindingResult bindingResult, Model model) {
        User user = loadUser();
        if (bindingResult.hasErrors()) {
            return showProfilePage(profileForm, model, user, false);
        }

        user.setFirstname(profileForm.getFirstname());
        user.setLastname(profileForm.getLastname());
        user.setEmail(profileForm.getEmail());
        user.setClassNumber(profileForm.getClassNumber());

        try {
            userService.save(user, false);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return showProfilePage(profileForm, model, loadUser(), false);
    }

    @GetMapping("/profile/password")
    public String changePassword(ChangePasswordForm changePasswordForm) {
        return "profile/password";
    }

    @PostMapping("/profile/password")
    public String savePassword(@Valid ChangePasswordForm changePasswordForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return changePassword(changePasswordForm);
        }

        User user = loadUser();
        boolean passwordMatches = bCryptPasswordEncoder.matches(changePasswordForm.getOldPassword(), user.getPassword());
        if (!passwordMatches) {
            model.addAttribute("error", "Das bisherige Passwort ist nicht korrekt");
            return changePassword(changePasswordForm);
        }

        user.setPassword(changePasswordForm.getNewPassword());
        userService.save(user, true);

        return "redirect:/profile";
    }

    private String showProfilePage(ProfileForm profileForm, Model model, User user, boolean initFormData) {
        if (initFormData) {
            profileForm.setId(user.getId());
            profileForm.setFirstname(user.getFirstname());
            profileForm.setLastname(user.getLastname());
            profileForm.setEmail(user.getEmail());
            profileForm.setClassNumber(user.getClassNumber());
        }

        model.addAttribute("user", user);
        model.addAttribute("isStudent",
                user.getRoles().stream().filter(userRole -> userRole.getRole().equals(Role.STUDENT)).count() > 0);

        return "profile/profile";
    }

    private User loadUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        org.springframework.security.core.userdetails.User principal = (org.springframework.security.core.userdetails.User) auth.getPrincipal();
        return userService.findUserByEmail(principal.getUsername());
    }
}
