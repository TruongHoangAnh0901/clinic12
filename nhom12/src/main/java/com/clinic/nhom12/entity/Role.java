package com.clinic.nhom12.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "roles")
@Data // Lombok tự sinh getter, setter
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name; // Ví dụ: ROLE_ADMIN, ROLE_DOCTOR, ROLE_PATIENT
    
    private String description;
}