package de.digi4docs.util;

import de.digi4docs.dto.StudentCountResult;
import de.digi4docs.dto.TaskDoneCountResult;
import de.digi4docs.form.StatisticForm;
import de.digi4docs.model.Course;
import de.digi4docs.model.Module;
import de.digi4docs.model.Task;
import de.digi4docs.service.UserService;
import de.digi4docs.service.UserTaskService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class Exporter {
    private final UserService userService;
    private UserTaskService userTaskService;

    @Autowired
    public Exporter(UserService userService, UserTaskService userTaskService) {
        this.userService = userService;
        this.userTaskService = userTaskService;
    }

    public ResponseEntity<InputStreamResource> exportCourse(Course course, StatisticForm form) {
        String filename = "Export " +
                course.getTitle() + "-" +
                form.getStartDate() + "-" +
                form.getEndDate() +
                ".csv";
        InputStreamResource file = new InputStreamResource(createCsv(course, form.getStartDate(), form.getEndDate()));
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                             .contentType(MediaType.parseMediaType("application/csv"))
                             .body(file);
    }

    private ByteArrayInputStream createCsv(Course course, String start, String end) {

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = null;
        try {
            csvPrinter = new CSVPrinter(new PrintWriter(out, false, StandardCharsets.ISO_8859_1),
                    CSVFormat.EXCEL.builder()
                                   .setDelimiter(";")
                                   .build());

            List<StudentCountResult> studentCountGroupedByYears = userService.findStudentCountGroupedByYears();
            Map<Integer, Integer> studentCountResults = studentCountGroupedByYears.stream()
                                                                                  .collect(Collectors.toMap(
                                                                                          StudentCountResult::getYear,
                                                                                          StudentCountResult::getTotal));
            List<Integer> studentYears = new ArrayList<>(studentCountResults.keySet());
            studentYears.sort(Collections.reverseOrder());

            List<List<String>> rows = new ArrayList<>();
            List<String> headerRow = new ArrayList<>();
            headerRow.add("Modul");
            headerRow.add("Untermodul");
            headerRow.add("Aufgabe");
            studentYears.forEach(year -> {
                headerRow.add(year + " abgeschlossen");
                headerRow.add(year + " Anzahl Schüler*innen");
                headerRow.add(year + " Erfüllung in %");
            });
            rows.add(headerRow);

            List<Module> modules = course.getModules();
            if (null != modules && !modules.isEmpty()) {
                boolean firstModuleRow = true;
                String previousSubModuleTitle = "";
                for (Module module : modules) {
                    List<Task> tasks = RecursiveHandler.getTasks(module);
                    List<TaskDoneCountResult> taskDoneCountResult = userTaskService.findDoneTaskCount(
                            tasks.stream()
                                 .map(Task::getId)
                                 .collect(Collectors.toList()),
                            start, end
                    );

                    for (Task task : tasks) {
                        Map<Integer, Integer> yearlyCounts = taskDoneCountResult.stream()
                                                                                .filter(result -> result.getTask()
                                                                                                        .equals(task.getId()))
                                                                                .collect(Collectors.toMap(
                                                                                        TaskDoneCountResult::getYear,
                                                                                        TaskDoneCountResult::getTotal));
                        List<String> data = new ArrayList<>();
                        data.add(firstModuleRow ? module.getTitle() : "");
                        data.add(previousSubModuleTitle.equals(task.getModule()
                                                                   .getTitle()) ? "" : task.getModule()
                                                                                           .getTitle());
                        data.add(task.getTitle());
                        studentYears.forEach(year -> {
                            int doneCount = Optional.ofNullable(yearlyCounts.get(year))
                                                    .orElse(0);
                            data.add(String.valueOf(doneCount));

                            int totalCount = Optional.ofNullable(studentCountResults.get(year))
                                                     .orElse(0);
                            data.add(String.valueOf(totalCount));

                            String percentage = (doneCount > 0 && totalCount > 0) ?
                                    String.format("%.2f", ((float) doneCount * 100) / (float) totalCount) : "0";
                            data.add(percentage);
                        });

                        rows.add(data);
                        previousSubModuleTitle = task.getModule()
                                                     .getTitle();
                        firstModuleRow = false;
                    }
                    firstModuleRow = true;
                }
            }

            csvPrinter.printRecords(rows);
            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Export could not be created", e);
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
