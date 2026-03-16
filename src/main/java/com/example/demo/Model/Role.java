package com.example.demo.Model;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Table(name = "role")
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Tên quyền, ví dụ: ROLE_ADMIN, ROLE_USER
    private String name;
}