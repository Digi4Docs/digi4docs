package de.digi4docs.service;

import de.digi4docs.model.File;
import de.digi4docs.model.User;
import de.digi4docs.repository.FileRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class FileService {
    private final FileRepository fileRepository;
    private final UserService userService;

    @Autowired
    public FileService(FileRepository fileRepository, UserService userService) {
        this.fileRepository = fileRepository;
        this.userService = userService;
    }

    public Optional<File> findById(int id)
    {
        return fileRepository.findById(id);
    }
}
