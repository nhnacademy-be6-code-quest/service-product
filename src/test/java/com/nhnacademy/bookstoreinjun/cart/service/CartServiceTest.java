package com.nhnacademy.bookstoreinjun.cart.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstoreinjun.dto.cart.CartCheckoutRequestDto;
import com.nhnacademy.bookstoreinjun.dto.cart.CartGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.cart.CartRequestDto;
import com.nhnacademy.bookstoreinjun.dto.cart.SaveCartResponseDto;
import com.nhnacademy.bookstoreinjun.entity.Cart;
import com.nhnacademy.bookstoreinjun.entity.Product;
import com.nhnacademy.bookstoreinjun.exception.NotFoundIdException;
import com.nhnacademy.bookstoreinjun.exception.XUserIdNotFoundException;
import com.nhnacademy.bookstoreinjun.repository.CartRepository;
import com.nhnacademy.bookstoreinjun.repository.ProductRepository;
import com.nhnacademy.bookstoreinjun.repository.QuerydslRepository;
import com.nhnacademy.bookstoreinjun.service.cart.CartServiceImpl;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("장바구니 서비스 테스트")
class CartServiceTest {
    @InjectMocks
    private CartServiceImpl cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private QuerydslRepository querydslRepository;

    @Mock
    private ObjectMapper mapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("장바구니 복구 성공 테스트")
    @Test
    void restoreSuccessTest(){
        Product product1 = Product.builder().productId(1L).build();
        Product product2 = Product.builder().productId(2L).build();

        when(cartRepository.findAllByClientIdAndCartRemoveTypeIsNull(1L))
                .thenReturn(Arrays.asList(
                        Cart.builder().product(product1).quantity(3L).build(),
                        Cart.builder().product(product1).quantity(2L).build(),
                        Cart.builder().product(product2).quantity(4L).build()
                ));
        List<CartRequestDto> restoredList  = cartService.restoreClientCartList(1L);
        assertNotNull(restoredList);
        assertEquals(2, restoredList.size());
        assertEquals(5, restoredList.getFirst().quantity());
        assertEquals(4, restoredList.getLast().quantity());

        verify(cartRepository, times(1)).findAllByClientIdAndCartRemoveTypeIsNull(1L);
    }

    @DisplayName("장바구니 복구 실패 테스트 - 존재하지 않는 유저")
    @Test
    void restoreFailureTest(){
        assertThrows(XUserIdNotFoundException.class, () -> cartService.restoreClientCartList(-1L));
    }

    @DisplayName("장바구니 추가 성공 테스트 - 회원")
    @Test
    void addSuccessTest(){
        CartRequestDto cartRequestDto = CartRequestDto.builder()
                .productId(1L)
                .quantity(5L)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(
                        Product.builder()
                                .productId(1L)
                                .productInventory(7L)
                                .build()));

        Product product1 = Product.builder().productId(1L).productInventory(7L).build();

        when(cartRepository.findByClientIdAndProduct_ProductIdAndCartRemoveTypeIsNull(1L, 1L))
                .thenReturn(Arrays.asList(
                        Cart.builder().product(product1).quantity(3L).build(),
                        Cart.builder().product(product1).quantity(2L).build()
                ));
        SaveCartResponseDto responseDto = cartService.addClientCartItem(1L, cartRequestDto);

        assertNotNull(responseDto);
        assertEquals(2, responseDto.savedCartQuantity());

        verify(productRepository, times(1)).findById(1L);

        verify(cartRepository, times(1)).findByClientIdAndProduct_ProductIdAndCartRemoveTypeIsNull(1L, 1L);

