package com.nhnacademy.bookstoreinjun.category.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import com.nhnacademy.bookstoreinjun.dto.category.CategoryGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.category.CategoryRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.category.CategoryRegisterResponseDto;
import com.nhnacademy.bookstoreinjun.dto.category.CategoryUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.category.CategoryUpdateResponseDto;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.entity.ProductCategory;
import com.nhnacademy.bookstoreinjun.exception.DuplicateException;
import com.nhnacademy.bookstoreinjun.exception.InvalidSortNameException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundIdException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundNameException;
import com.nhnacademy.bookstoreinjun.exception.PageOutOfRangeException;
import com.nhnacademy.bookstoreinjun.repository.ProductCategoryRelationRepository;
import com.nhnacademy.bookstoreinjun.repository.ProductCategoryRepository;
import com.nhnacademy.bookstoreinjun.service.category.ProductCategoryServiceImpl;
import com.nhnacademy.bookstoreinjun.util.FindAllSubCategoriesUtilImpl;
import java.util.Arrays;
import java.util.LinkedHashSet;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class ProductCategoryServiceTest {

    @InjectMocks
    private ProductCategoryServiceImpl categoryService;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductCategoryRelationRepository productCategoryRelationRepository;

    @Mock
    private FindAllSubCategoriesUtilImpl findAllSubCategoriesUtil;

    private static final String TEST_CATEGORY_NAME = "Test ProductCategory";

    private static final String TEST_PARENT_CATEGORY_NAME = "Test Parent ProductCategory";

    private final CategoryRegisterRequestDto requestDto = CategoryRegisterRequestDto.builder()
            .categoryName(TEST_CATEGORY_NAME)
            .build();


    @DisplayName("카테고리 신규 등록 성공 테스트")
    @Test
    void saveCategoryTestSuccess(){

        CategoryRegisterRequestDto dto = CategoryRegisterRequestDto.builder()
                .categoryName(TEST_CATEGORY_NAME)
                .parentCategoryName(TEST_PARENT_CATEGORY_NAME)
                .build();

        when(productCategoryRepository.existsByCategoryName(TEST_PARENT_CATEGORY_NAME)).thenReturn(true);

        CategoryRegisterResponseDto resultDto =  categoryService.saveCategory(dto);

        assertNotNull(resultDto);

        verify(productCategoryRepository, times(1)).save(any(ProductCategory.class));
    }

    @DisplayName("카테고리 신규 등록 실패 테스트 - 존재하지 않는 상위 카테고리명")
    @Test
    void saveCategoryTestFailureByNotFoundParentCategoryName(){
        when(productCategoryRepository.existsByCategoryName(TEST_PARENT_CATEGORY_NAME)).thenReturn(false);

        CategoryRegisterRequestDto dto = CategoryRegisterRequestDto.builder()
                .categoryName(TEST_CATEGORY_NAME)
                .parentCategoryName(TEST_PARENT_CATEGORY_NAME)
                .build();

        assertThrows(NotFoundNameException.class, () -> categoryService.saveCategory(dto));
    }

    @DisplayName("카테고리 신규 등록 실패 테스트 - 중복되는 카테고리명")
    @Test
    void saveCategoryTestFailureByExistingCategoryName(){
        when(productCategoryRepository.existsByCategoryName(TEST_CATEGORY_NAME)).thenReturn(true);

        assertThrows(DuplicateException.class, () -> categoryService.saveCategory(requestDto));
    }

    @DisplayName("카테고리 업데이트 성공 테스트")
    @Test
    void updateCategoryTestSuccess(){
        when(productCategoryRepository.existsByCategoryName(TEST_CATEGORY_NAME)).thenReturn(true);
        when(productCategoryRepository.existsByCategoryName("new" + TEST_CATEGORY_NAME)).thenReturn(false);
        when(productCategoryRepository.findByCategoryName(TEST_CATEGORY_NAME)).thenReturn(new ProductCategory());

        CategoryUpdateRequestDto dto = CategoryUpdateRequestDto.builder()
                .currentCategoryName(TEST_CATEGORY_NAME)
                .newCategoryName("new" + TEST_CATEGORY_NAME)
                .build();

        CategoryUpdateResponseDto resultDto = categoryService.updateCategory(dto);

        assertNotNull(resultDto);
        assertEquals( TEST_CATEGORY_NAME, resultDto.previousCategoryName());
        assertEquals("new" + TEST_CATEGORY_NAME, resultDto.newCategoryName());
        assertNotNull(resultDto.updateTime());
    }

    @DisplayName("카테고리 업데이트 실패 테스트 - 바뀌기 이전의 카테고리명이 현재 존재하지 않음")
    @Test
    void updateCategoryTestFailureByNotFoundCategoryName(){
        when(productCategoryRepository.existsByCategoryName(TEST_CATEGORY_NAME)).thenReturn(false);

        CategoryUpdateRequestDto dto = CategoryUpdateRequestDto.builder()
                .currentCategoryName(TEST_CATEGORY_NAME)
                .newCategoryName("new" + TEST_CATEGORY_NAME)
                .build();

        assertThrows(NotFoundNameException.class, () -> categoryService.updateCategory(dto));
    }

    @DisplayName("카테고리 업데이트 실패 테스트 - 바뀌기 이후의 카테고리명이 중복됨")
    @Test
    void updateCategoryTestFailureByExistingCategoryName(){
        when(productCategoryRepository.existsByCategoryName(TEST_CATEGORY_NAME)).thenReturn(true);
        when(productCategoryRepository.existsByCategoryName("new" + TEST_CATEGORY_NAME)).thenReturn(true);


        CategoryUpdateRequestDto dto = CategoryUpdateRequestDto.builder()
                .currentCategoryName(TEST_CATEGORY_NAME)
                .newCategoryName("new" + TEST_CATEGORY_NAME)
                .build();

        assertThrows(DuplicateException.class, () -> categoryService.updateCategory(dto));
    }

    @DisplayName("카테고리 조회 성공 테스트")
    @Test
    void getCategoryDtoTestSuccess(){
        when(productCategoryRepository.findByCategoryName(TEST_CATEGORY_NAME)).thenReturn(new ProductCategory());

        CategoryGetResponseDto dto = categoryService.getCategoryDtoByName(TEST_CATEGORY_NAME);

        verify(productCategoryRepository, times(1)).findByCategoryName(TEST_CATEGORY_NAME);

        assertNotNull(dto);
    }

    @DisplayName("카테고리 조회 실패 테스트 - 존재하지 않는 카테고리명")
    @Test
    void getCategoryDtoTestFailureByNotFoundCategoryName(){
        when(productCategoryRepository.findByCategoryName(TEST_CATEGORY_NAME)).thenReturn(null);

        assertThrows(NotFoundNameException.class, () -> categoryService.getCategoryDtoByName(TEST_CATEGORY_NAME));

        verify(productCategoryRepository, times(1)).findByCategoryName(TEST_CATEGORY_NAME);
    }

    @DisplayName("모든 카테고리 페이지 조회 성공 테스트")
    @Test
    void getAllCategoryPageTestSuccess(){
        PageRequestDto pageRequestDto = PageRequestDto.builder().build();
        when(productCategoryRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(
                ProductCategory.builder()
                       .categoryName(TEST_CATEGORY_NAME)
                       .build(),
                ProductCategory.builder()
                        .categoryName(TEST_CATEGORY_NAME)
                        .build()
        )));

        Page<CategoryGetResponseDto> dto = categoryService.getAllCategoryPage(pageRequestDto);
        assertNotNull(dto);

        assertEquals(2, dto.getTotalElements());
    }



    @DisplayName("모든 카테고리 페이지 조회 실패 테스트 - 잘못된 정렬 조건")
    @Test
    void getAllCategoryPageTestFailureByWrongSort(){
        PageRequestDto pageRequestDto = PageRequestDto.builder().sort("wrong").build();


        assertThrows(InvalidSortNameException.class, () -> categoryService.getAllCategoryPage(pageRequestDto));
    }

    @DisplayName("모든 카테고리 페이지 조회 실패 테스트 - 초과된 페이지 넘버")
    @Test
    void getAllTagPageTestFailureByOutOfPageRange(){
        PageRequestDto pageRequestDto = PageRequestDto.builder()
                .page(100)
                .build();

        when(productCategoryRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(
                ProductCategory.builder()
                        .categoryName(TEST_CATEGORY_NAME)
                        .build(),
                ProductCategory.builder()
                        .categoryName(TEST_CATEGORY_NAME)
                        .build()
        )));

        assertThrows(PageOutOfRangeException.class, () -> categoryService.getAllCategoryPage(pageRequestDto));
    }

    @DisplayName("특정 이름이 포함된 카테고리의 페이지 조회 성공 테스트")
    @Test
    void getNameContainingCategoryPageTestSuccess(){
        PageRequestDto pageRequestDto = PageRequestDto.builder().build();
        when(productCategoryRepository.findAllByCategoryNameContaining(any(Pageable.class), eq(TEST_CATEGORY_NAME))).thenReturn(new PageImpl<>(Arrays.asList(
                ProductCategory.builder()
                        .categoryName(TEST_CATEGORY_NAME)
                        .build(),
                ProductCategory.builder()
                        .categoryName(TEST_CATEGORY_NAME)
                        .build()
        )));

        Page<CategoryGetResponseDto> dto = categoryService.getNameContainingCategoryPage(pageRequestDto, TEST_CATEGORY_NAME);

        assertNotNull(dto);

        assertEquals(2, dto.getTotalElements());
    }

    @DisplayName("특정 이름이 포함된 카테고리의 페이지 조회 실패 테스트 - 잘못된 정렬 조건")
    @Test
    void getNameContainingCategoryPageTestFailureByWrongSort(){
        PageRequestDto pageRequestDto = PageRequestDto.builder().sort("wrong").build();

        assertThrows(InvalidSortNameException.class, () -> categoryService.getNameContainingCategoryPage(pageRequestDto, TEST_CATEGORY_NAME));
    }

    @DisplayName("특정 카테고리의 하위 카테고리 페이지 조회 성공 테스트")
    @Test
    void getSubCategoryPageTestSuccess(){
        PageRequestDto pageRequestDto = PageRequestDto.builder().build();


        when(findAllSubCategoriesUtil.getAllSubcategorySet(1L)).thenReturn(new LinkedHashSet<>(Arrays.asList(
                ProductCategory.builder()
                        .categoryName(TEST_CATEGORY_NAME)
                        .build(),
                ProductCategory.builder()
                        .categoryName(TEST_CATEGORY_NAME)
                        .build()
        )));


        Page<CategoryGetResponseDto> dto = categoryService.getSubCategoryPage(pageRequestDto, 1L);

        assertNotNull(dto);

        assertEquals(2, dto.getTotalElements());
    }

    @DisplayName("특정 카테고리의 하위 카테고리 페이지 조회 실패 테스트 - 존재하지 않는 상위 카테고리")
    @Test
    void getSubCategoryPageTestFailureByNotExistingParentCategory(){
        PageRequestDto pageRequestDto = PageRequestDto.builder().build();

        when(findAllSubCategoriesUtil.getAllSubcategorySet(1L)).thenThrow(new NotFoundIdException("category", 1L));

        assertThrows(NotFoundIdException.class, () -> categoryService.getSubCategoryPage(pageRequestDto, 1L));
    }

    @DisplayName("특정 카테고리의 하위 카테고리 페이지 조회 실패 테스트 - 잘못된 정렬 조건")
    @Test
    void getSubCategoryPageTestFailureByWrongSort(){
        PageRequestDto pageRequestDto = PageRequestDto.builder().page(1).sort("wrong").build();

        assertThrows(InvalidSortNameException.class, () -> categoryService.getSubCategoryPage(pageRequestDto, 1L));
    }

    @DisplayName("카테고리 삭제 성공 테스트")
    @Test
    void deleteCategoryTestSuccess(){
        when(productCategoryRelationRepository.existsByProductCategoryIn(any())).thenReturn(false);

        ResponseEntity<Void> response = categoryService.deleteCategory(1L);
        assertNotNull(response);
        assertEquals(200, response.getStatusCode().value());
        verify(productCategoryRelationRepository, times(1)).existsByProductCategoryIn(any());
        verify(productCategoryRepository, times(1)).deleteAll(any());
    }

    @DisplayName("카테고리 삭제 실패 테스트")
    @Test
    void deleteCategoryTestFailure(){
        when(productCategoryRelationRepository.existsByProductCategoryIn(any())).thenReturn(true);

        ResponseEntity<Void> response = categoryService.deleteCategory(1L);
        assertNotNull(response);
        assertEquals(409, response.getStatusCode().value());
        verify(productCategoryRelationRepository, times(1)).existsByProductCategoryIn(any());
        verify(productCategoryRepository, times(0)).deleteAll(any());
    }
}
