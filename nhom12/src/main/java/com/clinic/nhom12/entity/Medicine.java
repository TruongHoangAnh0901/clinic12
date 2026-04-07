package com.clinic.nhom12.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "medicines")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Medicine {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name; // Tên thuốc (VD: Paracetamol 500mg)

    @Column(nullable = false)
    private String batchNumber; // Số lô (VD: L01-2026)

    private int quantity; // Số lượng tồn kho

    @Column(nullable = false)
    private LocalDate expirationDate; // Ngày hết hạn (Rất quan trọng để làm tính năng FEFO)

    private double price; // Giá tiền

    private String imageUrl; // Hình ảnh hoặc bao bì thuốc

    // Đáp ứng yêu cầu: "Bật/tắt trạng thái người dùng/sản phẩm" trong ảnh của bạn
    private boolean isActive = true; 
}