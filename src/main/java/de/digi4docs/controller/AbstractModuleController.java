package de.digi4docs.controller;

import de.digi4docs.form.ModuleForm;
import de.digi4docs.model.Course;
import de.digi4docs.model.Module;
import de.digi4docs.service.ModuleService;
import de.digi4docs.util.RecursiveHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public abstract class AbstractModuleController extends AbstractController {
    @Autowired
    protected ModuleService moduleService;

    protected Module mapFormToModule(Module module, ModuleForm moduleForm) {
        module.setTitle(moduleForm.getTitle());
        module.setSubTitle(moduleForm.getSubTitle());
        module.setDescription(moduleForm.getDescription());
        module.setIcon(moduleForm.getIcon());
        module.setIsActive(moduleForm.getIsActive());

        return module;
    }

    protected String showDetailPage(int parentId, int id, ModuleForm moduleForm, Model model,
            boolean initFormData, String missingModuleRedirect,
            boolean addCourseToModel, boolean addParentToModel) {

        Optional<Module> moduleOptional = moduleService.findById(id);

        if (moduleOptional.isEmpty()) {
            return "redirect:" + missingModuleRedirect;
        }

        Module module = moduleOptional.get();

        if (initFormData) {
            moduleForm.setId(module.getId());
            moduleForm.setTitle(module.getTitle());
            moduleForm.setSubTitle(module.getSubTitle());
            moduleForm.setDescription(module.getDescription());
            moduleForm.setIcon(module.getIcon());
            moduleForm.setIsActive(module.getIsActive());
        }

        model.addAttribute("module", module);
        model.addAttribute("linkCourseId", RecursiveHandler.getCourse(module).getId());

        if (addCourseToModel) {
            model.addAttribute("course", module.getCourse());
        }

        if (addParentToModel) {
            model.addAttribute("parentModule", module.getParent());
        }

        initBreadcrumbModuleEntries(module);
        showBreadcrumbs(model);

        return "module/module";
    }

    protected AbstractModuleController initBreadcrumbCourseEntries(Course course) {
        getBreadcrumbs(true).put("/courses", "Kurse");
        getBreadcrumbs().put("/course/" + course.getId(), course.getTitle());

        return this;
    }

    protected AbstractModuleController initBreadcrumbModuleEntries(Module module) {
        Map<String, String> breadcrumbsReversed = new LinkedHashMap<>();

        while (null != module.getParent()) {
            breadcrumbsReversed.put("/module/" + module.getParent()
                                                       .getId() + "/module/" + module.getId(), module.getTitle());
            module = module.getParent();
        }


        if (null != module.getCourse()) {
            breadcrumbsReversed.put("/course/" + module.getCourse()
                                                       .getId() + "/module/" + module.getId(), module.getTitle());
            breadcrumbsReversed.put("/course/" + module.getCourse()
                                                       .getId(), module.getCourse()
                                                                       .getTitle());
        }

        breadcrumbsReversed.put("/courses", "Kurse");

        ArrayList<String> keyList = new ArrayList<>(breadcrumbsReversed.keySet());

        getBreadcrumbs(true);
        for (int i = keyList.size() - 1; i >= 0; i--) {
            getBreadcrumbs().put(keyList.get(i), breadcrumbsReversed.get(keyList.get(i)));
        }

        return this;
    }
}
