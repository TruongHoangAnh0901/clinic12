package com.clinic.nhom12.repository;

import com.clinic.nhom12.entity.Specialty;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SpecialtyRepository extends JpaRepository<Specialty, Long> {
    // Spring Data JPA đã hỗ trợ sẵn các hàm Thêm, Sửa, Xóa, Lấy danh sách
    Specialty findByName(String name);
    
    Page<Specialty> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}