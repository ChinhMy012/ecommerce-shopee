package com.example.ecommerce_backend.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "category_attributes")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CategoryAttribute {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;

    @Enumerated(EnumType.STRING)
    private AttributeType type; // TEXT, NUMBER, SELECT

    @Column(columnDefinition = "TEXT")
    private String optionsJson;

    public enum AttributeType {
        TEXT, NUMBER, SELECT
    }
}