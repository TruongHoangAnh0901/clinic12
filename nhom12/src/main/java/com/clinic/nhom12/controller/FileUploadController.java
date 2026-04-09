package com.clinic.nhom12.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@RestController
@RequestMapping("/api/upload")
public class FileUploadController {

    /**
     * ⚠️ CẢNH BÁO QUAN TRỌNG - EPHEMERAL STORAGE ISSUE ⚠️
     * 
     * Tính năng upload file này đang lưu trực tiếp vào thư mục "uploads/" trên ổ cứng cục bộ.
     * Khi deploy lên Render Free tier, toàn bộ file sẽ BỊ MẤT mỗi khi:
     * - Server restart (sau khi sleep/wake up)
     * - Deploy mới
     * - Render tự động scale/restart
     * 
     * GIẢI PHÁP:
     * - Sử dụng cloud storage (AWS S3, Cloudinary, Firebase Storage)
     * - Hoặc upgrade lên Render paid tier với persistent disk
     * - Hoặc lưu file vào database dưới dạng base64 (chỉ cho file nhỏ)
     */
    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Vui lòng chọn một tệp để tải lên.");
        }

        try {
            // Đảm bảo thư mục tồn tại
            File directory = new File(UPLOAD_DIR);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Tạo tên file duy nhất để chống trùng lặp (ví dụ: c3f12x..._hinhanh.png)
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFileName = UUID.randomUUID().toString() + extension;

            // Đường dẫn vật lý
            Path path = Paths.get(UPLOAD_DIR + uniqueFileName);
            Files.write(path, file.getBytes());

            // Trả về URI truy cập
            String fileDownloadUri = "/uploads/" + uniqueFileName;
            
            return ResponseEntity.ok(fileDownloadUri);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Không thể lưu file trên máy chủ do lỗi ngoại lệ");
        }
    }
}
