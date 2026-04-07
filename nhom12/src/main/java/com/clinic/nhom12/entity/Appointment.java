package com.clinic.nhom12.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String patientName; // Tên bệnh nhân (giữ lại để bệnh nhân vãng lai đặt lịch không cần tài khoản)

    @Column(nullable = false)
    private String patientPhone; // Số điện thoại liên hệ

    @Column(nullable = false)
    private LocalDate appointmentDate; // Ngày khám

    @Column(nullable = false)
    private String appointmentTime; // Giờ khám (VD: 08:00 - 09:00)

    // ✅ Lỗi 6 đã sửa: Dùng quan hệ @ManyToOne với User để có FK thực sự
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id")
    private User doctor; // Bác sĩ được chỉ định khám (có thể null nếu chưa phân công)

    @Column(columnDefinition = "TEXT")
    private String reason; // Lý do khám bệnh (Triệu chứng)

    @Column(nullable = false)
    private String status = "CHỜ DUYỆT"; // Trạng thái: CHỜ DUYỆT, ĐÃ XÁC NHẬN, HOÀN THÀNH, ĐÃ HỦY

    // Liên kết tài khoản bệnh nhân (nếu có đăng nhập)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_user_id")
    private User patientUser;

    // Dịch vụ bệnh nhân đăng ký kèm theo (Không bắt buộc)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "medical_service_id")
    private MedicalService medicalService;
}