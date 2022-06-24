package de.digi4docs.controller.settings;

import de.digi4docs.controller.AbstractController;
import de.digi4docs.form.CourseGroupForm;
import de.digi4docs.model.CourseGroup;
import de.digi4docs.service.CourseGroupService;
import de.digi4docs.service.CourseService;
import de.digi4docs.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.Optional;

@Controller
public class CourseGroupController extends AbstractController {
    @Autowired
    private CourseGroupService courseGroupService;

    @Autowired
    private CourseService courseService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/settings/groups")
    public String courseGroups(Model model) {
        model.addAttribute("courseGroups", courseGroupService.findAll());

        addBasicBreadcrumbs();
        showBreadcrumbs(model);

        return "settings/courseGroup/groups";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/settings/group")
    public String courseGroup(CourseGroupForm courseGroupForm, Model model) {
        addBasicBreadcrumbs();
        getBreadcrumbs().put("/settings/group", "Neue Gruppe");
        showBreadcrumbs(model);

        return "settings/courseGroup/new-group";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/settings/group")
    public String add(@Valid CourseGroupForm courseGroupForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return courseGroup(courseGroupForm, model);
        }

        CourseGroup courseGroup = mapFormToGroup(new CourseGroup(), courseGroupForm);

        try {
            courseGroupService.save(courseGroup);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return courseGroup(courseGroupForm, model);
        }

        return "redirect:/settings/groups";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/settings/group/{id}")
    public String courseGroup(@PathVariable int id, CourseGroupForm courseGroupForm, Model model) {
        return showDetailPage(id, courseGroupForm, model, true);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/settings/group/{id}")
    public String update(@PathVariable int id, @Valid CourseGroupForm courseGroupForm, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return showDetailPage(id, courseGroupForm, model, false);
        }

        Optional<CourseGroup> groupOptional = courseGroupService.findById(id);
        if (groupOptional.isEmpty()) {
            return "redirect:/settings/group";
        }

        CourseGroup courseGroup = mapFormToGroup(groupOptional.get(), courseGroupForm);

        try {
            courseGroupService.save(courseGroup);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return showDetailPage(id, courseGroupForm, model, false);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/settings/group/confirm-delete/{id}")
    public String confirmDelete(@PathVariable int id, Model model) {
        Optional<CourseGroup> groupOptional = courseGroupService.findById(id);
        if (groupOptional.isEmpty()) {
            return "redirect:/settings/group";
        }

        CourseGroup courseGroup = groupOptional.get();
        model.addAttribute("courseGroup", courseGroup);

        addBasicBreadcrumbs();
        getBreadcrumbs().put("/settings/group/" + courseGroup.getId(), courseGroup.getName());
        getBreadcrumbs().put("/settings/group/confirm-delete/" + courseGroup.getId(), "Gruppe löschen");
        showBreadcrumbs(model);

        return "settings/courseGroup/delete";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/settings/group/delete/{id}")
    public String delete(@PathVariable int id) {
        Optional<CourseGroup> groupOptional = courseGroupService.findById(id);
        if (groupOptional.isEmpty()) {
            return "redirect:/settings/group";
        }

        CourseGroup courseGroup = groupOptional.get();
        courseService.removeCourseGroup(courseGroup);
        userService.removeCourseGroup(courseGroup);
        courseGroupService.delete(courseGroup);

        return "redirect:/settings/groups";
    }

    private String showDetailPage(@PathVariable int id, CourseGroupForm courseGroupForm, Model model,
            boolean initFormData) {
        Optional<CourseGroup> groupOptional = courseGroupService.findById(id);
        if (groupOptional.isEmpty()) {
            return "redirect:/settings/group";
        }

        CourseGroup courseGroup = groupOptional.get();
        if (initFormData) {
            courseGroupForm.setId(courseGroup.getId());
            courseGroupForm.setName(courseGroup.getName());
            courseGroupForm.setIsActive(courseGroup.getIsActive());
        }

        model.addAttribute("courseGroup", courseGroup);

        addBasicBreadcrumbs();
        getBreadcrumbs().put("/settings/group/" + courseGroup.getId(), courseGroup.getName());
        showBreadcrumbs(model);

        return "settings/courseGroup/group";
    }

    private CourseGroup mapFormToGroup(CourseGroup courseGroup, CourseGroupForm courseGroupForm) {
        courseGroup.setName(courseGroupForm.getName());
        courseGroup.setIsActive(courseGroupForm.getIsActive());
        return courseGroup;
    }

    private void addBasicBreadcrumbs() {
        getBreadcrumbs(true).put("/settings/groups", "Einstellungen / Gruppen");
    }
}
