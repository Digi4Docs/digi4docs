package de.digi4docs.controller;

import de.digi4docs.dto.TaskReviewRow;
import de.digi4docs.model.Module;
import de.digi4docs.model.*;
import de.digi4docs.service.CourseService;
import de.digi4docs.service.ModuleService;
import de.digi4docs.service.UserService;
import de.digi4docs.service.UserTaskService;
import de.digi4docs.util.ProgressCountProvider;
import de.digi4docs.util.RecursiveHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.*;
import java.util.stream.Collectors;

@Controller
public class HomeController {
    @Autowired
    private CourseService courseService;

    @Autowired
    private ModuleService moduleService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserTaskService userTaskService;

    @Autowired
    private ProgressCountProvider progressCountProvider;

    @GetMapping(value = {"/home", "/"})
    public String home(Model model) {
        User currentUser = userService.findCurrentUser();
        model.addAttribute("currentUser", currentUser);

        boolean isStudent = userService.hasUserRole(currentUser, Role.STUDENT);
        if (isStudent) {

            List<Course> availableCoursesForUser = courseService.findAllActiveUngrouped();
            availableCoursesForUser.addAll(courseService.findAllActiveGroupedOfUser());
            List<Integer> availableCourseIds = availableCoursesForUser.stream()
                                                                      .map(Course::getId)
                                                                      .collect(Collectors.toList());

            Map<Course, Integer> coursePersonTaskCount =
                    progressCountProvider.getPersonalCourseTaskCountMap(availableCourseIds);
            List<Integer> doneTasks = userTaskService.findAllDoneTaskIds(currentUser);
            List<Course> finishedCourses = assignFinishedCourses(model, coursePersonTaskCount, doneTasks);
            assignBadges(model, doneTasks);

            model.addAttribute("courses", availableCoursesForUser
                    .stream()
                    .filter(course -> finishedCourses.stream()
                                                     .noneMatch(finishedCourse -> course.getId()
                                                                                        .equals(finishedCourse.getId())))
                    .filter(course -> !coursePersonTaskCount.containsKey(course))
                    .collect(Collectors.toList()));

            model.addAttribute("personalCourses", coursePersonTaskCount);
            model.addAttribute("courseTaskCounts",
                    progressCountProvider.getGeneralCourseTaskCountMap(coursePersonTaskCount.keySet()));

            List<UserTask> rejectedOfCurrentUser = userTaskService.findRejectedOfCurrentUser();
            model.addAttribute("rejectedTasks", rejectedOfCurrentUser);
            model.addAttribute("rejectTaskCourses", RecursiveHandler.getUserTaskCourseMap(rejectedOfCurrentUser));

        }

        boolean showTeacherTasks =
                userService.hasUserRole(currentUser, Role.ADMIN) || userService.hasUserRole(currentUser, Role.TEACHER);
        model.addAttribute("showTeacherTasks", showTeacherTasks);
        if (showTeacherTasks) {
            List<TaskReviewRow> teacherTasks = userTaskService.findTransmittedOfCurrentUser();
            model.addAttribute("teacherTasks", teacherTasks);
        }

        return "home";
    }

    private void assignBadges(Model model, List<Integer> doneTasks) {
        LinkedHashMap<String, LinkedList<Module>> badgeMap = new LinkedHashMap<>();
        moduleService.findAllBadgeModules()
                     .forEach(badgeModule -> {
                         String key = null == badgeModule.getParent() ? "Course" + badgeModule.getCourse()
                                                                                              .getId() :
                                 "Module" + badgeModule.getParent()
                                                       .getId();
                         if (!badgeMap.containsKey(key)) {
                             badgeMap.put(key, new LinkedList<>());
                         }

                         LinkedList<Module> modules = RecursiveHandler.getModuleModules(badgeModule);
                         List<Integer> taskIds = RecursiveHandler.getModulesTaskIds(modules);
                         if (taskIds.size() > 0) {
                             int userTaskCount = Math.toIntExact(doneTasks.stream()
                                                                          .filter(taskIds::contains)
                                                                          .count());
                             if (userTaskCount == taskIds.size()) {
                                 badgeMap.get(key)
                                         .add(badgeModule);
                             }
                         }
                     });
        model.addAttribute("badgeMap", badgeMap);
    }

    private List<Course> assignFinishedCourses(Model model, Map<Course, Integer> coursePersonTaskCount,
            List<Integer> doneTasks) {
        List<Course> finishedCourses = new ArrayList<>();
        for (Course course : coursePersonTaskCount.keySet()) {
            LinkedList<Module> courseModules = RecursiveHandler.getCourseModules(course);
            List<Integer> taskIds = RecursiveHandler.getModulesTaskIds(courseModules);
            int doneCourseTasks = Math.toIntExact(doneTasks.stream()
                                                           .filter(taskIds::contains)
                                                           .count());
            if (taskIds.size() == doneCourseTasks) {
                finishedCourses.add(course);
            }
        }
        finishedCourses.forEach(coursePersonTaskCount::remove);
        model.addAttribute("finishedCourses", finishedCourses);

        return finishedCourses;
    }
}