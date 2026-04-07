package com.clinic.nhom12.repository;

import com.clinic.nhom12.entity.MedicalService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalServiceRepository extends JpaRepository<MedicalService, Long> {
    Page<MedicalService> findByNameContainingIgnoreCase(String keyword, Pageable pageable);
}