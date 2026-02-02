package com.example.ecommerce_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "permissions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // CATEGORY_VIEW, CATEGORY_CREATE, ...
    @Column(nullable = false, unique = true)
    private String name;

    // ADMIN, SELLER, STAFF...
    @Column(name = "roles_type", nullable = false)
    private String rolesType;

    // CATEGORY, PRODUCT, ORDER...
    @Column(name = "module_name", nullable = false)
    private String moduleName;

    // VIEW, CREATE, UPDATE, DELETE
    @Column(nullable = false)
    private String features;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PermissionStatus status;

    public enum PermissionStatus {
        ACTIVE,
        INACTIVE
    }

}
