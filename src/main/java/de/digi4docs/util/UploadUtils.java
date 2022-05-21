package de.digi4docs.util;

import de.digi4docs.model.File;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public class UploadUtils {
    public static boolean addUploadData(File file, MultipartFile formFile) {
        String originalFilename = formFile.getOriginalFilename();
        if (null != originalFilename) {
            originalFilename = originalFilename.replace(",", "_");
            originalFilename = originalFilename.replace(":", "_");
            originalFilename = originalFilename.replace(" ", "_");
        }
        file.setName(originalFilename);
        file.setType(formFile.getContentType());
        file.setSize(formFile.getSize());
        try {
            file.setData(formFile.getBytes());
        } catch (IOException e) {
            return false;
        }

        return true;
    }
}
