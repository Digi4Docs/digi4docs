package de.digidoc.controller;

import de.digidoc.form.PasswordResetForm;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@Order(Ordered.HIGHEST_PRECEDENCE)
public class SecurityController {

    @GetMapping(value = {"/", "/login"})
    public String login() {
        return "security/login";
    }

    @GetMapping("/reset-password")
    public String resetPassword() {
        return "security/reset-password";
    }


    @PostMapping("/reset-password")
    public String resetPasswordSubmit(@ModelAttribute PasswordResetForm form, Model model) {
        // todo: send email
        model.addAttribute("success", true);
        return "security/reset-password";
    }
}
