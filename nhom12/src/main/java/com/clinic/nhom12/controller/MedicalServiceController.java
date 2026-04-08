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
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
@RestController
@RequestMapping("/api/medical-services")
public class MedicalServiceController {

    @Autowired
    private MedicalServiceRepository serviceRepository;

    // 1. READ (Lấy danh sách dịch vụ, có phân trang tìm kiếm)
    @GetMapping
    public Page<MedicalService> getAllServices(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Specification<MedicalService> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"));
            }
            if (isActive != null) {
                predicates.add(cb.equal(root.get("isActive"), isActive));
            }
            if (minPrice != null) {
                predicates.add(cb.ge(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.le(root.get("price"), maxPrice));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return serviceRepository.findAll(spec, pageable);
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