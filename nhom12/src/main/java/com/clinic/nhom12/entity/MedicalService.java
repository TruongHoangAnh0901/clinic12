package com.clinic.nhom12.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "medical_services")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalService {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Tên dịch vụ (VD: Siêu âm 4D, Xét nghiệm máu)

    @Column(columnDefinition = "TEXT")
    private String description; // Chi tiết dịch vụ

    @Column(nullable = false)
    private double price; // Giá dịch vụ

    private String imageUrl; // Hình ảnh minh họa

    // Bật/tắt trạng thái hiển thị
    private boolean isActive = true;
}