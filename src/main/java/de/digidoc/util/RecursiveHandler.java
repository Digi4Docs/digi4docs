package de.digidoc.util;

import de.digidoc.model.Course;
import de.digidoc.model.Module;
import de.digidoc.model.UserTask;

import java.util.*;

public class RecursiveHandler {

    public static List<Course> getCourses(List<UserTask> userTasks) {
        Map<Integer, Course> courseMap = new HashMap<>();
        userTasks.forEach(userTask -> {
            Module module = userTask.getTask().getModule();
            while (null != module.getParent()) {
                module = module.getParent();
            }
            if (null != module.getCourse() && !courseMap.containsKey(module.getCourse().getId())) {
                courseMap.put(module.getCourse().getId(), module.getCourse());
            }
        });
        List<Course> courses = new ArrayList<>(courseMap.values());
        courses.sort(Comparator.comparing(Course::getTitle));
        return courses;
    }
}
