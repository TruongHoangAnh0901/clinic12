package com.clinic.nhom12.controller;

import com.clinic.nhom12.entity.Medicine;
import com.clinic.nhom12.repository.MedicineRepository;
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
import java.time.LocalDate;
import java.util.ArrayList;
@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    @Autowired
    private MedicineRepository medicineRepository;

    // 1. Lấy danh sách toàn bộ thuốc (Có tìm kiếm, phân trang và lọc nâng cao)
    @GetMapping
    public Page<Medicine> getAllMedicines(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(required = false) String alertType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));

        Specification<Medicine> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (keyword != null && !keyword.trim().isEmpty()) {
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("name")), "%" + keyword.toLowerCase() + "%"),
                        cb.like(cb.lower(root.get("batchNumber")), "%" + keyword.toLowerCase() + "%")
                ));
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
            
            // alertType: EXPIRING_SOON (<30 ngày), EXPIRED, LOW_STOCK (<10)
            if (alertType != null) {
                LocalDate today = LocalDate.now();
                switch (alertType) {
                    case "EXPIRED":
                        predicates.add(cb.lessThan(root.get("expirationDate"), today));
                        break;
                    case "EXPIRING_SOON":
                        predicates.add(cb.between(root.get("expirationDate"), today, today.plusDays(30)));
                        break;
                    case "LOW_STOCK":
                        predicates.add(cb.lessThan(root.get("quantity"), 10));
                        break;
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        return medicineRepository.findAll(spec, pageable);
    }

    // 2. Thêm thuốc mới vào kho
    @PostMapping
    public Medicine createMedicine(@RequestBody Medicine medicine) {
        return medicineRepository.save(medicine);
    }

    // 3. Cập nhật thông tin thuốc
    @PutMapping("/{id}")
    public ResponseEntity<Medicine> updateMedicine(@PathVariable Long id, @RequestBody Medicine details) {
        Optional<Medicine> optional = medicineRepository.findById(id);
        
        if (optional.isPresent()) {
            Medicine existing = optional.get();
            existing.setName(details.getName());
            existing.setBatchNumber(details.getBatchNumber());
            existing.setQuantity(details.getQuantity());
            existing.setExpirationDate(details.getExpirationDate());
            existing.setPrice(details.getPrice());
            existing.setActive(details.isActive());
            
            return ResponseEntity.ok(medicineRepository.save(existing));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // 4. Xóa thuốc (Xóa mềm)
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteMedicine(@PathVariable Long id) {
        Optional<Medicine> opt = medicineRepository.findById(id);
        if (opt.isPresent()) {
            Medicine medicine = opt.get();
            medicine.setActive(false);
            medicineRepository.save(medicine);
            return ResponseEntity.ok("Đã xóa mềm thuốc thành công!");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy thuốc này!");
        }
    }

    // 5. TOGGLE STATUS
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<String> toggleStatus(@PathVariable Long id) {
        Optional<Medicine> opt = medicineRepository.findById(id);
        if (opt.isPresent()) {
            Medicine medicine = opt.get();
            medicine.setActive(!medicine.isActive());
            medicineRepository.save(medicine);
            return ResponseEntity.ok("Đã thay đổi trạng thái thành công!");
        } else {
            return ResponseEntity.badRequest().body("Không tìm thấy thuốc!");
        }
    }
}