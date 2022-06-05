package de.digi4docs.util;

import de.digi4docs.model.Course;
import de.digi4docs.model.Module;
import de.digi4docs.model.Task;
import de.digi4docs.model.UserTask;

import java.util.*;
import java.util.stream.Collectors;

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

    public static Course getCourse(Module module) {
        while (null == module.getCourse()) {
            module = module.getParent();
        }

        return module.getCourse();
    }

    public static List<Integer> getModulesTaskIds(List<Module> modules) {

        return modules.stream()
                            .map(Module::getTasks)
                            .flatMap(Collection::stream)
                            .map(Task::getId)
                            .collect(Collectors.toList());
    }

    public static LinkedList<Module> getCourseModules(Course course) {
        LinkedList<Module> courseModules = new LinkedList<>();

        addModules(course.getModules(), courseModules);

        return courseModules;
    }

    public static LinkedList<Module> getModuleModules(Module module) {
        LinkedList<Module> modules = new LinkedList<>();
        modules.add(module);

        addModules(module.getModules(), modules);

        return modules;
    }

    public static LinkedList<Module> getModules(Module module) {
        LinkedList<Module> modules = new LinkedList<>();
        modules.add(module);

        addModules(module.getModules(), modules);

        return modules;

    }

    private static void addModules(List<Module> modules, LinkedList<Module> courseModules) {
        for (Module module : modules) {
            courseModules.add(module);
            if (!module.getModules()
                       .isEmpty()) {
                addModules(module.getModules(), courseModules);
            }
        }
    }
}
