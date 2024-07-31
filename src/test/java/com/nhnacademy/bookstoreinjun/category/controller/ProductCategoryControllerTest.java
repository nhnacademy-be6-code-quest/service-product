package com.nhnacademy.bookstoreinjun.category.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstoreinjun.config.HeaderConfig;
import com.nhnacademy.bookstoreinjun.config.SecurityConfig;
import com.nhnacademy.bookstoreinjun.controller.CategoryController;
import com.nhnacademy.bookstoreinjun.dto.category.CategoryRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.category.CategoryUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.exception.DuplicateException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundNameException;
import com.nhnacademy.bookstoreinjun.service.category.ProductCategoryService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;



@WebMvcTest(CategoryController.class)
@Import({SecurityConfig.class, HeaderConfig.class})
class ProductCategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductCategoryService productCategoryService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("카테고리 신규 등록 성공 테스트")
    @Test
    void saveCategoryTest1() throws Exception {
        CategoryRegisterRequestDto dto = CategoryRegisterRequestDto.builder()
                        .categoryName("test productCategory 1")
                        .parentCategoryName("parent category 1")
                        .build();
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/product/admin/category/register")
                        .with(csrf())
                        .header("X-User-Role", "ROLE_ADMIN")
                        .header("X-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isCreated());

        verify(productCategoryService,times(1)).saveCategory(any(CategoryRegisterRequestDto.class));
    }

    @DisplayName("카테고리 신규 등록 실패 테스트 - 중복 카테고리명")
    @Test
    void saveCategoryTest2() throws Exception {

        CategoryRegisterRequestDto dto = CategoryRegisterRequestDto.builder()
                .categoryName("duplicate productCategory")
                .parentCategoryName("parent category 1")
                .build();
        String json = objectMapper.writeValueAsString(dto);

        when(productCategoryService.saveCategory(dto)).thenThrow(DuplicateException.class);

        mockMvc.perform(post("/api/product/admin/category/register")
                        .with(csrf())
                        .header("X-User-Role", "ROLE_ADMIN")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isConflict());

        verify(productCategoryService,times(1)).saveCategory(any(CategoryRegisterRequestDto.class));
    }

    @DisplayName("카테고리 신규 등록 실패 테스트 - 존재하지 않는 상위 카테고리명")
    @Test
    void saveCategoryTest3() throws Exception {
        CategoryRegisterRequestDto dto = CategoryRegisterRequestDto.builder()
                .categoryName("test productCategory 1")
                .parentCategoryName("not exist productCategory")
                .build();
        String json = objectMapper.writeValueAsString(dto);

        when(productCategoryService.saveCategory(dto)).thenThrow(NotFoundNameException.class);


        mockMvc.perform(post("/api/product/admin/category/register")
                        .with(csrf())
                        .header("X-User-Role", "ROLE_ADMIN")
                        .header("X-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isNotFound());

        verify(productCategoryService,times(1)).saveCategory(any(CategoryRegisterRequestDto.class));
    }

    @DisplayName("카테고리 업데이트 성공 테스트")
    @Test
    void updateCategoryTestSuccess() throws Exception {
        CategoryUpdateRequestDto dto = CategoryUpdateRequestDto.builder()
                .currentCategoryName("test category1")
                .newCategoryName("new test category1")
                .build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/product/admin/category/update")
                        .with(csrf())
                        .header("X-User-Role", "ROLE_ADMIN")
                        .header("X-User-Id", 1)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk());

        verify(productCategoryService,times(1)).updateCategory(dto);
    }

    @DisplayName("카테고리 삭제 성공 테스트")
    @Test
    void deleteCategoryTestSuccess() throws Exception {

        mockMvc.perform(delete("/api/product/admin/category/delete/1")
                        .with(csrf())
                        .header("X-User-Role", "ROLE_ADMIN")
                        .header("X-User-Id", 1)
                )
                .andExpect(status().isOk());

        verify(productCategoryService,times(1)).deleteCategory(1L);
    }

    @DisplayName("카테고리 페이지 조회 : 모든 카테고리 - 성공 테스트")
    @Test
    void getAllCategoryTestSuccess() throws Exception {

        mockMvc.perform(get("/api/product/admin/categories/all")
                .header("X-User-Id", "1")
                .header("X-User-Role", "ROLE_ADMIN"))
                .andExpect(status().isOk());

        verify(productCategoryService,times(1)).getAllCategoryPage(any());
    }

    @DisplayName("카테고리 페이지 조회 : 이름 포함 - 성공 테스트")
    @Test
    void getNameContainingCategoryTestSuccess() throws Exception {

        mockMvc.perform(get("/api/product/admin/categories/containing")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_ADMIN")
                        .param("categoryName", "test"))
                .andExpect(status().isOk());

        verify(productCategoryService,times(1)).getNameContainingCategoryPage(any(),eq("test"));
    }

    @DisplayName("카테고리 페이지 조회 : 하위 카테고리 - 성공 테스트")
    @Test
    void getSubCategoryTestSuccess() throws Exception {

        mockMvc.perform(get("/api/product/admin/categories/1/sub")
                .header("X-User-Id", "1")
                .header("X-User-Role", "ROLE_ADMIN"))
                .andExpect(status().isOk());

        verify(productCategoryService,times(1)).getSubCategoryPage(any(),eq(1L));
    }

    @DisplayName("카테고리 트리 조회 - 성공 테스트")
    @Test
    void getTreeCategoryTestSuccess() throws Exception {

        mockMvc.perform(get("/api/product/categories/tree"))
                .andExpect(status().isOk());

        verify(productCategoryService,times(1)).getCategoryTree();
    }
}
