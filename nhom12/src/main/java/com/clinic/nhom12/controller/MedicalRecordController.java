package com.clinic.nhom12.controller;

import com.clinic.nhom12.entity.MedicalRecord;
import com.clinic.nhom12.service.MedicalRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/medical-records")
public class MedicalRecordController {

    // ✅ Lỗi 9 đã sửa: Dùng Service thay vì gọi thẳng Repository
    @Autowired
    private MedicalRecordService medicalRecordService;

    // 1. READ — Lấy toàn bộ bệnh án (dành cho Admin/Bác sĩ)
    @GetMapping
    public List<MedicalRecord> getAllRecords() {
        return medicalRecordService.getAllRecords();
    }

    // 2. READ — Lấy bệnh án theo bệnh nhân
    @GetMapping("/patient/{patientId}")
    public List<MedicalRecord> getRecordsByPatient(@PathVariable Long patientId) {
        return medicalRecordService.getRecordsByPatientId(patientId);
    }

    // 3. READ — Lấy bệnh án theo bác sĩ
    @GetMapping("/doctor/{doctorId}")
    public List<MedicalRecord> getRecordsByDoctor(@PathVariable Long doctorId) {
        return medicalRecordService.getRecordsByDoctorId(doctorId);
    }

    // 4. CREATE — Bác sĩ tạo bệnh án mới sau khi khám
    @PostMapping
    public MedicalRecord createRecord(@RequestBody MedicalRecord record) {
        return medicalRecordService.createRecord(record);
    }

    // 5. UPDATE — Sửa thông tin bệnh án nếu có sai sót
    @PutMapping("/{id}")
    public ResponseEntity<MedicalRecord> updateRecord(@PathVariable Long id, @RequestBody MedicalRecord details) {
        Optional<MedicalRecord> updated = medicalRecordService.updateRecord(id, details);
        return updated
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 6. DELETE — Xóa bệnh án (chỉ Admin theo SecurityConfig)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecord(@PathVariable Long id) {
        if (medicalRecordService.deleteRecord(id)) {
            return ResponseEntity.ok("Đã xóa hồ sơ bệnh án thành công!");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy hồ sơ!");
        }
    }
}