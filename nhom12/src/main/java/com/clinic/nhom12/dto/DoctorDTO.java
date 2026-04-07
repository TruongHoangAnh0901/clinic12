package com.clinic.nhom12.dto;

import lombok.Data;

@Data
public class DoctorDTO {
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private String email;
    private String phone;
    private String avatarUrl;
    private Long specialtyId;
    private String specialtyName;
    private String doctorInfo;
    private boolean isActive = true;
}
