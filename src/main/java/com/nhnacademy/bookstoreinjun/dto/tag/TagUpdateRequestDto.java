package com.nhnacademy.bookstoreinjun.dto.tag;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TagUpdateRequestDto (
        @NotBlank
        String currentTagName,
        @NotBlank
        String newTagName
)
{}
