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
@RestController
@RequestMapping("/api/medicines")
public class MedicineController {

    @Autowired
    private MedicineRepository medicineRepository;

    // 1. Lấy danh sách toàn bộ thuốc (Có tìm kiếm, phân trang)
    @GetMapping
    public Page<Medicine> getAllMedicines(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        if (keyword == null || keyword.trim().isEmpty()) {
            return medicineRepository.findAll(pageable);
        } else {
            return medicineRepository.findByNameContainingIgnoreCase(keyword, pageable);
        }
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