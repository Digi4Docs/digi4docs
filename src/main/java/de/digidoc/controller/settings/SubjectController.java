package de.digidoc.controller.settings;

import de.digidoc.controller.AbstractController;
import de.digidoc.form.SubjectForm;
import de.digidoc.model.Subject;
import de.digidoc.model.User;
import de.digidoc.service.SubjectService;
import de.digidoc.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Controller
public class SubjectController extends AbstractController {
    @Autowired
    private UserService userService;

    @Autowired
    private SubjectService subjectService;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/settings/subjects")
    public String subjects(Model model) {
        model.addAttribute("subjects", subjectService.findAll());

        addBasicBreadcrumbs();
        showBreadcrumbs(model);

        return "settings/subject/subjects";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/settings/subject")
    public String subject(SubjectForm subjectForm, Model model) {
        model.addAttribute("teachers", userService.findAllTeachers());

        addBasicBreadcrumbs();
        getBreadcrumbs().put("/settings/subject", "Neues Fach");
        showBreadcrumbs(model);

        return "settings/subject/new-subject";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/settings/subject")
    public String add(@Valid SubjectForm subjectForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return subject(subjectForm, model);
        }

        Subject subject = mapFormToSubject(new Subject(), subjectForm);

        try {
            subjectService.save(subject);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return subject(subjectForm, model);
        }

        return "redirect:/settings/subjects";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/settings/subject/{id}")
    public String subject(@PathVariable int id, SubjectForm subjectForm, Model model) {
        return showDetailPage(id, subjectForm, model, true);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/settings/subject/{id}")
    public String update(@PathVariable int id, @Valid SubjectForm subjectForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return showDetailPage(id, subjectForm, model, false);
        }

        Optional<Subject> subjectOptional = subjectService.findById(id);
        if (subjectOptional.isEmpty()) {
            return "redirect:/settings/subject";
        }

        Subject subject = mapFormToSubject(subjectOptional.get(), subjectForm);

        try {
            subjectService.save(subject);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return showDetailPage(id, subjectForm, model, false);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/settings/subject/confirm-delete/{id}")
    public String confirmDelete(@PathVariable int id, Model model) {
        Optional<Subject> subjectOptional = subjectService.findById(id);
        if (subjectOptional.isEmpty()) {
            return "redirect:/settings/subject";
        }

        Subject subject = subjectOptional.get();
        model.addAttribute("subject", subject);

        addBasicBreadcrumbs();
        getBreadcrumbs().put("/settings/subject/" + subject.getId(), subject.getName());
        getBreadcrumbs().put("/settings/subject/confirm-delete/" + subject.getId(), "Fach löschen");
        showBreadcrumbs(model);

        return "settings/subject/delete";
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/settings/subject/delete/{id}")
    public String delete(@PathVariable int id) {
        Optional<Subject> subjectOptional = subjectService.findById(id);
        if (subjectOptional.isEmpty()) {
            return "redirect:/settings/subject";
        }

        Subject subject = subjectOptional.get();
        subjectService.delete(subject);

        return "redirect:/settings/subjects";
    }

    private String showDetailPage(@PathVariable int id, SubjectForm subjectForm, Model model, boolean initFormData) {
        Optional<Subject> subjectOptional = subjectService.findById(id);
        if (subjectOptional.isEmpty()) {
            return "redirect:/settings/subject";
        }

        Subject subject = subjectOptional.get();
        if (initFormData) {
            subjectForm.setId(subject.getId());
            subjectForm.setName(subject.getName());
            subjectForm.setIsActive(subject.getIsActive());
            subjectForm.setTeachers(new ArrayList<>());

            subject.getUsers().forEach(user -> {
                subjectForm.getTeachers().add(user.getId());
            });
        }

        model.addAttribute("subject", subject);
        model.addAttribute("teachers", userService.findAllTeachers());

        addBasicBreadcrumbs();
        getBreadcrumbs().put("/settings/subject/" + subject.getId(), subject.getName());
        showBreadcrumbs(model);

        return "settings/subject/subject";
    }

    private Subject mapFormToSubject(Subject subject, SubjectForm subjectForm) {
        subject.setName(subjectForm.getName());
        subject.setIsActive(subjectForm.getIsActive());

        if (null == subject.getUsers()) {
            subject.setUsers(new HashSet<>());
        }

        subject.getUsers().clear();
        if (!CollectionUtils.isEmpty(subjectForm.getTeachers())) {
            Map<Integer, User> teachers = userService.findAllTeachers().stream().collect(Collectors.toMap(User::getId, teacher -> teacher));
            subjectForm.getTeachers().forEach(userId -> {
                if (teachers.containsKey(userId)) {
                    subject.getUsers().add(teachers.get(userId));
                }
                ;
            });
        }

        return subject;
    }

    private void addBasicBreadcrumbs() {
        getBreadcrumbs(true).put("/settings/subjects", "Einstellungen / Fächer");
    }
}
