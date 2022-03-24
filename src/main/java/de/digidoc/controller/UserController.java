package de.digidoc.controller;

import de.digidoc.form.UserEditForm;
import de.digidoc.form.UserForm;
import de.digidoc.form.UserNewForm;
import de.digidoc.model.Role;
import de.digidoc.model.User;
import de.digidoc.model.UserRole;
import de.digidoc.model.UserTask;
import de.digidoc.service.UserService;
import de.digidoc.service.UserTaskService;
import de.digidoc.util.RecursiveHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

@Controller
public class UserController extends AbstractController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserTaskService userTaskService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());

        addBasicBreadcrumbs();
        showBreadcrumbs(model);

        return "user/users";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user/{id}")
    public String user(@PathVariable int id, UserEditForm userEditForm, Model model) {
        return showUserPage(id, userEditForm, model, true);
    }

    private String showUserPage(@PathVariable int id, UserEditForm userEditForm, Model model, boolean initFormData) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isEmpty()) {
            return "redirect:/user";
        }

        User user = userOptional.get();
        if (initFormData) {
            userEditForm.setId(user.getId());
            userEditForm.setFirstname(user.getFirstname());
            userEditForm.setLastname(user.getLastname());
            userEditForm.setEmail(user.getEmail());
            userEditForm.setClassYear(user.getClassYear());
            userEditForm.setClassIdentifier(user.getClassIdentifier());
            userEditForm.setClassNumber(user.getClassNumber());
            userEditForm.setIsActive(user.getIsActive());
            userEditForm.setRoles(new ArrayList<>());

            user.getRoles().forEach(userRole -> {
                userEditForm.getRoles().add(userRole.getRole());
            });
        }

        model.addAttribute("user", user);

        boolean isStudent = userService.hasUserRole(user, Role.STUDENT);
        model.addAttribute("isStudent", isStudent);
        if (isStudent) {
            List<UserTask> userTasks = userTaskService.findByUser(user.getId());
            model.addAttribute("courses", RecursiveHandler.getCourses(userTasks));
        }

        addBasicBreadcrumbs();
        getBreadcrumbs().put("/user/" + user.getId(), user.getLastname() + ", " + user.getFirstname());
        showBreadcrumbs(model);

        return "user/user";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user/confirm-delete/{id}")
    public String confirmDeleteUser(@PathVariable int id, Model model) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isEmpty()) {
            return "redirect:/users";
        }

        User user = userOptional.get();
        model.addAttribute("user", user);


        addBasicBreadcrumbs();
        getBreadcrumbs().put("/user/" + user.getId(), user.getLastname() + ", " + user.getFirstname());
        getBreadcrumbs().put("/user/confirm-delete/" + user.getId(), "Benutzer löschen");
        showBreadcrumbs(model);

        return "user/delete";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user/delete/{id}")
    public String deleteUser(@PathVariable int id) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isEmpty()) {
            return "redirect:/users";
        }

        User user = userOptional.get();
        userService.delete(user);

        return "redirect:/users";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/user")
    public String user(UserNewForm userNewForm, Model model) {
        addBasicBreadcrumbs();
        getBreadcrumbs().put("/user", "Neuer Benutzer");
        showBreadcrumbs(model);

        return "user/new-user";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/user")
    public String addUser(@Valid UserNewForm userNewForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return user(userNewForm, model);
        }

        User user = mapFormToUser(new User(), userNewForm);
        user.setPassword(userNewForm.getPassword());
        user.setEmail(userNewForm.getEmail());

        try {
            userService.add(user);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return user(userNewForm, model);
        }

        return "redirect:/users";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/user/{id}")
    public String updateUser(@PathVariable int id, @Valid UserEditForm userEditForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return showUserPage(id, userEditForm, model, false);
        }

        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isEmpty()) {
            return "redirect:/user";
        }

        User user = mapFormToUser(userOptional.get(), userEditForm);
        boolean changePassword = (null != userEditForm.getPassword() && !userEditForm.getPassword().isEmpty());
        if (changePassword) {
            user.setPassword(userEditForm.getPassword());
        }
        user.setEmail(userEditForm.getEmail());

        try {
            userService.save(user, changePassword);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return showUserPage(id, userEditForm, model, false);
    }

    private User mapFormToUser(User user, UserForm userForm) {
        user.setFirstname(userForm.getFirstname());
        user.setLastname(userForm.getLastname());
        user.setClassIdentifier(userForm.getClassIdentifier());
        user.setClassNumber(userForm.getClassNumber());
        user.setClassYear(userForm.getClassYear());
        user.setIsActive(userForm.getIsActive());

        if (null == user.getRoles()) {
            user.setRoles(new HashSet<>());
        }

        user.getRoles().clear();
        userForm.getRoles().forEach(role -> {
            user.getRoles().add(new UserRole(null, user, role));
        });

        return user;
    }

    private void addBasicBreadcrumbs() {
        getBreadcrumbs(true).put("/users", "Benutzer");
    }
}
