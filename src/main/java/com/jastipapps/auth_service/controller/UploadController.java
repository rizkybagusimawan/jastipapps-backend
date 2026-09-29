package com.jastipapps.auth_service.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Value("${app.upload.dir}")
    private String uploadDir;

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadFile(
            @RequestParam("file") MultipartFile file) {

        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "File kosong"));
            }

            String originalFilename = file.getOriginalFilename();

            String extension =
                    originalFilename != null && originalFilename.contains(".")
                            ? originalFilename.substring(
                                    originalFilename.lastIndexOf("."))
                            : ".jpg";

            String newFilename = UUID.randomUUID() + extension;

            File dir = new File(uploadDir);

            if (!dir.exists()) {
                dir.mkdirs();
            }

            Path filePath = Path.of(uploadDir, newFilename);

            Files.write(filePath, file.getBytes());

            // Simpan relative path agar bisa digunakan
            // oleh Android, iOS, dan platform lainnya.
            String fileUrl = "/uploads/" + newFilename;

            return ResponseEntity.ok(
                    Map.of("url", fileUrl)
            );

        } catch (IOException e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of(
                            "error",
                            "Gagal upload: " + e.getMessage()
                    ));
        }
    }
}