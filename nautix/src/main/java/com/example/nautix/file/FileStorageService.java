package com.example.nautix.file;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface FileStorageService {
    
    String storeProductImage(MultipartFile file) throws IOException;
    void deleteProductImage(String storedName) throws IOException;

}

