package com.clinic.nhom12.controller;

import com.clinic.nhom12.entity.MedicalService;
import com.clinic.nhom12.repository.MedicalServiceRepository;
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
@RequestMapping("/api/medical-services")
public class MedicalServiceController {

    @Autowired
    private MedicalServiceRepository serviceRepository;

    // 1. READ (Lấy danh sách dịch vụ, có phân trang tìm kiếm)
    @GetMapping
    public Page<MedicalService> getAllServices(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        if (keyword == null || keyword.trim().isEmpty()) {
            return serviceRepository.findAll(pageable);
        } else {
            return serviceRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }
    }

    // 2. CREATE (Thêm dịch vụ mới)
    @PostMapping
    public MedicalService createService(@RequestBody MedicalService service) {
        return serviceRepository.save(service);
    }

    // 3. UPDATE (Sửa thông tin dịch vụ)
    @PutMapping("/{id}")
    public ResponseEntity<MedicalService> updateService(@PathVariable Long id, @RequestBody MedicalService details) {
        Optional<MedicalService> optional = serviceRepository.findById(id);
        
        if (optional.isPresent()) {
            MedicalService existing = optional.get();
            existing.setName(details.getName());
            existing.setDescription(details.getDescription());
            existing.setPrice(details.getPrice());
            existing.setActive(details.isActive());
            
            return ResponseEntity.ok(serviceRepository.save(existing));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. DELETE (Xóa mềm dịch vụ)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteService(@PathVariable Long id) {
        Optional<MedicalService> opt = serviceRepository.findById(id);
        if (opt.isPresent()) {
            MedicalService service = opt.get();
            service.setActive(false);
            serviceRepository.save(service);
            return ResponseEntity.ok("Đã xóa mềm dịch vụ y tế thành công!");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy dịch vụ!");
        }
    }

    // 5. TOGGLE STATUS
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleStatus(@PathVariable Long id) {
        Optional<MedicalService> opt = serviceRepository.findById(id);
        if (opt.isPresent()) {
            MedicalService service = opt.get();
            service.setActive(!service.isActive());
            serviceRepository.save(service);
            return ResponseEntity.ok("Đã thay đổi trạng thái thành công!");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy dịch vụ!");
        }
    }
}