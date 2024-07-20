package com.nhnacademy.bookstoreinjun.cart.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstoreinjun.config.HeaderConfig;
import com.nhnacademy.bookstoreinjun.config.SecurityConfig;
import com.nhnacademy.bookstoreinjun.controller.CartController;
import com.nhnacademy.bookstoreinjun.dto.cart.CartGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.cart.CartRequestDto;
import com.nhnacademy.bookstoreinjun.service.cart.CartService;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import({SecurityConfig.class, HeaderConfig.class})
@DisplayName("장바구니 컨트롤러 테스트")
class CartControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    HttpHeaders header;

    @MockBean
    CartService cartService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("장바구니 복구 테스트")
    @Test
    void restoreTest() throws Exception {
        mockMvc.perform(get("/api/product/client/cart/restore")
                        .header("X-User-Id", 1))
                .andExpect(status().isOk());
        verify(cartService,times(1)).restoreClientCartList(1L);
    }

    @DisplayName("장바구니 조회 테스트 - 회원")
    @Test
    void getClientCartTest() throws Exception {
        mockMvc.perform(get("/api/product/client/cart")
                        .header("X-User-Id", 1))
                .andExpect(status().isOk());
        verify(cartService,times(1)).getClientCart(1L);

    }

    @DisplayName("장바구니 조회 테스트 - 비회원")
    @Test
    void getGuestCartTest() throws Exception {
        List<CartRequestDto> requestDtoList = Arrays.asList(
                CartRequestDto.builder()
                        .productId(1L)
                        .quantity(3L)
                        .build(),
                CartRequestDto.builder()
                        .productId(2L)
                        .quantity(5L)
                        .build()
        );

        when(cartService.getGuestCart(requestDtoList))
                .thenReturn(Arrays.asList(
                        CartGetResponseDto.builder()
                                .build(),
                        CartGetResponseDto.builder()
                                .build()));

        mockMvc.perform(post("/api/product/guest/cart")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(requestDtoList))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));

        verify(cartService,times(1)).getGuestCart(requestDtoList);

    }

    @DisplayName("장바구니 물품 등록 테스트 - 회원")
    @Test
    void addClientCartItemTest() throws Exception {
        CartRequestDto requestDto = CartRequestDto.builder()
                .productId(1L)
                .quantity(3L)
                .build();
        mockMvc.perform(post("/api/product/client/cart/add")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", 1)
                )
                .andExpect(status().isOk());
        verify(cartService,times(1)).addClientCartItem(1L, requestDto);
    }

    @DisplayName("장바구니 물품 등록 테스트 - 비회원")
    @Test
    void addGuestCartItemTest() throws Exception {
        CartRequestDto requestDto = CartRequestDto.builder()
                .productId(1L)
                .quantity(3L)
                .build();
        mockMvc.perform(post("/api/product/guest/cart/add")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        verify(cartService,times(1)).checkCartRequestOfGuest(requestDto);
    }

    @DisplayName("장바구니 물품 갱신 테스트 - 회원")
    @Test
    void updateClientCartItemTest() throws Exception {
        CartRequestDto requestDto = CartRequestDto.builder()
                .productId(1L)
                .quantity(3L)
                .build();
        mockMvc.perform(put("/api/product/client/cart/update")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Id", 1)
                )
                .andExpect(status().isOk());
        verify(cartService,times(1)).setClientCartItemQuantity(1L, requestDto);
    }

    @DisplayName("장바구니 물품 갱신 테스트 - 비회원")
    @Test
    void updateGuestCartItemTest() throws Exception {
        CartRequestDto requestDto = CartRequestDto.builder()
                .productId(1L)
                .quantity(3L)
                .build();
        mockMvc.perform(put("/api/product/guest/cart/update")
                        .with(csrf())
                        .content(objectMapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isOk());
        verify(cartService,times(1)).checkCartRequestOfGuest(requestDto);
    }

    @DisplayName("장바구니 물품 삭제 테스트 - 회원")
    @Test
    void deleteClientCartItemTest() throws Exception {
        mockMvc.perform(delete("/api/product/client/cart/items/1")
                        .with(csrf())
                        .header("X-User-Id", 1)
                )
                .andExpect(status().isOk());
        verify(cartService,times(1)).deleteCartItem(1L,1L);
    }

    @DisplayName("장바구니 물품 초기화 테스트 - 회원")
    @Test
    void clearClientCartItemTest() throws Exception {
        mockMvc.perform(delete("/api/product/client/cart/all")
                        .with(csrf())
                        .header("X-User-Id", 1)
                )
                .andExpect(status().isOk());
        verify(cartService,times(1)).clearAllCart(1L);
    }
}
