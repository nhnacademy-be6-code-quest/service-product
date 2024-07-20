package com.nhnacademy.bookstoreinjun.product.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstoreinjun.config.HeaderConfig;
import com.nhnacademy.bookstoreinjun.config.SecurityConfig;
import com.nhnacademy.bookstoreinjun.controller.ProductController;
import com.nhnacademy.bookstoreinjun.dto.product.InventorySetRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductLikeRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductStateUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.service.product.ProductService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
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

@WebMvcTest(ProductController.class)
@Import({SecurityConfig.class, HeaderConfig.class})
@DisplayName("상품 컨트롤러 테스트")
class ProductControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    HttpHeaders header;

    @MockBean
    ProductService productService;

    private final ObjectMapper objectMapper = new ObjectMapper();


    @DisplayName("상품 조회 - 모든 페이지")
    @Test
    void test() throws Exception {
        mockMvc.perform(get("/api/product/admin/page/all")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_ADMIN")
                )
                .andExpect(status().isOk());

        verify(productService,times(1)).findAllPage(any());
    }


    @DisplayName("상품 상태 업데이트")
    @Test
    void test2() throws Exception {
        ProductStateUpdateRequestDto requestDto = ProductStateUpdateRequestDto.builder()
                .productId(1L)
                .productState(0)
                .build();

        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/api/product/admin/update/state")
                        .with(csrf())
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk());

        verify(productService,times(1)).updateProductState(any());
    }

    @DisplayName("상품 좋아요 요청")
    @Test
    void test3() throws Exception {
        ProductLikeRequestDto requestDto = ProductLikeRequestDto.builder()
                .productId(1L)
                .build();

        String requestBody = objectMapper.writeValueAsString(requestDto);

        mockMvc.perform(post("/api/product/client/like")
                        .with(csrf())
                        .header("X-User-Id", "1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isCreated());

        verify(productService,times(1)).saveProductLike(eq(1L), any());
    }


    @DisplayName("상품 좋아요 취소 요청")
    @Test
    void test4() throws Exception {

        mockMvc.perform(delete("/api/product/client/unlike")
                        .with(csrf())
                        .header("X-User-Id", "1")
                        .param("productId", "1")
                )
                .andExpect(status().isOk());

        verify(productService,times(1)).deleteProductLike(1L,1L);
    }


    @DisplayName("상품 재고 수정 요청")
    @Test
    void test5() throws Exception {
        InventorySetRequestDto requestDto = InventorySetRequestDto.builder()
                .productId(1L)
                .quantityToSet(10L)
                .build();

        String requestBody = objectMapper.writeValueAsString(requestDto);

        when(productService.setProductInventory(requestDto)).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        mockMvc.perform(put("/api/product/admin/inventory/set")
                        .with(csrf())
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_ADMIN")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody)
                )
                .andExpect(status().isOk());

        verify(productService,times(1)).setProductInventory(requestDto);
    }

}