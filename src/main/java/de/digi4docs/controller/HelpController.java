package de.digi4docs.controller;

import de.digi4docs.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HelpController {
    @GetMapping("/help")
    public String help(Model model) {
        return "help";
    }
}
