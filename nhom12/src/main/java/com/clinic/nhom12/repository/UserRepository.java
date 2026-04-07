package com.clinic.nhom12.repository;

import com.clinic.nhom12.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    
    // Lấy danh sách người dùng theo tên role (vd: ROLE_DOCTOR)
    java.util.List<User> findByRoles_Name(String roleName);

    Page<User> findByRoles_Name(String roleName, Pageable pageable);
    Page<User> findByFullNameContainingIgnoreCaseAndRoles_Name(String keyword, String roleName, Pageable pageable);
}