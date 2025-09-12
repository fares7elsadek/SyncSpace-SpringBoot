package com.fares7elsadek.syncspace.shared.services;

import com.fares7elsadek.syncspace.shared.api.StorageService;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Profile("dev")
public class StorageServiceDevImpl implements StorageService {

    private final Path root = Paths.get("uploads");

    public StorageServiceDevImpl() throws IOException {
        if (!Files.exists(root)) {
            Files.createDirectories(root);
        }
    }
    @Override
    public String upload(MultipartFile file) throws IOException {
        var fileName = file.getOriginalFilename();
        int dotIndex = fileName.lastIndexOf('.');
        String name = (dotIndex == -1) ? fileName : fileName.substring(0, dotIndex);
        String extension = (dotIndex == -1) ? "" : fileName.substring(dotIndex);

        var filename = name + "-" + UUID.randomUUID() + extension;
        Path filePath = root.resolve(filename);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return filename;
    }

    @Override
    public void remove(String fileName) throws IOException {
        Path filePath = root.resolve(fileName);
        Files.delete(filePath);
    }
}
