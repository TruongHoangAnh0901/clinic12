package com.clinic.nhom12.controller;

import com.clinic.nhom12.entity.Appointment;
import com.clinic.nhom12.service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.time.LocalDate;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    // ✅ Lỗi 10 đã sửa: Dùng Service thay vì gọi thẳng Repository
    @Autowired
    private AppointmentService appointmentService;

    // 1. READ — Lấy tất cả lịch hẹn (Admin/Bác sĩ xem)
    @GetMapping
    public List<Appointment> getAllAppointments() {
        return appointmentService.getAllAppointments();
    }

    // 1.1 READ — Lọc lịch hẹn nâng cao (có phân trang)
    @GetMapping("/search")
    public Page<Appointment> searchAppointments(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "appointmentDate") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        return appointmentService.searchAppointments(keyword, status, fromDate, toDate, doctorId, pageable);
    }

    // 2. READ — Lấy lịch hẹn theo trạng thái
    @GetMapping("/status/{status}")
    public List<Appointment> getByStatus(@PathVariable String status) {
        return appointmentService.getAppointmentsByStatus(status);
    }

    // 3. READ — Lấy lịch hẹn của một bác sĩ
    @GetMapping("/doctor/{doctorId}")
    public List<Appointment> getByDoctor(@PathVariable Long doctorId) {
        return appointmentService.getAppointmentsByDoctorId(doctorId);
    }

    // 4. READ — Lấy lịch hẹn của một bệnh nhân (theo tài khoản)
    @GetMapping("/patient/{patientId}")
    public List<Appointment> getByPatient(@PathVariable Long patientId) {
        return appointmentService.getAppointmentsByPatientUserId(patientId);
    }

    // 5. CREATE — Bệnh nhân đặt lịch mới
    @PostMapping
    public Appointment createAppointment(@RequestBody Appointment appointment) {
        return appointmentService.createAppointment(appointment);
    }

    // 6. UPDATE — Admin/Bác sĩ duyệt lịch hẹn, đổi trạng thái
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(@PathVariable Long id, @RequestBody Appointment details) {
        Optional<Appointment> updated = appointmentService.updateAppointment(id, details);
        return updated
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // 7. DELETE — Hủy/xóa lịch hẹn
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointment(@PathVariable Long id) {
        if (appointmentService.deleteAppointment(id)) {
            return ResponseEntity.ok("Đã xóa lịch hẹn thành công!");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy lịch hẹn này!");
        }
    }
}