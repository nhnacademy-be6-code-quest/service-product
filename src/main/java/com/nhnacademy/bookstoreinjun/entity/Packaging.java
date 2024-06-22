package com.nhnacademy.bookstoreinjun.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@Builder
@Table(name = "package")
@NoArgsConstructor
@AllArgsConstructor
public class Packaging {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long packageId;
    private String packageName;
    @ManyToOne
    @JoinColumn(name = "productId")
    private Product product;
}
