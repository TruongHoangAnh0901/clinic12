package com.clinic.nhom12.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDate;
import java.util.List;
import com.clinic.nhom12.dto.MedicineUsageDTO;

@Entity
@Table(name = "medical_records")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MedicalRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ✅ Lỗi 5 đã sửa: Dùng quan hệ @ManyToOne thay vì lưu String tên bệnh nhân
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private User patient; // Bệnh nhân được khám

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    private User doctor; // Bác sĩ lập bệnh án

    // Giữ lại appointmentId để biết bệnh án thuộc lần khám nào (không bắt buộc)
    private Long appointmentId;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String diagnosis; // Chẩn đoán của bác sĩ

    @Column(columnDefinition = "TEXT")
    private String prescription; // Đơn thuốc (Tên thuốc, liều lượng)

    @Column(columnDefinition = "TEXT")
    private String doctorNotes; // Lời dặn của bác sĩ

    @Column(nullable = false)
    private LocalDate recordDate; // Ngày lập bệnh án

    @Transient
    private List<MedicineUsageDTO> prescribedMedicines; // Tạm dùng bắt List thuốc truyền lên từ UI
}