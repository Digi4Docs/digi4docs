package de.digi4docs.controller;

import de.digi4docs.form.CourseForm;
import de.digi4docs.model.Course;
import de.digi4docs.model.CourseGroup;
import de.digi4docs.service.CourseGroupService;
import de.digi4docs.service.CourseService;
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
public class CourseController extends AbstractController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private CourseGroupService courseGroupService;

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @GetMapping("/courses")
    public String courses(Model model) {
        model.addAttribute("courses", courseService.findAll());

        getBreadcrumbs(true).put("/courses", "Kurse");
        showBreadcrumbs(model);

        return "course/courses";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @GetMapping("/course")
    public String course(CourseForm courseForm, Model model) {
        getBreadcrumbs(true).put("/courses", "Kurse");
        getBreadcrumbs().put("/course", "Neuer Kurs");
        showBreadcrumbs(model);

        return "course/new-course";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @PostMapping("/course")
    public String add(@Valid CourseForm courseForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return course(courseForm, model);
        }

        Course course = mapFormToCourse(new Course(), courseForm);

        try {
            courseService.save(course);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return course(courseForm, model);
        }

        return "redirect:/courses";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @GetMapping("/course/{id}")
    public String course(@PathVariable int id, CourseForm courseForm, Model model) {
        return showDetailPage(id, courseForm, model, true);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @PostMapping("/course/{id}")
    public String update(@PathVariable int id, @Valid CourseForm courseForm, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return showDetailPage(id, courseForm, model, false);
        }

        Optional<Course> courseOptional = courseService.findById(id);
        if (courseOptional.isEmpty()) {
            return "redirect:/course";
        }

        Course course = mapFormToCourse(courseOptional.get(), courseForm);

        try {
            courseService.save(course);
            model.addAttribute("success", true);
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
        }

        return showDetailPage(id, courseForm, model, false);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @GetMapping("/course/confirm-delete/{id}")
    public String confirmDelete(@PathVariable int id, Model model) {
        Optional<Course> courseOptional = courseService.findById(id);
        if (courseOptional.isEmpty()) {
            return "redirect:/course";
        }

        Course course = courseOptional.get();
        model.addAttribute("course", course);

        getBreadcrumbs(true).put("/courses", "Kurse");
        getBreadcrumbs().put("/course/" + id, course.getTitle());
        getBreadcrumbs().put("/course/confirm-delete/" + id, "Kurs löschen");
        showBreadcrumbs(model);

        return "course/delete";
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('COURSES')")
    @GetMapping("/course/delete/{id}")
    public String delete(@PathVariable int id) {
        Optional<Course> courseOptional = courseService.findById(id);
        if (courseOptional.isEmpty()) {
            return "redirect:/course";
        }

        Course course = courseOptional.get();
        courseService.delete(course);

        return "redirect:/courses";
    }

    private String showDetailPage(int id, CourseForm courseForm, Model model, boolean initFormData) {
        Optional<Course> courseOptional = courseService.findById(id);
        if (courseOptional.isEmpty()) {
            return "redirect:/course";
        }

        model.addAttribute("courseGroups", courseGroupService.findAllActive());

        Course course = courseOptional.get();
        if (initFormData) {
            courseForm.setId(course.getId());
            courseForm.setTitle(course.getTitle());
            courseForm.setSubTitle(course.getSubTitle());
            courseForm.setExternalImage(course.getExternalImage());
            courseForm.setDescription(course.getDescription());
            courseForm.setIcon(course.getIcon());
            courseForm.setColor(course.getColor());
            courseForm.setBadgeText(course.getBadgeText());
            courseForm.setIsActive(course.getIsActive());

            courseForm.setCourseGroups(new ArrayList<>());
            course.getCourseGroups()
                  .forEach(courseGroup -> {
                      courseForm.getCourseGroups()
                                .add(courseGroup.getId());
                  });
        }

        model.addAttribute("course", course);

        getBreadcrumbs(true).put("/courses", "Kurse");
        getBreadcrumbs().put("/course/" + id, course.getTitle());
        showBreadcrumbs(model);

        return "course/course";
    }

    private Course mapFormToCourse(Course course, CourseForm courseForm) {
        course.setTitle(courseForm.getTitle());
        course.setSubTitle(courseForm.getSubTitle());
        course.setExternalImage(courseForm.getExternalImage());
        course.setDescription(courseForm.getDescription());
        course.setIcon(courseForm.getIcon());
        course.setColor(courseForm.getColor());
        course.setBadgeText(courseForm.getBadgeText());
        course.setIsActive(courseForm.getIsActive());


        if (null == course.getCourseGroups()) {
            course.setCourseGroups(new HashSet<>());
        }

        course.getCourseGroups()
              .clear();
        if (!CollectionUtils.isEmpty(courseForm.getCourseGroups())) {
            Map<Integer, CourseGroup> groups = courseGroupService.findAllActive()
                                                                 .stream()
                                                                 .collect(Collectors.toMap(CourseGroup::getId,
                                                                         courseGroup -> courseGroup));
            courseForm.getCourseGroups()
                      .forEach(groupId -> {
                          if (groups.containsKey(groupId)) {
                              course.getCourseGroups()
                                    .add(groups.get(groupId));
                          }
                      });
        }

        return course;
    }
}
