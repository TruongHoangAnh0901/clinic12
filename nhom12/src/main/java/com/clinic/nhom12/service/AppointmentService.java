package com.clinic.nhom12.service;

import com.clinic.nhom12.entity.Appointment;
import com.clinic.nhom12.repository.AppointmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AppointmentService {

    @Autowired
    private AppointmentRepository appointmentRepository;

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
            if (details.getPatientName() != null) existing.setPatientName(details.getPatientName());
            if (details.getPatientPhone() != null) existing.setPatientPhone(details.getPatientPhone());
            if (details.getAppointmentDate() != null) existing.setAppointmentDate(details.getAppointmentDate());
            if (details.getAppointmentTime() != null) existing.setAppointmentTime(details.getAppointmentTime());
            if (details.getDoctor() != null) existing.setDoctor(details.getDoctor());
            if (details.getReason() != null) existing.setReason(details.getReason());
            if (details.getStatus() != null) existing.setStatus(details.getStatus());
            if (details.getMedicalService() != null) existing.setMedicalService(details.getMedicalService());
            return Optional.of(appointmentRepository.save(existing));
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
