package com.nhnacademy.bookstoreinjun.dto.product;

import lombok.Builder;

@Builder
public record ProductGetNameAndPriceResponseDto(
        Long productId,
        String productName,
        long productPriceSales
) {
}
