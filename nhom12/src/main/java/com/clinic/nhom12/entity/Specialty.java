package com.clinic.nhom12.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "specialties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Specialty {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Tên chuyên khoa (vd: Khoa Nội, Khoa Nhi)

    @Column(columnDefinition = "TEXT")
    private String description; // Mô tả chuyên khoa

    private String imageUrl; // Link ảnh đại diện cho khoa
    
    private boolean isActive = true; // Yêu cầu "Bật/tắt trạng thái" trong ảnh
}