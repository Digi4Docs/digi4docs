package de.digi4docs.controller;

import de.digi4docs.form.ModuleForm;
import de.digi4docs.model.Course;
import de.digi4docs.model.Module;
import de.digi4docs.service.CourseService;
import de.digi4docs.util.RecursiveHandler;
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
public class CourseModuleController extends AbstractModuleController {
    @Autowired
    private CourseService courseService;

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @GetMapping("/course/{id}/modules")
    public String modules(@PathVariable int id, Model model) {
        Course course = courseService.findById(id)
                                     .get();
        model.addAttribute("course", course);
        model.addAttribute("linkCourseId", course.getId());
        model.addAttribute("modules", moduleService.findAllByCourse(id));

        initBreadcrumbCourseEntries(course).showBreadcrumbs(model);

        return "module/modules";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @GetMapping("/course/{id}/module")
    public String module(@PathVariable int id, ModuleForm moduleForm, Model model) {
        Course course = courseService.findById(id)
                                     .get();

        model.addAttribute("course", course);
        model.addAttribute("linkCourseId", course.getId());

        moduleForm.setColor(course.getColor());
        moduleForm.setBadgeText(course.getBadgeText());

        initBreadcrumbCourseEntries(course);
        getBreadcrumbs().put("/course/" + course.getId() + "/module", "Neues Modul");
        showBreadcrumbs(model);

        return "module/new-module";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @PostMapping("/course/{id}/module")
    public String add(@PathVariable int id, @Valid ModuleForm moduleForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return module(id, moduleForm, model);
        }

        Course course = courseService.findById(id)
                                     .get();

        Module module = mapFormToModule(new Module(), moduleForm);
        module.setCourse(course);

        try {
            moduleService.save(module);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return module(id, moduleForm, model);
        }

        return "redirect:/course/" + course.getId() + "/modules";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @GetMapping("/course/{courseId}/module/{id}")
    public String course(@PathVariable int courseId, @PathVariable int id, ModuleForm moduleForm, Model model) {
        return showDetailPage(courseId, id, moduleForm, model, true);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @PostMapping("/course/{courseId}/module/{id}")
    public String update(@PathVariable int courseId, @PathVariable int id, @Valid ModuleForm moduleForm,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return showDetailPage(courseId, id, moduleForm, model, false);
        }

        Optional<Module> moduleOptional = moduleService.findById(id);
        if (moduleOptional.isEmpty()) {
            return "redirect:/course/" + courseId + "/modules";
        }

        Module module = mapFormToModule(moduleOptional.get(), moduleForm);

        try {
            moduleService.save(module);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return showDetailPage(courseId, id, moduleForm, model, false);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @GetMapping("/course/{courseId}/module/confirm-delete/{id}")
    public String confirmDelete(@PathVariable int courseId, @PathVariable int id, Model model) {
        Optional<Module> moduleOptional = moduleService.findById(id);
        if (moduleOptional.isEmpty()) {
            return "redirect:/course/" + courseId + "/modules";
        }

        Module module = moduleOptional.get();
        model.addAttribute("module", module);
        model.addAttribute("course", courseService.findById(courseId)
                                                  .get());

        initBreadcrumbModuleEntries(module);
        getBreadcrumbs().put("/course/" + courseId + "/module/confirm-delete/" + id, "Modul löschen");
        showBreadcrumbs(model);

        return "module/delete";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @GetMapping("/course/{courseId}/module/delete/{id}")
    public String delete(@PathVariable int courseId, @PathVariable int id) {
        Optional<Module> moduleOptional = moduleService.findById(id);
        if (moduleOptional.isEmpty()) {
            return "redirect:/course/" + courseId + "/modules";
        }

        Module module = moduleOptional.get();
        moduleService.delete(module);

        return "redirect:/course/" + courseId + "/modules";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @GetMapping("/course/{courseId}/module/up/{id}")
    public String up(@PathVariable int courseId, @PathVariable int id) {
        Optional<Module> moduleOptional = moduleService.findById(id);
        if (moduleOptional.isEmpty()) {
            return "redirect:/course/" + courseId + "/modules";
        }

        Module module = moduleOptional.get();
        moduleService.orderUp(module);

        return "redirect:/course/" + courseId + "/modules";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @GetMapping("/course/{courseId}/module/down/{id}")
    public String down(@PathVariable int courseId, @PathVariable int id) {
        Optional<Module> moduleOptional = moduleService.findById(id);
        if (moduleOptional.isEmpty()) {
            return "redirect:/course/" + courseId + "/modules";
        }

        Module module = moduleOptional.get();
        moduleService.orderDown(module);

        return "redirect:/course/" + courseId + "/modules";
    }

    private String showDetailPage(int courseId, int id, ModuleForm moduleForm, Model model, boolean initFormData) {
        return showDetailPage(id, moduleForm, model, initFormData, "/course/" + courseId + "/modules", true,
                false);
    }
}
