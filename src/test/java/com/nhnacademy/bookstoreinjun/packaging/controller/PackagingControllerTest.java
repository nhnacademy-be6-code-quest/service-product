package com.nhnacademy.bookstoreinjun.packaging.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstoreinjun.config.HeaderConfig;
import com.nhnacademy.bookstoreinjun.config.SecurityConfig;
import com.nhnacademy.bookstoreinjun.controller.PackagingController;
import com.nhnacademy.bookstoreinjun.dto.packaging.PackagingGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.packaging.PackagingRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.packaging.PackagingUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.service.packaging.PackagingService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(PackagingController.class)
@Import({SecurityConfig.class, HeaderConfig.class})
@DisplayName("포장지 컨트롤러 테스트")
class PackagingControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    PackagingService packagingService;

    @Autowired
    HttpHeaders header;

    private static final ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("포장지 개별 조회 성공 테스트")
    @Test
    void getSinglePackagingSuccessTest() throws Exception {
        when(packagingService.getPackageInfoByProductId(1L)).thenReturn(new PackagingGetResponseDto());
        mockMvc.perform(get("/api/product/packaging/single/byProduct/1"))
                .andExpect(status().isOk());

        verify(packagingService, times(1)).getPackageInfoByProductId(1L);
    }


    @DisplayName("포장지 신규 등록 성공 테스트")
    @Test
    void registerSuccessTest() throws Exception {
        PackagingRegisterRequestDto requestDto = PackagingRegisterRequestDto.builder()
                .packagingName("test packaging")
                .productName("test product")
                .productThumbnailUrl("test image")
                .productDescription("test description")
                .productPriceStandard(1L)
                .productPriceSales(1L)
                .productInventory(1L)
                .build();
        String requestJson = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(post("/api/product/admin/packaging/register")
                        .with(csrf())
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Role", "ROLE_ADMIN")
                        .header("X-User-Id", 1)
                )
                .andExpect(status().isCreated());

        verify(packagingService, times(1)).registerPackage(any());
    }

    @DisplayName("포장지 수정 성공 테스트")
    @Test
    void updateSuccessTest() throws Exception {
        PackagingUpdateRequestDto requestDto = PackagingUpdateRequestDto.builder()
                .productId(1L)
                .packagingName("updated packaging")
                .productName("updated product")
                .productThumbnailUrl("updated image")
                .productDescription("updated description")
                .productPriceStandard(2L)
                .productPriceSales(2L)
                .productInventory(2L)
                .productState(2)
                .build();
        String requestJson = objectMapper.writeValueAsString(requestDto);
        mockMvc.perform(put("/api/product/admin/packaging/update")
                        .with(csrf())
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Role", "ROLE_ADMIN")
                        .header("X-User-Id", 1)
                )
                .andExpect(status().isOk());

        verify(packagingService, times(1)).updatePackageInfo(any());
    }

    @DisplayName("포장지 조회 성공 테스트 - 모든")
    @Test
    void getAllPackageSuccessTest() throws Exception {

        mockMvc.perform(get("/api/product/packaging/all"))
                .andExpect(status().isOk());

        verify(packagingService, times(1)).getAllPackages(null);
    }


    @DisplayName("포장지 조회 성공 테스트 - 페이지")
    @Test
    void getPackagePageSuccessTest() throws Exception {

        mockMvc.perform(get("/api/product/packaging/page")
                        .param("page", "1")
                        .param("size", "10"))
                .andExpect(status().isOk());

        verify(packagingService, times(1)).getPackagesPage(null, 1, 10);
    }
}
