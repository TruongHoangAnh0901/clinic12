package com.clinic.nhom12.repository;

import com.clinic.nhom12.entity.Appointment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    // Tìm lịch hẹn theo tài khoản bệnh nhân
    List<Appointment> findByPatientUserId(Long patientUserId);

    // Tìm lịch hẹn theo bác sĩ
    List<Appointment> findByDoctorId(Long doctorId);

    // Tìm lịch hẹn theo trạng thái
    List<Appointment> findByStatus(String status);
}