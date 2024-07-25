package com.nhnacademy.bookstoreinjun.service.book;

import com.nhnacademy.bookstoreinjun.dto.book.BookProductGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductRegisterResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductUpdateResponseDto;
import com.nhnacademy.bookstoreinjun.exception.PageOutOfRangeException;
import java.util.Map;
import java.util.Set;
import com.nhnacademy.bookstoreinjun.exception.InvalidSortNameException;
import org.springframework.data.domain.Page;

public interface BookService {

    boolean checkIfBookExists(String isbn);

    /**
     * 도서 등록 요청에 따른, 상품 등록 결과를 반환합니다.
     * @param bookProductRegisterRequestDto 요청입니다. 이 요청은 다음 필드를 포함합니다:
     *                                       - title: 도서 제목
     *                                       - publisher: 출판사
     *                                       - author: 저자
     *                                       - pubDate: 출판일
     *                                       - isbn: ISBN 번호 (10글자)
     *                                       - isbn13: ISBN-13 번호 (13글자)
     *                                       - cover: 표지 이미지 URL
     *                                       - productName 상품명 (2글자 이상)
     *                                       - packable: 포장 가능 여부
     *                                       - productDescription: 상품 설명
     *                                       - productState: 상품 상태 (0이면 정상 판매)
     *                                       - productPriceStandard: 정가  (0 이상)
     *                                       - productPriceSales: 할인 가격 (0 이상)
     *                                       - productInventory: 재고 수량 (0 이상)
     *                                       - categories: 도서 카테고리 목록 (최소 1개 이상)
     *                                       - tags: 도서 태그 목록
     *                                       (categories, tags 를 제외하고는 NotNull)
     *
     * @return 등록 결과. 상품 id와 상품 등록 시간 (현재 시간)을 반환합니다.
     */
    ProductRegisterResponseDto saveBook(BookProductRegisterRequestDto bookProductRegisterRequestDto);

    /**
     * 도서 등록 요청에 따른, 상품 등록 결과를 반환합니다.
     * @param bookProductUpdateRequestDto 요청입니다. 이 요청은 다음 필드를 포함합니다:
     *                                       - productId: 업데이트 할 도서의 id
     *                                       - productName 상품명 (2글자 이상)
     *                                       - packable: 포장 가능 여부
     *                                       - productDescription: 상품 설명
     *                                       - productState: 상품 상태 (0이면 정상 판매)
     *                                       - productPriceSales: 할인 가격 (0 이상)
     *                                       - productInventory: 재고 수량 (0 이상)
     *                                       - categories: 도서 카테고리 목록 (최소 1개 이상)
     *                                       - tags: 도서 태그 목록
     *
     * @return 등록 결과. 상품 업데이트 시간 (현재 시간)을 반환합니다.
     */
    ProductUpdateResponseDto updateBook(BookProductUpdateRequestDto bookProductUpdateRequestDto);

    /**
     * 특정 도서 id에 해당하는 도서의 정보를 반환합니다.
     * @param productId 확인할 도서의 상품 id
     * @return 해당 도서 id에 해당하는 도서의 정보
     */
    BookProductGetResponseDto getBookByProductId(Long clientIdOfHeader, Long productId);

    /**
     * 페이징 요청에 따른 도서의 페이지를 반환합니다.
     * @param pageRequestDto 페이징 요청 (int page (페이지 넘버), int size (페이지 당 사이즈), String sort (정렬할 조건), boolean desc (오름차순/내림차순 여부))
     * @return 해당 요청에 따라 반환되는 도서 페이지
     * @throws PageOutOfRangeException 요청의 page 가, total page 를 초과할 때 발생합니다.
     * @throws InvalidSortNameException 요청의 sort 가, book 을 정렬하기에 부적절한 경우 발생합니다.
     */
    Page<BookProductGetResponseDto> getBookPageByProductState(Long clientIdOfHeader, PageRequestDto pageRequestDto, Integer productState);


    /**
     * 제목에 특정 도서명을 포함하는 도서의 페이지를 반환합니다.
     * @param pageRequestDto 페이징 요청 (int page (페이지 넘버), int size (페이지 당 사이즈), String sort (정렬할 조건), boolean desc (오름차순/내림차순 여부))
     * @param title 포함되어야 할 도서명
     * @return 해당 요청에 따라 반환되는 도서 페이지
     * @throws PageOutOfRangeException 요청의 page 가, total page 를 초과할 때 발생합니다.
     * @throws InvalidSortNameException 요청의 sort 가, book 을 정렬하기에 부적절한 경우 발생합니다.
     */
    Page<BookProductGetResponseDto> getNameContainingBookPageByProductState(Long clientIdOfHeader, PageRequestDto pageRequestDto, String title, Integer productState);


    /**
     * 판매 중인 도서 상품 중 특정 태그를 갖는 도서의 페이지를 반환합니다.
     * @param pageRequestDto 페이징 요청 (int page (페이지 넘버), int size (페이지 당 사이즈), String sort (정렬할 조건), boolean desc (오름차순/내림차순 여부))
     * @param tags 필터링할 태그명의 set 입니다.
     * @param conditionIsAnd 결과로 나올 도서가 지정된 태그를 모두 가지고 있어야 하는 지 아니면 태그 중 하나라도 가지고 있으면 되는 지를 결정합니다.
     *                       true 면 모두 가지고 있어야 하고, null 이거나 false 라면 하나만 가지고 있어도 됩니다.
     * @return 태그명으로 필터링된 도서 상품 페이지
     * @throws PageOutOfRangeException 요청의 page 가, total page 를 초과할 때 발생합니다.
     * @throws InvalidSortNameException 요청의 sort 가, book 을 정렬하기에 부적절한 경우 발생합니다.
     */

    Page<BookProductGetResponseDto> getBookPageFilterByTagsAndProductState(Long clientIdOfHeader, PageRequestDto pageRequestDto, Set<String> tags, Boolean conditionIsAnd, Integer productState);


    /**
     * @param pageRequestDto 페이징 요청 (int page (페이지 넘버), int size (페이지 당 사이즈), String sort (정렬할 조건), boolean desc (오름차순/내림차순 여부))*
     * @param categoryId 필터링할 카테고리 아이디
     * @return 카테고리명으로 필터링된 도서 상품 페이지
     */
    Map<String, Page<BookProductGetResponseDto>> getBookPageFilterByCategoryAndProductState(Long clientIdOfHeader, PageRequestDto pageRequestDto, Long categoryId, Integer productState);


    Page<BookProductGetResponseDto> getLikeBookPage(Long clientIdOfHeader, PageRequestDto pageRequestDto, Integer productState);

}