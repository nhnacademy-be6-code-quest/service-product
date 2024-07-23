package com.nhnacademy.bookstoreinjun.product.service;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.InventoryDecreaseRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.InventoryIncreaseRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.InventorySetRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductGetNameAndPriceResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductLikeRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductLikeResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductStateUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductUpdateResponseDto;
import com.nhnacademy.bookstoreinjun.entity.Product;
import com.nhnacademy.bookstoreinjun.entity.ProductLike;
import com.nhnacademy.bookstoreinjun.exception.DuplicateException;
import com.nhnacademy.bookstoreinjun.exception.InvalidSortNameException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundIdException;
import com.nhnacademy.bookstoreinjun.exception.PageOutOfRangeException;
import com.nhnacademy.bookstoreinjun.exception.XUserIdNotFoundException;
import com.nhnacademy.bookstoreinjun.repository.ProductLikeRepository;
import com.nhnacademy.bookstoreinjun.repository.ProductRepository;
import com.nhnacademy.bookstoreinjun.repository.QuerydslRepository;
import com.nhnacademy.bookstoreinjun.service.product.ProductServiceImpl;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("상품 서비스 테스트")
class ProductServiceTest {
    @InjectMocks
    private ProductServiceImpl productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductLikeRepository productLikeRepository;

    @Mock
    private QuerydslRepository querydslRepository;

    @Mock
    private ObjectMapper mapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @DisplayName("상품 페이지 조회 성공 테스트")
    @Test
    void getAllProductPageSuccessTest(){
        when(productRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(
                Product.builder().build(),
                Product.builder().build(),
                Product.builder().build()
        )));

        PageRequestDto pageRequestDto = PageRequestDto.builder().build();

        Page<ProductGetResponseDto> responseDtoPage=  productService.findAllPage(pageRequestDto);

        assertNotNull(responseDtoPage);

        assertNotNull(responseDtoPage.getContent());

