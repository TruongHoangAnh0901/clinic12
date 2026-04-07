package com.clinic.nhom12.controller;

import com.clinic.nhom12.dto.AuthResponse;
import com.clinic.nhom12.dto.LoginRequest;
import com.clinic.nhom12.dto.RegisterRequest;
import com.clinic.nhom12.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth") // Đường dẫn gốc cho các chức năng đăng nhập/đăng ký
public class AuthController {

    @Autowired
    private UserService userService;

    // API Đăng ký tài khoản: POST http://localhost:8080/api/auth/register
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequest request) {
        String result = userService.registerPatient(request);
        
        if (result.startsWith("Lỗi")) {
            return ResponseEntity.badRequest().body(result); // Trả về mã lỗi 400
        }
        
        return ResponseEntity.ok(result); // Trả về mã thành công 200
    }
// API Đăng nhập: POST http://localhost:8080/api/auth/login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        AuthResponse result = userService.login(request);
        
        if (result.getMessage().startsWith("Lỗi")) {
            return ResponseEntity.badRequest().body(result.getMessage()); // Trả về chữ lỗi
        }
        
        return ResponseEntity.ok(result); // Trả về object JSON (AuthResponse)
    }

    // API xử lý sau khi đăng nhập Google thành công
    // API lấy danh sách bác sĩ để bệnh nhân đặt lịch
    @GetMapping("/doctors")
    public java.util.List<com.clinic.nhom12.entity.User> getDoctors() {
        return userService.getDoctors();
    }

    // API xử lý sau khi đăng nhập Google thành công
    @GetMapping("/oauth2-success")
    public ResponseEntity<String> oauth2Success(@RequestParam Long userId, @RequestParam String fullName, @RequestParam String role) {
        String html = "<!DOCTYPE html>\n" +
                "<html lang=\"vi\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>Đang xử lý đăng nhập...</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "<script>\n" +
                "    const userId = '" + userId + "';\n" +
                "    const fullName = decodeURIComponent('" + fullName.replace("'", "\\'") + "');\n" +
                "    const role = '" + role.replace("'", "\\'") + "';\n" +
                "    if (fullName && role) {\n" +
                "        localStorage.setItem(\"loggedInUserId\", userId);\n" +
                "        localStorage.setItem(\"loggedInUser\", fullName);\n" +
                "        localStorage.setItem(\"userRole\", role);\n" +
                "    }\n" +
                "    window.location.href = '/';\n" +
                "</script>\n" +
                "</body>\n" +
                "</html>";
        return ResponseEntity.ok()
                .header("Content-Type", "text/html; charset=UTF-8")
                .body(html);
    }
}