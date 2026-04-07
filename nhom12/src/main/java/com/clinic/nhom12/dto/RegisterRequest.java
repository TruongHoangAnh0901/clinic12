package com.clinic.nhom12.dto;

import lombok.Data;

@Data // Lombok tự động tạo Getter/Setter
public class RegisterRequest {
    private String username;  // Tên đăng nhập
    private String email;     // Địa chỉ email
    private String password;
    private String fullName;
    private String phone;
}