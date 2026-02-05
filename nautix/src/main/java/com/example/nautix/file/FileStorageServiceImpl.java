package com.example.nautix.file;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    @Value("${file.upload.product-images-dir}")
    private String productImagesDir;

     @PostConstruct
    public void init() {
        try {
            Path uploadPath = Paths.get(productImagesDir).toAbsolutePath().normalize();
            if (Files.notExists(uploadPath)) {
                Files.createDirectories(uploadPath);
                System.out.println("✅ Created upload directory: " + uploadPath);
            } else {
                System.out.println("ℹ️ Upload directory already exists: " + uploadPath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize upload directory!", e);
        }
    }

    @Override
    public String storeProductImage(MultipartFile file) throws IOException {
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        String ext = "";
        int idx = original.lastIndexOf('.');
        if (idx > 0) ext = original.substring(idx);
        String stored = UUID.randomUUID() + ext;
        Path target = Paths.get(productImagesDir).resolve(stored);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        return stored;
    }

@Override
public void deleteProductImage(String storedName) throws IOException {
    Path uploadDir = Paths.get(productImagesDir)
                         .toAbsolutePath()
                         .normalize();
    Path filePath  = uploadDir.resolve(storedName).normalize();
    System.out.println("Deleting file: " + filePath);
    Files.deleteIfExists(filePath);
}

}