        assertEquals(3, responseDtoPage.getTotalElements());
    }

    @DisplayName("상품 페이지 조회 실패 테스트 - 초과된 페이지 넘버")
    @Test
    void getAllProductPageFailureTest1(){
        when(productRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(Arrays.asList(
                Product.builder().build(),
                Product.builder().build(),
                Product.builder().build()
        )));

        PageRequestDto pageRequestDto = PageRequestDto.builder().page(10).build();

        assertThrows(PageOutOfRangeException.class, () -> productService.findAllPage(pageRequestDto));
    }

    @DisplayName("상품 페이지 조회 실패 테스트 - 잘못된 정렬 조건의 경우")
    @Test
    void getAllProductPageFailureTest2(){
        when(productRepository.findAll(any(Pageable.class))).thenThrow(PropertyReferenceException.class);

        PageRequestDto pageRequestDto = PageRequestDto.builder().build();

        assertThrows(InvalidSortNameException.class, () -> productService.findAllPage(pageRequestDto));
    }

    @DisplayName("상품 좋아요 등록 성공 테스트")
    @Test
    void saveProductLikeSuccessTest(){
        Product product = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductLikeRequestDto requestDto = ProductLikeRequestDto.builder()
                .productId(1L)
                .build();

        ProductLikeResponseDto responseDto = productService.saveProductLike(1L, requestDto);

        assertNotNull(responseDto);

        verify(productLikeRepository).save(any(ProductLike.class));
        verify(productRepository).findById(1L);
        verify(productLikeRepository).existsByClientIdAndProduct(1L, product);
    }


    @DisplayName("상품 좋아요 등록 실패 테스트 - 요청 헤더에 X-User-Id가 존재하지 않을 경우")
    @Test
    void saveProductLikeFailureTest1(){
        ProductLikeRequestDto requestDto = ProductLikeRequestDto.builder()
                .productId(1L)
                .build();

        assertThrows(XUserIdNotFoundException.class, () -> productService.saveProductLike(-1L, requestDto));
    }

    @DisplayName("상품 좋아요 등록 실패 테스트 - 존재하지 않는 상품에 대한 요청")
    @Test
    void saveProductLikeFailureTest2(){
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        ProductLikeRequestDto requestDto = ProductLikeRequestDto.builder()
                .productId(1L)
                .build();

        assertThrows(NotFoundIdException.class, () -> productService.saveProductLike(1L, requestDto));
    }

    @DisplayName("상품 좋아요 등록 실패 테스트 - 이미 좋아요가 눌러져있을 경우")
    @Test
    void saveProductLikeFailureTest3(){
        Product product = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productLikeRepository.existsByClientIdAndProduct(1L, product)).thenReturn(true);
        ProductLikeRequestDto requestDto = ProductLikeRequestDto.builder()
                .productId(1L)
                .build();

        assertThrows(DuplicateException.class, () -> productService.saveProductLike(1L, requestDto));


        verify(productRepository).findById(1L);
        verify(productLikeRepository).existsByClientIdAndProduct(1L, product);
    }

    @DisplayName("상품 좋아요 취소 성공 테스트")
    @Test
    void deleteProductLikeSuccessTest(){
        Product product = new Product();
        ProductLike productLike = new ProductLike();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productLikeRepository.existsByClientIdAndProduct(1L, product)).thenReturn(true);
        when(productLikeRepository.findByClientIdAndProduct(1L, product)).thenReturn(productLike);

        ProductLikeResponseDto responseDto = productService.deleteProductLike(1L, 1L);

        assertNotNull(responseDto);

        verify(productLikeRepository).delete(productLike);
        verify(productRepository).findById(1L);
        verify(productLikeRepository).findByClientIdAndProduct(1L, product);
    }

    @DisplayName("상품 좋아요 취소 실패 테스트 - 요청 헤더에 X-User-Id가 존재하지 않을 경우")
    @Test
    void deleteProductLikeFailureTest1(){
        assertThrows(XUserIdNotFoundException.class, () -> productService.deleteProductLike(-1L, 1L));
    }


    @DisplayName("상품 좋아요 취소 실패 테스트 - 존재하지 않는 상품에 대한 요청")
    @Test
    void deleteProductLikeFailureTest2(){
        when(productRepository.findById(1L)).thenReturn(Optional.empty());
        assertThrows(NotFoundIdException.class, () -> productService.deleteProductLike(1L, 1L));
    }


    @DisplayName("상품 좋아요 취소 실패 테스트 - 좋아요가 눌러져 있지 않을 경우")
    @Test
    void deleteProductLikeFailureTest3(){
        Product product = new Product();
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productLikeRepository.existsByClientIdAndProduct(1L, product)).thenReturn(false);
        assertThrows(NotFoundIdException.class, () -> productService.deleteProductLike(1L, 1L));
    }

    @DisplayName("상품 상태 업데이트 등록 성공 테스트")
    @Test
    void updateProductStateSuccessTest(){
        ProductStateUpdateRequestDto requestDto = ProductStateUpdateRequestDto.builder()
                .productId(1L)
                .productState(0)
                .build();

        when(querydslRepository.setProductState(1L, 0)).thenReturn(1L);

        ProductUpdateResponseDto responseDto = productService.updateProductState(requestDto);

        assertNotNull(responseDto);

        verify(querydslRepository).setProductState(1L, 0);
    }


    @DisplayName("상품 상태 업데이트 등록 실패 테스트 - 존재하지 않는 상품에 대한 요청")
    @Test
    void updateProductStateFailureTest(){
        ProductStateUpdateRequestDto requestDto = ProductStateUpdateRequestDto.builder()
                .productId(1L)
                .productState(0)
                .build();

        when(querydslRepository.setProductState(1L, 0)).thenReturn(0L);

        assertThrows(NotFoundIdException.class, () -> productService.updateProductState(requestDto));
    }


    @DisplayName("상품 재고 감소 성공 테스트 - 요청 받은 대로 수행됨")
    @Test
    void decreaseProductInventorySuccessTest1() throws Exception{
        Map<Long, Long> decreaseInfo = new LinkedHashMap<>();
        decreaseInfo.put(1L, 1L);
        decreaseInfo.put(2L, 2L);
        InventoryDecreaseRequestDto requestDto = InventoryDecreaseRequestDto.builder()
                .orderId(1L)
                .decreaseInfo(decreaseInfo)
                .build();

        String message = objectMapper.writeValueAsString(requestDto);
        when(mapper.readValue(message, InventoryDecreaseRequestDto.class)).thenReturn(requestDto);
        when(querydslRepository.decreaseProductInventory(requestDto)).thenReturn(2L);

        assertDoesNotThrow(() -> productService.decreaseProductInventoryQueue(message));
    }

    @DisplayName("상품 재고 감소 일부 성공 테스트 - 요청 받은 대로 수행되지는 못함.")
    @Test
    void decreaseProductInventorySuccessTest2() throws Exception{
        Map<Long, Long> decreaseInfo = new LinkedHashMap<>();
        decreaseInfo.put(1L, 1L);
        decreaseInfo.put(2L, 2L);
        InventoryDecreaseRequestDto requestDto = InventoryDecreaseRequestDto.builder()
                .orderId(1L)
                .decreaseInfo(decreaseInfo)
                .build();

        String message = objectMapper.writeValueAsString(requestDto);
        when(mapper.readValue(message, InventoryDecreaseRequestDto.class)).thenReturn(requestDto);
        when(querydslRepository.decreaseProductInventory(requestDto)).thenReturn(1L);

        assertDoesNotThrow(() -> productService.decreaseProductInventoryQueue(message));
    }

    @DisplayName("상품 재고 감소 실패 테스트 - 잘못된 형태의 rabbit mq message")
    @Test
    void decreaseProductInventoryFailureTest1() throws Exception{

        String message = "wrong message";

        when(mapper.readValue(message, InventoryDecreaseRequestDto.class)).thenThrow(JsonProcessingException.class);
        assertDoesNotThrow(() -> productService.decreaseProductInventoryQueue(message));
    }


    @SuppressWarnings("unchecked")
    @DisplayName("상품 재고 증가 성공 테스트 - 요청 받은 대로 수행됨")
    @Test
    void increaseProductInventorySuccessTest1() throws Exception{
        List<InventoryIncreaseRequestDto> requestDtoList = Arrays.asList(
                InventoryIncreaseRequestDto.builder()
                        .productId(1L)
                        .quantityToIncrease(1L)
                        .build(),
                InventoryIncreaseRequestDto.builder()
                        .productId(2L)
                        .quantityToIncrease(2L)
                        .build()
        );

        String message = objectMapper.writeValueAsString(requestDtoList);
        when(mapper.readValue(eq(message), any(TypeReference.class))).thenReturn(requestDtoList);

        when(querydslRepository.increaseProductInventory(requestDtoList)).thenReturn(2L);
        assertDoesNotThrow(() -> productService.increaseProductInventoryQueue(message));
    }


    @SuppressWarnings("unchecked")
    @DisplayName("상품 재고 증가 일부 성공 테스트 - 요청 받은 대로 수행되지는 못함.")
    @Test
    void increaseProductInventorySuccessTest2() throws Exception{
        List<InventoryIncreaseRequestDto> requestDtoList = Arrays.asList(
                InventoryIncreaseRequestDto.builder()
                        .productId(1L)
                        .quantityToIncrease(1L)
                        .build(),
                InventoryIncreaseRequestDto.builder()
                        .productId(2L)
                        .quantityToIncrease(2L)
                        .build()
        );

        String message = objectMapper.writeValueAsString(requestDtoList);
        when(mapper.readValue(eq(message), any(TypeReference.class))).thenReturn(requestDtoList);

        when(querydslRepository.increaseProductInventory(requestDtoList)).thenReturn(1L);
        assertDoesNotThrow(() -> productService.increaseProductInventoryQueue(message));
    }

    @SuppressWarnings("unchecked")
    @DisplayName("상품 재고 증가 실패 테스트 - 잘못된 형태의 rabbit mq message")
    @Test
    void increaseProductInventoryFailureTest1() throws Exception{

        String message = "wrong message";

        when(mapper.readValue(eq(message), any(TypeReference.class))).thenThrow(JsonProcessingException.class);
        assertDoesNotThrow(() -> productService.increaseProductInventoryQueue(message));
    }

    @DisplayName("상품 재고 변경 성공 테스트")
    @Test
    void setProductInventorySuccessTest(){
        InventorySetRequestDto requestDto = InventorySetRequestDto.builder()
                .productId(1L)
                .quantityToSet(30L)
                .build();
        when(querydslRepository.setProductInventory(requestDto)).thenReturn(1L);

        ResponseEntity<Void> responseEntity = productService.setProductInventory(requestDto);

        assertNotNull(responseEntity);
        assertEquals(200, responseEntity.getStatusCode().value());
    }

    @DisplayName("상품 재고 변경 실패 테스트 - 존재하지 않는 상품에 대한 요청")
    @Test
    void setProductInventoryFailureTest(){
        InventorySetRequestDto requestDto = InventorySetRequestDto.builder()
                .productId(1L)
                .quantityToSet(30L)
                .build();
        when(querydslRepository.setProductInventory(requestDto)).thenReturn(0L);

        ResponseEntity<Void> responseEntity = productService.setProductInventory(requestDto);

        assertNotNull(responseEntity);
        assertEquals(404, responseEntity.getStatusCode().value());
    }

    @DisplayName("상품명 및 판매가 조회 성공 테스트")
    @Test
    void getProductNameAndPriceSalesSuccessTest(){
        Product product = Product.builder()
                .productId(1L)
                .productName("test product")
                .productPriceSales(12345)
                .build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductGetNameAndPriceResponseDto responseDto = productService.getSingleProductInfo(1L);

        assertNotNull(responseDto);
        assertEquals(1L, responseDto.productId());
        assertEquals("test product", responseDto.productName());
        assertEquals(12345, responseDto.productPriceSales());

        verify(productRepository, times(1)).findById(1L);
    }

    @DisplayName("상품명 및 판매가 조회 실패 테스트 - 존재하지 않는 상품에 대한 요청")
    @Test
    void getProductNameAndPriceSalesFailureTest(){
        
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundIdException.class, () -> productService.getSingleProductInfo(1L));

        verify(productRepository, times(1)).findById(1L);
    }
}
