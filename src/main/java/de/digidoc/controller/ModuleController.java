package de.digidoc.controller;

import de.digidoc.form.ModuleForm;
import de.digidoc.model.Module;
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
public class ModuleController extends AbstractModuleController {

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/module/{id}/modules")
    public String modules(@PathVariable int id, Model model) {
        Module parentModule = moduleService.findById(id).get();
        model.addAttribute("modules", moduleService.findAllByParentModule(parentModule.getId()));
        model.addAttribute("parentModule", parentModule);

        if (null != parentModule.getCourse()) {
            model.addAttribute("course", parentModule.getCourse());
        }

        initBreadcrumbModuleEntries(parentModule).showBreadcrumbs(model);

        return "module/modules";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/module/{id}/module")
    public String module(@PathVariable int id, ModuleForm moduleForm, Model model) {
        Module module = moduleService.findById(id).get();
        model.addAttribute("module", module);

        initBreadcrumbModuleEntries(module).showBreadcrumbs(model);

        return "module/new-module";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/module/{id}/module")
    public String add(@PathVariable int id, @Valid ModuleForm moduleForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return module(id, moduleForm, model);
        }

        Module parentModule = moduleService.findById(id).get();
        Module module = mapFormToModule(new Module(), moduleForm);
        module.setParent(parentModule);

        try {
            moduleService.save(module);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return module(id, moduleForm, model);
        }

        return "redirect:/module/" + parentModule.getId() + "/modules";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/module/{parentId}/module/{id}")
    public String course(@PathVariable int parentId, @PathVariable int id, ModuleForm moduleForm, Model model) {
        return showDetailPage(parentId, id, moduleForm, model, true);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/module/{parentId}/module/{id}")
    public String update(@PathVariable int parentId, @PathVariable int id, @Valid ModuleForm moduleForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return showDetailPage(parentId, id, moduleForm, model, false);
        }

        Optional<Module> moduleOptional = moduleService.findById(id);
        if (moduleOptional.isEmpty()) {
            return "redirect:/module/" + parentId + "/modules";
        }

        Module module = mapFormToModule(moduleOptional.get(), moduleForm);

        try {
            moduleService.save(module);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return showDetailPage(parentId, id, moduleForm, model, false);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/module/{parentId}/module/confirm-delete/{id}")
    public String confirmDelete(@PathVariable int parentId, @PathVariable int id, Model model) {
        Optional<Module> moduleOptional = moduleService.findById(id);
        if (moduleOptional.isEmpty()) {
            return "redirect:/module/" + parentId + "/modules";
        }

        Module module = moduleOptional.get();
        model.addAttribute("module", module);
        model.addAttribute("parentModule", moduleService.findById(parentId).get());

        initBreadcrumbModuleEntries(module);
        getBreadcrumbs().put("/module/" + parentId + "/module/confirm-delete/" + id, "Modul löschen");
        showBreadcrumbs(model);

        return "module/delete";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/module/{parentId}/module/delete/{id}")
    public String delete(@PathVariable int parentId, @PathVariable int id) {
        Optional<Module> moduleOptional = moduleService.findById(id);
        if (moduleOptional.isEmpty()) {
            return "redirect:/module/" + parentId + "/modules";
        }

        Module module = moduleOptional.get();
        moduleService.delete(module);

        return "redirect:/module/" + parentId + "/modules";
    }

    private String showDetailPage(int parentId, int id, ModuleForm moduleForm, Model model, boolean initFormData) {
        return showDetailPage(parentId, id, moduleForm, model, initFormData, "/module/" + parentId + "/modules", false, true);
    }
}
