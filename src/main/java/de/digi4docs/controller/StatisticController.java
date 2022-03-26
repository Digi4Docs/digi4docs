package de.digi4docs.controller;

import de.digi4docs.form.StatisticForm;
import de.digi4docs.model.Course;
import de.digi4docs.model.User;
import de.digi4docs.service.CourseService;
import de.digi4docs.util.Exporter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
public class StatisticController extends AbstractController {

    @Autowired
    private CourseService courseService;

    @Autowired
    private Exporter exporter;

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/statistic")
    public String statistic(Model model, StatisticForm statisticForm) {
        return showPage(model);
    }

    @PreAuthorize("hasAuthority('ADMIN')")
    @PostMapping("/statistic")
    public ResponseEntity<InputStreamResource> statistic(@Valid StatisticForm statisticForm, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            throw new RuntimeException("Ungültige Formulareingabe");
        }

        Optional<Course> courseOptional = courseService.findById(statisticForm.getCourseId());
        if (courseOptional.isEmpty()) {
            throw new RuntimeException("Der Kurs wurde nicht gefunden");
        }

        return exporter.exportCourse(courseOptional.get(), statisticForm);
    }

    public String showPage(Model model)
    {
        List<Course> courses = courseService.findAll();
        model.addAttribute("courses", courses);

        return "statistic/statistic";
    }
}
