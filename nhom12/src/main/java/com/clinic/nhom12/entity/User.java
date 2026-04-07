package com.clinic.nhom12.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username; // Dùng email hoặc SĐT để đăng nhập

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String fullName;

    @Column(unique = true)
    private String email;

    @Column(unique = true)
    private String phone;
    private LocalDate dateOfBirth;
    private String gender;
    private String address;
    private String avatarUrl; // Ảnh đại diện người dùng

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "specialty_id")
    private Specialty specialty; // Chuyên khoa (dành cho bác sĩ)

    @Column(columnDefinition = "TEXT")
    private String doctorInfo; // Lời giới thiệu, bằng cấp (dành cho bác sĩ)

    @Column(nullable = false)
    private boolean isActive = true;

    // Mối quan hệ N-N với bảng Role (1 user có nhiều quyền, 1 quyền có nhiều user)
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;
}