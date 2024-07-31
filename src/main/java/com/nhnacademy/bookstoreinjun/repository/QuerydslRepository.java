package com.nhnacademy.bookstoreinjun.repository;

import com.nhnacademy.bookstoreinjun.dto.book.BookProductGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.cart.CartCheckoutRequestDto;
import com.nhnacademy.bookstoreinjun.dto.cart.CartGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.cart.CartRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.InventoryDecreaseRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.InventoryIncreaseRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.InventorySetRequestDto;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface QuerydslRepository {
    BookProductGetResponseDto findBookByProductId(Long clientId, Long productId);

    Page<BookProductGetResponseDto> findAllBookPage(Long clientId, Pageable pageable, Integer productState);

    Page<BookProductGetResponseDto> findNameContainingBookPage(Long clientId, Pageable pageable, String title, Integer productState);
    /**
     * 판매 중인 도서 상품 중 특정 태그를 갖는 도서의 페이지를 반환합니다.
     * @param tags 필터링할 태그명의 set 입니다.
     * @param conditionIsAnd 결과로 나올 도서가 지정된 태그를 모두 가지고 있어야 하는 지 아니면 태그 중 하나라도 가지고 있으면 되는 지를 결정합니다.
     *                       true 면 모두 가지고 있어야 하고, null 이거나 false 라면 하나만 가지고 있어도 됩니다.
     * @param pageable 요청한 페이지. offset 과 limit, 정렬조건, 오름차순/내림차순을 결정합니다.
     * @return 태그명으로 필터링된 도서 상품 페이지
     */
    Page<BookProductGetResponseDto> findBooksByTagFilter(Long clientId, Set<String> tags, Boolean conditionIsAnd, Pageable pageable , Integer productState);


    /**
     *
     * @param categoryId a
     * @param pageable a
     * @return a
     */
    Page<BookProductGetResponseDto> findBooksByCategoryFilter(Long clientId, Long categoryId, Pageable pageable , Integer productState);

    Page<BookProductGetResponseDto> findLikeBooks(Long clientId, Pageable pageable, Integer productState);

    long setProductState(Long productId, Integer productState);

    long decreaseProductInventory(InventoryDecreaseRequestDto inventoryDecreaseRequestDto);

    long increaseProductInventory(List<InventoryIncreaseRequestDto> dtoList);

    long setProductInventory(InventorySetRequestDto dto);

    boolean deleteCartItem(long clientId, long productId);

    boolean deleteAllCart(long clientId);

    long checkOutCart(CartCheckoutRequestDto dto);

    List<CartGetResponseDto> getClientCart(Long clientId);

    List<CartGetResponseDto> getGuestCart(List<CartRequestDto> cartRequestDtoList);

}
