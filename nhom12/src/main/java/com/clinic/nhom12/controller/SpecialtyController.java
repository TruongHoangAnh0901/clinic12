package com.clinic.nhom12.controller;

import com.clinic.nhom12.entity.Specialty;
import com.clinic.nhom12.repository.SpecialtyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
@RestController
@RequestMapping("/api/specialties")
public class SpecialtyController {

    @Autowired
    private SpecialtyRepository specialtyRepository;

    // 1. READ (Lấy danh sách tất cả chuyên khoa, có phân trang/tìm kiếm)
    @GetMapping
    public Page<Specialty> getAllSpecialties(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        if (keyword == null || keyword.trim().isEmpty()) {
            return specialtyRepository.findAll(pageable);
        } else {
            return specialtyRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }
    }

    // 2. CREATE (Thêm chuyên khoa mới)
    @PostMapping
    public Specialty createSpecialty(@RequestBody Specialty specialty) {
        return specialtyRepository.save(specialty);
    }

    // 3. UPDATE (Sửa thông tin chuyên khoa)
    @PutMapping("/{id}")
    public ResponseEntity<Specialty> updateSpecialty(@PathVariable Long id, @RequestBody Specialty specialtyDetails) {
        Optional<Specialty> optionalSpecialty = specialtyRepository.findById(id);
        
        if (optionalSpecialty.isPresent()) {
            Specialty existingSpecialty = optionalSpecialty.get();
            existingSpecialty.setName(specialtyDetails.getName());
            existingSpecialty.setDescription(specialtyDetails.getDescription());
            existingSpecialty.setImageUrl(specialtyDetails.getImageUrl());
            existingSpecialty.setActive(specialtyDetails.isActive());
            
            return ResponseEntity.ok(specialtyRepository.save(existingSpecialty));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. DELETE (Xóa mềm - tắt isActive)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSpecialty(@PathVariable Long id) {
        Optional<Specialty> opt = specialtyRepository.findById(id);
        if (opt.isPresent()) {
            Specialty specialty = opt.get();
            specialty.setActive(false);
            specialtyRepository.save(specialty);
            return ResponseEntity.ok("Đã xóa mềm chuyên khoa thành công!");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy chuyên khoa này!");
        }
    }

    // 5. TOGGLE STATUS (Bật/tắt trạng thái)
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleStatus(@PathVariable Long id) {
        Optional<Specialty> opt = specialtyRepository.findById(id);
        if (opt.isPresent()) {
            Specialty specialty = opt.get();
            specialty.setActive(!specialty.isActive());
            specialtyRepository.save(specialty);
            return ResponseEntity.ok("Đã thay đổi trạng thái thành công!");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy chuyên khoa!");
        }
    }
}