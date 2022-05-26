package de.digi4docs.controller;

import de.digi4docs.form.TextTemplateForm;
import de.digi4docs.model.TextTemplate;
import de.digi4docs.service.TextTemplateService;
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
public class TextTemplateController extends AbstractController {

    @Autowired
    private TextTemplateService textTemplateService;

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/templates")
    public String templates(Model model) {
        model.addAttribute("templates", textTemplateService.findAll());

        getBreadcrumbs(true).put("/templates", "Meine Text-Vorlagen");
        showBreadcrumbs(model);

        return "templates/templates";
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/template")
    public String templateNew(TextTemplateForm textTemplateForm, Model model) {
        getBreadcrumbs(true).put("/templates", "Meine Text-Vorlagen");
        getBreadcrumbs().put("/template", "Neuer Kurs");
        showBreadcrumbs(model);

        return "templates/new-template";
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @PostMapping("/template")
    public String add(@Valid TextTemplateForm textTemplateForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return templateNew(textTemplateForm, model);
        }

        TextTemplate textTemplate = new TextTemplate();
        textTemplate.setTitle(textTemplateForm.getTitle());
        textTemplate.setText(textTemplateForm.getText());

        try {
            textTemplateService.save(textTemplate);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return templateNew(textTemplateForm, model);
        }

        return "redirect:templates";
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/template/{id}")
    public String templateEdit(@PathVariable int id, TextTemplateForm textTemplateForm, Model model) {
        return showDetailPage(id, textTemplateForm, model, true);
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @PostMapping("/template/{id}")
    public String save(@PathVariable int id, @Valid TextTemplateForm textTemplateForm, BindingResult bindingResult,
            Model model) {
        if (bindingResult.hasErrors()) {
            return templateNew(textTemplateForm, model);
        }

        Optional<TextTemplate> textTemplateOptional = textTemplateService.findById(id);
        if (textTemplateOptional.isEmpty()) {
            model.addAttribute("error", "Text-Vorlage konnte nicht gefunden und aktualisiert werden.");
            return showDetailPage(id, textTemplateForm, model, false);
        }

        TextTemplate textTemplate = textTemplateOptional.get();
        textTemplate.setTitle(textTemplateForm.getTitle());
        textTemplate.setText(textTemplateForm.getText());

        try {
            textTemplateService.save(textTemplate);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return showDetailPage(textTemplate.getId(), textTemplateForm, model, false);
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/template/confirm-delete/{id}")
    public String confirmDelete(@PathVariable int id, Model model) {
        Optional<TextTemplate> textTemplateOptional = textTemplateService.findById(id);
        if (textTemplateOptional.isEmpty()) {
            return "redirect:/templates";
        }

        TextTemplate textTemplate = textTemplateOptional.get();
        model.addAttribute("textTemplate", textTemplate);

        getBreadcrumbs(true).put("/templates", "Meine Text-Vorlagen");
        getBreadcrumbs().put("/template/" + id, "Vorlage löschen");
        showBreadcrumbs(model);

        return "templates/delete";
    }

    @PreAuthorize("hasAuthority('TEACHER') or hasAuthority('ADMIN')")
    @GetMapping("/template/delete/{id}")
    public String delete(@PathVariable int id) {
        Optional<TextTemplate> textTemplateOptional = textTemplateService.findById(id);
        if (textTemplateOptional.isEmpty()) {
            return "redirect:/templates";
        }

        TextTemplate textTemplate = textTemplateOptional.get();
        textTemplateService.delete(textTemplate);

        return "redirect:/templates";
    }

    private String showDetailPage(int id, TextTemplateForm textTemplateForm, Model model, boolean initFormData) {
        Optional<TextTemplate> textTemplateOptional = textTemplateService.findById(id);
        if (textTemplateOptional.isEmpty()) {
            return "redirect:/templates";
        }

        TextTemplate textTemplate = textTemplateOptional.get();
        if (initFormData) {
            textTemplateForm.setTitle(textTemplate.getTitle());
            textTemplateForm.setText(textTemplate.getText());
        }

        model.addAttribute("textTemplate", textTemplate);

        getBreadcrumbs(true).put("/templates", "Meine Text-Vorlagen");
        getBreadcrumbs().put("/template/" + id, "Vorlage bearbeiten");
        showBreadcrumbs(model);

        return "templates/template";
    }
}
