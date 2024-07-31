package com.nhnacademy.bookstoreinjun.dto.tag;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record TagRegisterRequestDto(
        @NotBlank
        String tagName
){}
