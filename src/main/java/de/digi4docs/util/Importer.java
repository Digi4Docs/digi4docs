package de.digi4docs.util;

import de.digi4docs.model.Role;
import de.digi4docs.model.User;
import de.digi4docs.model.UserRole;
import de.digi4docs.service.UserService;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Component
public class Importer {
    private UserService userService;

    @Autowired
    public Importer(UserService userService) {
        this.userService = userService;
    }

    public String validateFile(MultipartFile file) {
        if (file.isEmpty()) {
            return "Bitte wähle eine Datei aus";
        }

        String extension = FilenameUtils.getExtension(file.getOriginalFilename());
        if (!"csv".equals(extension)) {
            return "Deine Datei ist im '" + extension + "'-Format. Das ist nicht zulässig. Bitte lade nur CSV-Dateien" +
                    " hoch.";
        }

        return null;
    }

    public List<List<String>> readCsv(MultipartFile file) throws IOException {
        List<List<String>> records = new ArrayList<>();
        try (Scanner scanner = new Scanner(file.getInputStream())) {
            // skip title columns
            if (scanner.hasNextLine()) {
                scanner.nextLine();
            }

            while (scanner.hasNextLine()) {
                records.add(getRecordFromLine(scanner.nextLine()));
            }
        }
        return records;
    }

    private List<String> getRecordFromLine(String line) {
        List<String> values = new ArrayList<>();
        try (Scanner rowScanner = new Scanner(line)) {
            rowScanner.useDelimiter(";");
            while (rowScanner.hasNext()) {
                values.add(rowScanner.next());
            }
        }

        return values;
    }

    public Map<Integer, String> importData(List<List<String>> userData, Boolean isActive, List<Role> roles) {
        Map<Integer, String> errors = new HashMap<>();

        for (int i = 0; i < userData.size(); i++) {
            List<String> row = userData.get(i);
            try {
                User user = new User();
                user.setFirstname(row.get(0));
                user.setLastname(row.get(1));
                user.setEmail(row.get(2));

                String classYear = row.get(3);
                if (null != classYear && !classYear.isEmpty()) {
                    user.setClassYear(Integer.valueOf(classYear));
                }

                String classNumber = row.get(4);
                if (null != classNumber && !classNumber.isEmpty()) {
                    user.setClassNumber(Integer.valueOf(classNumber));
                }

                user.setClassIdentifier(null == row.get(5) ? "" : row.get(5));
                user.setIsActive(isActive);
                user.setPassword("");
                user.setRoles(new HashSet<>());

                roles.forEach(role -> {
                    user.getRoles()
                        .add(new UserRole(null, user, role));
                });

                userService.add(user);
            } catch (Exception e) {
                errors.put(i, e.getMessage());
            }
        }

        return errors;
    }

    public ResponseEntity<InputStreamResource> createTemplateFile()
    {
        String filename = "Import-Vorlage.csv";

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        CSVPrinter csvPrinter = null;
        try {
            csvPrinter = new CSVPrinter(new PrintWriter(out, false, StandardCharsets.ISO_8859_1),
                    CSVFormat.EXCEL.builder()
                                   .setDelimiter(";")
                                   .build());

            List<List<String>> rows = new ArrayList<>();
            List<String> headerRow = new ArrayList<>();
            headerRow.add("Vorname");
            headerRow.add("Nachname");
            headerRow.add("E-Mail");
            headerRow.add("Jahrgang");
            headerRow.add("Klasse (Zahl)");
            headerRow.add("Klasse (Buchstabe)");
            rows.add(headerRow);

            csvPrinter.printRecords(rows);
            csvPrinter.flush();
        } catch (IOException e) {
            throw new RuntimeException("Export could not be created", e);
        }

        InputStreamResource file = new InputStreamResource(new ByteArrayInputStream(out.toByteArray()));
        return ResponseEntity.ok()
                             .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + filename)
                             .contentType(MediaType.parseMediaType("application/csv"))
                             .body(file);
    }
}