        verify(cartRepository, times(1)).save(any());
    }

    @DisplayName("장바구니 추가 실패 테스트 - 회원 : 존재하지 않는 유저")
    @Test
    void addFailureTest(){
        CartRequestDto cartRequestDto = CartRequestDto.builder()
                .productId(1L)
                .quantity(5L)
                .build();
        assertThrows(XUserIdNotFoundException.class, () -> cartService.addClientCartItem(-1L, cartRequestDto));
    }

    @DisplayName("장바구니 추가 실패 테스트 - 회원 : 존재하지 않는 상품")
    @Test
    void addFailureTest2(){
        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        CartRequestDto cartRequestDto = CartRequestDto.builder()
                .productId(1L)
                .quantity(5L)
                .build();
        assertThrows(NotFoundIdException.class, () -> cartService.addClientCartItem(1L, cartRequestDto));
    }

    @DisplayName("장바구니 추가 성공 테스트 - 비회원")
    @Test
    void addSuccessTest2(){
        when(productRepository.findById(1L))
                .thenReturn(Optional.of(
                        Product.builder()
                                .productId(1L)
                                .productInventory(7L)
                                .build()));

        CartRequestDto cartRequestDto = CartRequestDto.builder()
                .productId(1L)
                .quantity(10L)
                .build();

        SaveCartResponseDto responseDto = cartService.checkCartRequestOfGuest(cartRequestDto);
        assertNotNull(responseDto);
        assertEquals(7, responseDto.savedCartQuantity());
    }

    @DisplayName("장바구니 추가 실패 테스트 - 비회원 : 존재하지 않는 상품")
    @Test
    void addFailureTest3(){
        when(productRepository.findById(1L))
                .thenReturn(Optional.empty());

        CartRequestDto cartRequestDto = CartRequestDto.builder()
                .productId(1L)
                .quantity(10L)
                .build();

        assertThrows(NotFoundIdException.class, () -> cartService.checkCartRequestOfGuest(cartRequestDto));
    }

    @DisplayName("장바구니 수량 변경 성공 테스트 - 회원")
    @Test
    void updateSuccessTest(){
        CartRequestDto cartRequestDto = CartRequestDto.builder()
                .productId(1L)
                .quantity(1L)
                .build();

        when(productRepository.findById(1L))
                .thenReturn(Optional.of(
                        Product.builder()
                                .productId(1L)
                                .productInventory(7L)
                                .build()));

        Product product1 = Product.builder().productId(1L).productInventory(7L).build();

        when(cartRepository.findByClientIdAndProduct_ProductIdAndCartRemoveTypeIsNull(1L, 1L))
                .thenReturn(Arrays.asList(
                        Cart.builder().product(product1).quantity(3L).build(),
                        Cart.builder().product(product1).quantity(2L).build()
                ));
        SaveCartResponseDto responseDto = cartService.setClientCartItemQuantity(1L, cartRequestDto);

        assertNotNull(responseDto);
        assertEquals(-4, responseDto.savedCartQuantity());

        verify(productRepository, times(1)).findById(1L);

        verify(cartRepository, times(1)).findByClientIdAndProduct_ProductIdAndCartRemoveTypeIsNull(1L, 1L);

        verify(cartRepository, times(1)).save(any());
    }

    @DisplayName("장바구니 물품 삭제 성공 테스트 - 회원")
    @Test
    void deleteSuccessTest(){
        when(querydslRepository.deleteCartItem(1L, 3L)).thenReturn(true);
        assertDoesNotThrow(() -> cartService.deleteCartItem(1L, 3L));
    }

    @DisplayName("장바구니 물품 삭제 실패 테스트 - 회원")
    @Test
    void deleteFailureTest() {
        assertThrows(XUserIdNotFoundException.class, () -> cartService.deleteCartItem(-1L, 3L));
    }

    @DisplayName("장바구니 초기화 성공 테스트 - 회원")
    @Test
    void clearSuccessTest() {
        when(querydslRepository.deleteAllCart(1L)).thenReturn(true);
        assertDoesNotThrow(() -> cartService.clearAllCart(1L));

    }

    @DisplayName("장바구니 초기화 성공 테스트 - 회원")
    @Test
    void clearFailureTest() {
        assertThrows(XUserIdNotFoundException.class, () -> cartService.clearAllCart(-1L));
    }

    @DisplayName("장바구니 특정 물품들 비우기 성공 테스트 - 회원")
    @Test
    void checkoutSuccessTest() throws JsonProcessingException {
        CartCheckoutRequestDto requestDto = CartCheckoutRequestDto.builder()
                .clientId(1L)
                .productIdList(Arrays.asList(1L, 2L, 3L, 4L, 5L))
                .build();

        String message = objectMapper.writeValueAsString(requestDto);
        when(mapper.readValue(message, CartCheckoutRequestDto.class)).thenReturn(requestDto);
        assertDoesNotThrow(() -> cartService.checkOutCart(message));
    }

    @DisplayName("장바구니 특정 물품들 비우기 실패 테스트 - 회원")
    @Test
    void checkoutFailureTest() throws JsonProcessingException {
        String message = objectMapper.writeValueAsString("wrong");

        when(mapper.readValue(message, CartCheckoutRequestDto.class)).thenThrow(JsonProcessingException.class);

        assertDoesNotThrow(() -> cartService.checkOutCart(message));
    }

    @DisplayName("장바구니 조회 성공 테스트 - 회원")
    @Test
    void getClientCartSuccessTest(){
        when(querydslRepository.getClientCart(1L))
                .thenReturn(Arrays.asList(
                        CartGetResponseDto.builder().build(),
                        CartGetResponseDto.builder().build()
                ));

        List<CartGetResponseDto> cartGetResponseDtoList = cartService.getClientCart(1L);
        assertNotNull(cartGetResponseDtoList);
        assertEquals(2, cartGetResponseDtoList.size());
    }

    @DisplayName("장바구니 조회 실패 테스트 - 회원 : 존재하지 않는 회원 아이디")
    @Test
    void getClientCartFailureTest(){
        assertThrows(XUserIdNotFoundException.class,() -> cartService.getClientCart(-1L));
    }

    @DisplayName("장바구니 조회 성공 테스트 - 비회원")
    @Test
    void getGuestCartSuccessTest(){
        List<CartRequestDto> cartRequestDtoList = Arrays.asList(
                CartRequestDto.builder()
                        .productId(1L)
                        .quantity(2L)
                        .build(),
                CartRequestDto.builder()
                        .productId(3L)
                        .quantity(4L)
                        .build()
        );

        when(querydslRepository.getGuestCart(cartRequestDtoList))
                .thenReturn(Arrays.asList(
                        CartGetResponseDto.builder().build(),
                        CartGetResponseDto.builder().build()
                ));

        List<CartGetResponseDto> cartGetResponseDtoList = cartService.getGuestCart(cartRequestDtoList);

        assertNotNull(cartGetResponseDtoList);
        assertEquals(2, cartGetResponseDtoList.size());
    }
}
