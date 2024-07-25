package com.nhnacademy.bookstoreinjun.dto.book;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

@Builder
public record BookProductUpdateRequestDto (
        @NotNull
        Long productId,

        @NotNull
        @Length(min = 2)
        String productName,
        boolean packable,

        @Length(min = 10, max =10) String isbn,

        @Length(min = 13, max =13) String isbn13,

        String cover,

        @NotNull
        @NotBlank
        String productDescription,
        long productPriceSales,
        long productInventory,
        int productState,

        @NotNull(message = "{must.have.category}")
        @Size(min = 1, message = "{must.have.category}")
        @Size(max = 10, message = "{too.much.category}")
        Set<String> categories,

        Set<String> tags
)
{
}
