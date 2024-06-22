package com.nhnacademy.bookstoreinjun.dto.packaging;

import com.nhnacademy.bookstoreinjun.entity.Product;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class PackageInfoResponseDto {
    private Long packageId;
    private String packageName;
    private Product product;
}
