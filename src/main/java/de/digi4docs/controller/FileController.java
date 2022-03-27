package de.digi4docs.controller;

import de.digi4docs.model.File;
import de.digi4docs.model.Role;
import de.digi4docs.model.User;
import de.digi4docs.service.FileService;
import de.digi4docs.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.io.ByteArrayInputStream;
import java.util.Optional;

@Controller
public class FileController extends AbstractController {

    @Autowired
    private FileService fileService;

    @Autowired
    private UserService userService;

    @SneakyThrows
    @GetMapping("/public/file/{id}")
    public ResponseEntity<InputStreamResource> download(@PathVariable int id) {
        User currentUser = userService.findCurrentUser();
        boolean isAdmin = userService.hasUserRole(currentUser, Role.ADMIN);
        boolean isTeacher = userService.hasUserRole(currentUser, Role.TEACHER);

        Optional<File> fileOptional = fileService.findById(id);
        if (fileOptional.isPresent()) {
            File file = fileOptional.get();
            boolean isOwnFile = currentUser.getId()
                                           .equals(file.getUserTask()
                                                       .getUser()
                                                       .getId());
            if (!isAdmin && !isTeacher && !isOwnFile) {
                throw new Exception("Du hast keinen Zugriff auf diese Datei.");
            } else {
                InputStreamResource inputStreamResource =
                        new InputStreamResource(new ByteArrayInputStream(file.getData()));
                return ResponseEntity.ok()
                                     .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + file.getName())
                                     .contentType(MediaType.parseMediaType(file.getType()))
                                     .body(inputStreamResource);
            }

        } else {
            throw new Exception("Datei konnte nicht gefunden werden.");
        }
    }
}
