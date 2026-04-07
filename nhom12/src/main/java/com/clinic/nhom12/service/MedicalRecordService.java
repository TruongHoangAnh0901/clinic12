package com.clinic.nhom12.service;

import com.clinic.nhom12.entity.MedicalRecord;
import com.clinic.nhom12.entity.User;
import com.clinic.nhom12.repository.MedicalRecordRepository;
import com.clinic.nhom12.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.clinic.nhom12.entity.Medicine;
import com.clinic.nhom12.repository.MedicineRepository;
import com.clinic.nhom12.dto.MedicineUsageDTO;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class MedicalRecordService {

    @Autowired
    private MedicalRecordRepository recordRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    // Lấy tất cả bệnh án
    public List<MedicalRecord> getAllRecords() {
        return recordRepository.findAll();
    }

    // Lấy bệnh án theo ID bệnh nhân
    public List<MedicalRecord> getRecordsByPatientId(Long patientId) {
        return recordRepository.findByPatientId(patientId);
    }

    // Lấy bệnh án theo ID bác sĩ
    public List<MedicalRecord> getRecordsByDoctorId(Long doctorId) {
        return recordRepository.findByDoctorId(doctorId);
    }

    // Tạo bệnh án mới
    public MedicalRecord createRecord(MedicalRecord record) {
        if (record.getRecordDate() == null) {
            record.setRecordDate(LocalDate.now());
        }

        // Kiểm tra và trừ số lượng kho thuốc
        if (record.getPrescribedMedicines() != null && !record.getPrescribedMedicines().isEmpty()) {
            for (MedicineUsageDTO usage : record.getPrescribedMedicines()) {
                Optional<Medicine> medOpt = medicineRepository.findById(usage.getId());
                if (medOpt.isPresent()) {
                    Medicine med = medOpt.get();
                    if (med.getQuantity() >= usage.getQuantity()) {
                        med.setQuantity(med.getQuantity() - usage.getQuantity());
                        medicineRepository.save(med);
                    }
                }
            }
        }
        return recordRepository.save(record);
    }

    // Cập nhật bệnh án
    public Optional<MedicalRecord> updateRecord(Long id, MedicalRecord details) {
        Optional<MedicalRecord> optional = recordRepository.findById(id);
        if (optional.isPresent()) {
            MedicalRecord existing = optional.get();
            if (details.getPatient() != null) existing.setPatient(details.getPatient());
            if (details.getDoctor() != null) existing.setDoctor(details.getDoctor());
            if (details.getAppointmentId() != null) existing.setAppointmentId(details.getAppointmentId());
            if (details.getDiagnosis() != null) existing.setDiagnosis(details.getDiagnosis());
            if (details.getPrescription() != null) existing.setPrescription(details.getPrescription());
            if (details.getDoctorNotes() != null) existing.setDoctorNotes(details.getDoctorNotes());
            if (details.getRecordDate() != null) existing.setRecordDate(details.getRecordDate());
            return Optional.of(recordRepository.save(existing));
        }
        return Optional.empty();
    }

    // Xóa bệnh án
    public boolean deleteRecord(Long id) {
        if (recordRepository.existsById(id)) {
            recordRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
