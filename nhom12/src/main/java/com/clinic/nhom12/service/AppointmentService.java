package com.clinic.nhom12.service;

import com.clinic.nhom12.entity.Appointment;
import com.clinic.nhom12.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private EmailService emailService;

    // Lấy tất cả lịch hẹn
    public List<Appointment> getAllAppointments() {
        return appointmentRepository.findAll();
    }

    // Lấy lịch hẹn theo ID bệnh nhân (User)
    public List<Appointment> getAppointmentsByPatientUserId(Long patientUserId) {
        return appointmentRepository.findByPatientUserId(patientUserId);
    }

    // Lấy lịch hẹn theo ID bác sĩ
    public List<Appointment> getAppointmentsByDoctorId(Long doctorId) {
        return appointmentRepository.findByDoctorId(doctorId);
    }

    // Lấy lịch hẹn theo trạng thái
    public List<Appointment> getAppointmentsByStatus(String status) {
        return appointmentRepository.findByStatus(status);
    }

    // TÌM KIẾM & LỌC NÂNG CAO
    public Page<Appointment> searchAppointments(
            String keyword, String status, LocalDate fromDate, LocalDate toDate, Long doctorId, Pageable pageable) {

        Specification<Appointment> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("patientName")), "%" + keyword.toLowerCase() + "%"));
            }
            if (status != null && !status.trim().isEmpty()) {
                predicates.add(cb.equal(root.get("status"), status));
            }
            if (fromDate != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("appointmentDate"), fromDate));
            }
            if (toDate != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("appointmentDate"), toDate));
            }
            if (doctorId != null) {
                predicates.add(cb.equal(root.join("doctor").get("id"), doctorId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return appointmentRepository.findAll(spec, pageable);
    }

    // Tạo lịch hẹn mới
    public Appointment createAppointment(Appointment appointment) {
        appointment.setStatus("CHỜ DUYỆT");
        return appointmentRepository.save(appointment);
    }

    // Cập nhật lịch hẹn (Admin/Bác sĩ duyệt, đổi trạng thái)
    public Optional<Appointment> updateAppointment(Long id, Appointment details) {
        Optional<Appointment> optional = appointmentRepository.findById(id);
        if (optional.isPresent()) {
            Appointment existing = optional.get();
            String oldStatus = existing.getStatus();

            if (details.getPatientName() != null) existing.setPatientName(details.getPatientName());
            if (details.getPatientPhone() != null) existing.setPatientPhone(details.getPatientPhone());
            if (details.getAppointmentDate() != null) existing.setAppointmentDate(details.getAppointmentDate());
            if (details.getAppointmentTime() != null) existing.setAppointmentTime(details.getAppointmentTime());
            if (details.getDoctor() != null) existing.setDoctor(details.getDoctor());
            if (details.getReason() != null) existing.setReason(details.getReason());
            if (details.getStatus() != null) existing.setStatus(details.getStatus());
            if (details.getMedicalService() != null) existing.setMedicalService(details.getMedicalService());
            
            Appointment saved = appointmentRepository.save(existing);
            
            // Gửi email nếu trạng thái chuyển sang ĐÃ XÁC NHẬN
            if (!"ĐÃ XÁC NHẬN".equals(oldStatus) && "ĐÃ XÁC NHẬN".equals(saved.getStatus())) {
                if (saved.getPatientUser() != null && saved.getPatientUser().getEmail() != null) {
                    emailService.sendAppointmentConfirmationEmail(saved.getPatientUser().getEmail(), saved);
                }
            }

            return Optional.of(saved);
        }
        return Optional.empty();
    }

    // Xóa / hủy lịch hẹn
    public boolean deleteAppointment(Long id) {
        if (appointmentRepository.existsById(id)) {
            appointmentRepository.deleteById(id);
            return true;
        }
        return false;
    }
}
