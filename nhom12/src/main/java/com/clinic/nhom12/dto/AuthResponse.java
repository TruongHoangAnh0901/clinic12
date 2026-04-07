package com.clinic.nhom12.dto;

import lombok.Data;

@Data
public class AuthResponse {
    private String message;   // Thông báo (Thành công / Thất bại)
    private Long userId;      // ID của người dùng để tải hồ sơ
    private String fullName;  // Tên để hiển thị Avatar
    private String avatarUrl; // Ảnh đại diện
    private String role;      // Quyền để phân luồng trang web
}