package com.clinic.nhom12.repository;

import com.clinic.nhom12.entity.MedicalRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MedicalRecordRepository extends JpaRepository<MedicalRecord, Long> {
    // Tìm tất cả bệnh án của một bệnh nhân
    List<MedicalRecord> findByPatientId(Long patientId);

    // Tìm tất cả bệnh án do một bác sĩ lập
    List<MedicalRecord> findByDoctorId(Long doctorId);
}