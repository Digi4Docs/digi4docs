package de.digidoc.controller;

import de.digidoc.form.NewPasswordForm;
import de.digidoc.form.PasswordForgottenForm;
import de.digidoc.model.PasswordForgotten;
import de.digidoc.model.User;
import de.digidoc.service.PasswordForgottenService;
import de.digidoc.service.UserService;
import de.digidoc.util.mail.MailProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Optional;

@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityController {

    @Autowired
    private MailProvider mailProvider;

    @Autowired
    private PasswordForgottenService passwordForgottenService;

    @Autowired
    private UserService userService;

    @GetMapping("/login")
    public String login() {
        return "security/login";
    }

    @GetMapping("/reset-password")
    public String resetPassword(PasswordForgottenForm form) {
        return "security/reset-password";
    }


    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@Valid PasswordForgottenForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return resetPassword(form);
        }

        User user = userService.findActiveUserByEmail(form.getEmail()).get();
        Optional<PasswordForgotten> optionalPasswordForgotten = passwordForgottenService.findByUserId(user.getId());
        PasswordForgotten passwordForgotten = optionalPasswordForgotten.orElseGet(PasswordForgotten::new);
        passwordForgotten.setUser(user);
        passwordForgottenService.save(passwordForgotten);

        mailProvider.sendPasswordForgottenMail(passwordForgotten);
        
        model.addAttribute("success", true);
        return "security/reset-password";
    }

    @GetMapping("/new-password/{hash}")
    public String newPassword(@PathVariable String hash, NewPasswordForm form, Model model) {
        validateHash(hash, model);
        model.addAttribute("hash", hash);

        return "security/new-password";
    }

    @PostMapping("/new-password/{hash}")
    public String saveNewPassword(@PathVariable String hash, @Valid NewPasswordForm form, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return newPassword(hash, form, model);
        }

        PasswordForgotten passwordForgotten = validateHash(hash, model);
        passwordForgotten.getUser().setPassword(form.getNewPassword());
        userService.save(passwordForgotten.getUser(), true);

        passwordForgottenService.delete(passwordForgotten);
        model.addAttribute("hash", hash);
        model.addAttribute("success", true);

        return "security/new-password";
    }

    private PasswordForgotten validateHash(String hash, Model model) {
        Optional<PasswordForgotten> optionalHash = passwordForgottenService.findByHash(hash);
        if (optionalHash.isEmpty()) {
            model.addAttribute("error", "Der aufgerufene Code ist ungültig.");
            return null;
        }

        PasswordForgotten passwordForgotten = optionalHash.get();
        if (LocalDateTime.now().isAfter(passwordForgotten.getExpirationAt())) {
            model.addAttribute("error", "Der aufgerufene Code ist abgelaufen.");
            return null;
        }

        return passwordForgotten;
    }
}
