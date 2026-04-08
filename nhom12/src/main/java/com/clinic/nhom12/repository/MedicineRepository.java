package com.clinic.nhom12.repository;

import com.clinic.nhom12.entity.Medicine;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineRepository extends JpaRepository<Medicine, Long>, org.springframework.data.jpa.repository.JpaSpecificationExecutor<Medicine> {
    Page<Medicine> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}