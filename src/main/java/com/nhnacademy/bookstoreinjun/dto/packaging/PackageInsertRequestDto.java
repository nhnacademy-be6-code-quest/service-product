package com.nhnacademy.bookstoreinjun.dto.packaging;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class PackageInsertRequestDto {
    private Long packageId;
    private String packageName;
    private Long productId;
}
