package de.digi4docs.util;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

@ControllerAdvice
public class FileUploadExceptionAdvice {
    @ExceptionHandler(MultipartException.class)
    public String handleAll(Throwable t, Model model) {
        return handleFileException(model);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String handleSizeExceeded(Throwable t, Model model) {
        return handleFileException(model);
    }

    private String handleFileException(Model model) {
        model.addAttribute("error",
                "Deine Datei konnte nicht hochgeladen werden. Achte darauf, dass die Datei nicht größer als 25MB sein " +
                        "darf.");
        return "error";
    }
}
