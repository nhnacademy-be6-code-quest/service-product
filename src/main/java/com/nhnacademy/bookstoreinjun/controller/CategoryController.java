package com.nhnacademy.bookstoreinjun.controller;

import com.nhnacademy.bookstoreinjun.dto.category.*;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.service.category.ProductCategoryService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class CategoryController {
    private final ProductCategoryService productCategoryService;

    private final HttpHeaders header;
    
    @PostMapping("/admin/category/register")
    public ResponseEntity<CategoryRegisterResponseDto> createCategory(@Valid @RequestBody CategoryRegisterRequestDto categoryRegisterRequestDto) {
        return new ResponseEntity<>(productCategoryService.saveCategory(categoryRegisterRequestDto), header, HttpStatus.CREATED);
    }

    @PutMapping("/admin/category/update")
    public ResponseEntity<CategoryUpdateResponseDto> updateCategory(@Valid @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto) {
        return new ResponseEntity<>(productCategoryService.updateCategory(categoryUpdateRequestDto), header, HttpStatus.OK);
    }

    @DeleteMapping("/admin/category/delete/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("categoryId") Long categoryId) {
        return productCategoryService.deleteCategory(categoryId);
    }

    @GetMapping("/admin/categories/all")
    public ResponseEntity<Page<CategoryGetResponseDto>> getAllCategories(
            @Valid @ModelAttribute PageRequestDto pageRequestDto) {
        return new ResponseEntity<>(productCategoryService.getAllCategoryPage(pageRequestDto), header, HttpStatus.OK);
    }

    @GetMapping("/admin/categories/containing")
    public ResponseEntity<Page<CategoryGetResponseDto>> getNameContainingCategories(
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @NotBlank @RequestParam("categoryName") String categoryName) {
        log.info("Called list containing");
        return new ResponseEntity<>(productCategoryService.getNameContainingCategoryPage(pageRequestDto, categoryName), header, HttpStatus.OK);
    }

    @GetMapping("/admin/categories/{categoryId}/sub")
    public ResponseEntity<Page<CategoryGetResponseDto>> getSubCategories(
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @PathVariable("categoryId") Long categoryId) {
        return new ResponseEntity<>(productCategoryService.getSubCategoryPage(pageRequestDto, categoryId), header, HttpStatus.OK);
    }

    @GetMapping("/categories/tree")
    public ResponseEntity<CategoryNodeResponseDto> getCategoriesTree() {
        return ResponseEntity.ok(productCategoryService.getCategoryTree());
    }
}
