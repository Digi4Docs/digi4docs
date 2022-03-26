package de.digi4docs.util;

import de.digi4docs.model.Module;
import de.digi4docs.model.*;
import de.digi4docs.service.UserTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class ProgressCountProvider {
    private UserTaskService userTaskService;

    @Autowired
    public ProgressCountProvider(UserTaskService userTaskService) {
        this.userTaskService = userTaskService;
    }

    public Map<Course, Integer> getPersonalCourseTaskCountMap() {
        List<UserTask> userTasks = userTaskService.findByUser();

        if (userTasks.isEmpty()) {
            return new HashMap<>();
        }

        Map<Integer, Course> courses = new HashMap<>();
        Map<Integer, Integer> courseTaskCounts = new HashMap<>();

        userTasks.forEach(userTask -> {
            Module module = userTask.getTask().getModule();
            while (null != module.getParent()) {
                module = module.getParent();
            }

            if (null != module.getCourse()) {
                int currentTaskCount = TaskStatus.DONE.equals(userTask.getStatus()) ? 1 : 0;
                if (!courses.containsKey(module.getCourse().getId())) {
                    courses.put(module.getCourse().getId(), module.getCourse());
                    courseTaskCounts.put(module.getCourse().getId(), currentTaskCount);
                } else {
                    courseTaskCounts.put(module.getCourse().getId(), courseTaskCounts.get(module.getCourse().getId()) + currentTaskCount);
                }
            }
        });

        List<Course> coursesArr = new ArrayList<>(courses.values()).stream()
                .filter(Course::getIsActive)
                .sorted(Comparator.comparing(Course::getTitle))
                .collect(Collectors.toList());

        HashMap<Course, Integer> courseTaskCountMap = new LinkedHashMap<>();
        coursesArr.forEach(course -> {
            courseTaskCountMap.put(course, courseTaskCounts.get(course.getId()));
        });

        return courseTaskCountMap;
    }

    public Map<Integer, Integer> getGeneralCourseTaskCountMap(Set<Course> courses) {
        Map<Integer, Integer> courseTaskCounts = new HashMap<>();
        courses.forEach(course -> {
            List<Module> modules = course.getModules();
            int taskCount = 0;
            if (null != modules && !modules.isEmpty()) {
                for (Module module : modules) {
                    if (module.getIsActive()) {
                        taskCount += module.getTasks().stream().filter(Task::getIsActive).count();
                        taskCount = countTasks(module, taskCount);
                    }
                }
            }
            courseTaskCounts.put(course.getId(), taskCount);
        });

        return courseTaskCounts;
    }

    public Map<Integer, Integer> getPersonalModuleTaskCountMap(List<Module> modules) {
        List<UserTask> userTasks = userTaskService.findByUser();

        if (userTasks.isEmpty()) {
            return new HashMap<>();
        }

        Map<Integer, Integer> moduleTaskCounts = new HashMap<>();
        Map<Integer, Module> moduleMap = modules.stream().collect(Collectors.toMap(Module::getId, module -> module));

        for (UserTask userTask : userTasks) {
            Module module = userTask.getTask().getModule();

            while (null != module.getParent()) {
                module = module.getParent();
            }

            if (moduleMap.containsKey(module.getId())) {
                int currentTaskCount = TaskStatus.DONE.equals(userTask.getStatus()) ? 1 : 0;
                if (!moduleTaskCounts.containsKey(module.getId())) {
                    moduleTaskCounts.put(module.getId(), currentTaskCount);
                } else {
                    moduleTaskCounts.put(module.getId(), moduleTaskCounts.get(module.getId()) + currentTaskCount);
                }
            }
        }

        return moduleTaskCounts;
    }

    public Map<Integer, Integer> getGeneralModuleTaskCountMap(List<Module> modules) {
        Map<Integer, Integer> taskCounts = new HashMap<>();

        if (null != modules && !modules.isEmpty()) {
            for (Module module : modules) {
                int taskCount = 0;
                if (module.getIsActive()) {
                    taskCount += module.getTasks().stream().filter(Task::getIsActive).count();
                    taskCount = countTasks(module, taskCount);
                }
                taskCounts.put(module.getId(), taskCount);
            }
        }

        return taskCounts;
    }

    private int countTasks(Module module, int taskCount) {
        if (null != module.getModules() && !module.getModules().isEmpty()) {
            for (Module subModule : module.getModules()) {
                if (subModule.getIsActive()) {
                    taskCount += subModule.getTasks().stream().filter(Task::getIsActive).count();
                    taskCount = countTasks(subModule, taskCount);
                }
            }
        }

        return taskCount;
    }
}
