package com.clinic.nhom12.controller;

import com.clinic.nhom12.dto.DoctorDTO;
import com.clinic.nhom12.entity.User;
import com.clinic.nhom12.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private UserService userService;

    // 1. READ — Lấy danh sách bác sĩ (Có phân trang, tìm kiếm)
    @GetMapping
    public Page<DoctorDTO> getAllDoctors(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<User> rs = userService.getDoctors(keyword, pageable);
        return rs.map(this::convertToDTO);
    }

    // 2. READ — Lấy chi tiết 1 bác sĩ
    @GetMapping("/{id}")
    public ResponseEntity<DoctorDTO> getDoctorById(@PathVariable Long id) {
        Optional<User> doc = userService.getDoctorById(id);
        return doc.map(user -> ResponseEntity.ok(convertToDTO(user)))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 3. CREATE — Tạo bác sĩ mới
    @PostMapping
    public ResponseEntity<String> createDoctor(@RequestBody DoctorDTO dto) {
        String result = userService.createDoctor(dto);
        if ("SUCCESS".equals(result)) {
            return ResponseEntity.ok("Tạo bác sĩ thành công!");
        }
        return ResponseEntity.badRequest().body(result);
    }

    // 4. UPDATE — Sửa bác sĩ
    @PutMapping("/{id}")
    public ResponseEntity<String> updateDoctor(@PathVariable Long id, @RequestBody DoctorDTO dto) {
        String result = userService.updateDoctor(id, dto);
        if ("SUCCESS".equals(result)) {
            return ResponseEntity.ok("Cập nhật bác sĩ thành công!");
        }
        return ResponseEntity.badRequest().body(result);
    }

    // 5. DELETE — Xóa bác sĩ
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteDoctor(@PathVariable Long id) {
        if (userService.deleteDoctor(id)) {
            return ResponseEntity.ok("Đã xóa bác sĩ thành công!");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy bác sĩ này!");
        }
    }

    // --- HELPER FUNCTION ---
    private DoctorDTO convertToDTO(User doc) {
        DoctorDTO dto = new DoctorDTO();
        dto.setId(doc.getId());
        dto.setUsername(doc.getUsername());
        // KHÔNG trả về password
        dto.setFullName(doc.getFullName());
        dto.setEmail(doc.getEmail());
        dto.setPhone(doc.getPhone());
        dto.setAvatarUrl(doc.getAvatarUrl());
        dto.setDoctorInfo(doc.getDoctorInfo());
        dto.setActive(doc.isActive());
        if (doc.getSpecialty() != null) {
            dto.setSpecialtyId(doc.getSpecialty().getId());
            dto.setSpecialtyName(doc.getSpecialty().getName());
        }
        return dto;
    }

    // 6. TOGGLE STATUS (Bật/tắt trạng thái)
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleDoctorStatus(@PathVariable Long id) {
        if (userService.toggleDoctorStatus(id)) {
            return ResponseEntity.ok("Đã thay đổi trạng thái bác sĩ thành công!");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy bác sĩ này!");
        }
    }
}
