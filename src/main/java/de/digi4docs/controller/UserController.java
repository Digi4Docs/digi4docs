package de.digi4docs.controller;

import de.digi4docs.form.ImportForm;
import de.digi4docs.form.UserEditForm;
import de.digi4docs.form.UserForm;
import de.digi4docs.form.UserNewForm;
import de.digi4docs.model.*;
import de.digi4docs.service.CourseGroupService;
import de.digi4docs.service.UserService;
import de.digi4docs.service.UserTaskService;
import de.digi4docs.util.Importer;
import de.digi4docs.util.RecursiveHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class UserController extends AbstractController {
    @Autowired
    private Importer importer;

    @Autowired
    private CourseGroupService courseGroupService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTaskService userTaskService;

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USERS')")
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());

        addBasicBreadcrumbs();
        showBreadcrumbs(model);

        return "user/users";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/import")
    public String importUser(ImportForm importForm, Model model) {
        addBasicBreadcrumbs();
        showBreadcrumbs(model);

        return "user/import";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/users/import")
    public String importUser(@RequestParam("file") MultipartFile file, @Valid ImportForm importForm,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return importUser(importForm, model);
        }

        String validationMessage = importer.validateFile(file);
        if (null != validationMessage) {
            model.addAttribute("error", validationMessage);
            return importUser(importForm, model);
        }

        try {
            List<List<String>> dataToImport = importer.readCsv(file);
            Map<Integer, String> importErrors =
                    importer.importData(dataToImport, importForm.getIsActive(), importForm.getRoles());
            model.addAttribute("importErrors", importErrors);
            model.addAttribute("importSuccess", dataToImport.size() - importErrors.size());
        } catch (IOException e) {
            model.addAttribute("error", "Datei kann nicht eingelesen werden. Fehlermeldung: " + e.getMessage());
            return importUser(importForm, model);
        }

        addBasicBreadcrumbs();
        showBreadcrumbs(model);

        return "user/import";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/users/import-template")
    public ResponseEntity<InputStreamResource> importTemplate() {
        return importer.createTemplateFile();
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USERS')")
    @GetMapping("/user/{id}")
    public String user(@PathVariable int id, UserEditForm userEditForm, Model model) {
        return showUserPage(id, userEditForm, model, true);
    }

    private String showUserPage(@PathVariable int id, UserEditForm userEditForm, Model model, boolean initFormData) {
        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isEmpty()) {
            return "redirect:/user";
        }

        model.addAttribute("courseGroups", courseGroupService.findAllActive());

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
            user.getRoles()
                .forEach(userRole -> {
                    userEditForm.getRoles()
                                .add(userRole.getRole());
                });

            userEditForm.setCourseGroups(new ArrayList<>());
            user.getCourseGroups()
                .forEach(courseGroup -> {
                    userEditForm.getCourseGroups()
                                .add(courseGroup.getId());
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

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USERS')")
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

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USERS')")
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

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USERS')")
    @GetMapping("/user")
    public String user(UserNewForm userNewForm, Model model) {
        addBasicBreadcrumbs();
        getBreadcrumbs().put("/user", "Neuer Benutzer");
        showBreadcrumbs(model);

        return "user/new-user";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USERS')")
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

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('USERS')")
    @PostMapping("/user/{id}")
    public String updateUser(@PathVariable int id, @Valid UserEditForm userEditForm, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return showUserPage(id, userEditForm, model, false);
        }

        Optional<User> userOptional = userService.findById(id);
        if (userOptional.isEmpty()) {
            return "redirect:/user";
        }

        User user = mapFormToUser(userOptional.get(), userEditForm);
        boolean changePassword = (null != userEditForm.getPassword() && !userEditForm.getPassword()
                                                                                     .isEmpty());
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

        user.getRoles()
            .clear();
        userForm.getRoles()
                .forEach(role -> {
                    user.getRoles()
                        .add(new UserRole(null, user, role));
                });


        if (null == user.getCourseGroups()) {
            user.setCourseGroups(new HashSet<>());
        }

        user.getCourseGroups()
            .clear();
        if (!CollectionUtils.isEmpty(userForm.getCourseGroups())) {
            Map<Integer, CourseGroup> groups = courseGroupService.findAllActive()
                                                                 .stream()
                                                                 .collect(Collectors.toMap(CourseGroup::getId,
                                                                         courseGroup -> courseGroup));
            userForm.getCourseGroups()
                    .forEach(groupId -> {
                        if (groups.containsKey(groupId)) {
                            user.getCourseGroups()
                                .add(groups.get(groupId));
                        }
                    });
        }

        return user;
    }

    private void addBasicBreadcrumbs() {
        getBreadcrumbs(true).put("/users", "Benutzer");
    }
}
