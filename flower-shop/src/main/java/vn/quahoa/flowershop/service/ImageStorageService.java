package vn.quahoa.flowershop.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ImageStorageService {

    @Value("${app.upload.base-url:http://localhost:8080}")
    private String baseUrl;

    private static final String UPLOAD_DIR = "uploads/products";

    /**
     * Save image from MultipartFile
     */
    public String saveImageFromFile(MultipartFile file, Long productId, boolean isMain) throws IOException {
        String subDir = isMain ? "/main" : "";
        String uploadPath = UPLOAD_DIR + "/" + productId + subDir;
        Path uploadDir = Paths.get(uploadPath);

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        String prefix = isMain ? "main_" : "";
        String fileName = prefix + System.currentTimeMillis() + "_" + file.getOriginalFilename();
        Path filePath = uploadDir.resolve(fileName);
        
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        return baseUrl + "/" + uploadPath + "/" + fileName;
    }

    /**
     * Download and save image from URL
     */
    public String saveImageFromUrl(String imageUrl, Long productId, boolean isMain) throws IOException {
        if (imageUrl == null || imageUrl.trim().isEmpty()) {
            return null;
        }

        // If it's already a local URL, return as is
        if (imageUrl.startsWith(baseUrl)) {
            return imageUrl;
        }

        String subDir = isMain ? "/main" : "";
        String uploadPath = UPLOAD_DIR + "/" + productId + subDir;
        Path uploadDir = Paths.get(uploadPath);

        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }

        // Extract file extension from URL
        String extension = getExtensionFromUrl(imageUrl);
        String prefix = isMain ? "main_" : "";
        String fileName = prefix + System.currentTimeMillis() + extension;
        Path filePath = uploadDir.resolve(fileName);

        // Download image from URL
        try (InputStream in = new URL(imageUrl).openStream()) {
            Files.copy(in, filePath, StandardCopyOption.REPLACE_EXISTING);
            log.info("Downloaded image from URL: {} to {}", imageUrl, filePath);
        } catch (Exception e) {
            log.error("Failed to download image from URL: {}", imageUrl, e);
            // If download fails, return original URL
            return imageUrl;
        }

        return baseUrl + "/" + uploadPath + "/" + fileName;
    }

    /**
     * Extract file extension from URL
     */
    private String getExtensionFromUrl(String url) {
        try {
            String path = new URL(url).getPath();
            int lastDot = path.lastIndexOf('.');
            if (lastDot > 0) {
                return path.substring(lastDot);
            }
        } catch (Exception e) {
            log.warn("Could not extract extension from URL: {}", url);
        }
        return ".jpg"; // default extension
    }

    /**
     * Delete image file
     */
    public void deleteImage(String imageUrl) {
        if (imageUrl == null || !imageUrl.startsWith(baseUrl)) {
            return;
        }

        try {
            String relativePath = imageUrl.replace(baseUrl + "/", "");
            Path filePath = Paths.get(relativePath);
            Files.deleteIfExists(filePath);
            log.info("Deleted image: {}", filePath);
        } catch (Exception e) {
            log.error("Failed to delete image: {}", imageUrl, e);
        }
    }
}
