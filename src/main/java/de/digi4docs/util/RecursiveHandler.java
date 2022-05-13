package de.digi4docs.util;

import de.digi4docs.model.Course;
import de.digi4docs.model.Module;
import de.digi4docs.model.Task;
import de.digi4docs.model.UserTask;
import org.springframework.ui.Model;

import java.util.*;

public class RecursiveHandler {

    public static List<Task> getTasks(Module module) {
        List<Task> tasks = new ArrayList<>();
        if (null != module.getTasks() && !module.getTasks()
                                                .isEmpty()) {
            tasks.addAll(module.getTasks());
        }
        getNestedTasks(module.getModules(), tasks);

        return tasks;
    }

    private static void getNestedTasks(List<Module> modules, List<Task> tasks) {
        if (null != modules && !modules.isEmpty()) {
            modules.forEach(module -> {
                if (null != module.getTasks() && !module.getTasks()
                                                        .isEmpty()) {
                    tasks.addAll(module.getTasks());
                }
                getNestedTasks(module.getModules(), tasks);
            });
        }
    }

    public static Map<Integer, Course> getUserTaskCourseMap(List<UserTask> userTasks) {
        Map<Integer, Course> courseMap = new HashMap<>();
        userTasks.forEach(userTask -> {
            Module module = userTask.getTask()
                                    .getModule();
            while (null != module.getParent()) {
                module = module.getParent();
            }
            if (null != module.getCourse() && !courseMap.containsKey(userTask.getId())) {
                courseMap.put(userTask.getId(), module.getCourse());
            }
        });
        return courseMap;
    }

    public static List<Course> getCourses(List<UserTask> userTasks) {
        Map<Integer, Course> courseMap = new HashMap<>();
        userTasks.forEach(userTask -> {
            Module module = userTask.getTask()
                                    .getModule();
            while (null != module.getParent()) {
                module = module.getParent();
            }
            if (null != module.getCourse() && !courseMap.containsKey(module.getCourse()
                                                                           .getId())) {
                courseMap.put(module.getCourse()
                                    .getId(), module.getCourse());
            }
        });
        List<Course> courses = new ArrayList<>(courseMap.values());
        courses.sort(Comparator.comparing(Course::getTitle));
        return courses;
    }

    public static Course getCourse(Module module)
    {
        while (null == module.getCourse()) {
            module = module.getParent();
        }

        return module.getCourse();
    }
}
