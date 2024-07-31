package com.nhnacademy.bookstoreinjun.controller;

import com.nhnacademy.bookstoreinjun.dto.book.BookProductGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.book.aladin.AladinBookResponseDto;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductRegisterResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductUpdateResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.Map;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Book", description = "도서 관련 API")
public interface BookControllerInterface {

    @Operation(
            summary = "관리자 권한 확인",
            description = "Product - 도서 등록용 페이지 등에 접근 시 권한을 확인",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "관리자 권한일 경우 반환"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "관리자 권한이 아닐 경우 반환 (필터에 의해 동작)"
                    )
            }
    )
    @GetMapping("/admin/book/roleCheck")
    ResponseEntity<Void> roleCheck();


    @Operation(
            summary = "알라딘 API를 통한 도서 목록 조회",
            description = "Product - 도서 등록 과정에 사용할 도서 조회 - 알라딘 API 호출을 통한",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "관리자 권한일 경우 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation 에 걸릴 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description =  "알라딘 API 호출에 필요한 Key가 일치하지 않을 경우"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "관리자 권한이 아닐 경우 반환 (필터에 의해 동작)"
                    )
            }
    )
    @GetMapping("/admin/book")
    ResponseEntity<Page<AladinBookResponseDto>> getBooksForAdmin(
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "알라딘 API 를 통한 도서 검색 시 사용할 검색어로서의 제목")
            @RequestParam("title") String title);



    @Operation(
            summary = "도서 ISBN 10의 중복 확인",
            description = "Product - 도서 등록 과정에서 1차적인 ISBN 10 중복 확인",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "데이터베이스 상 해당 ISBN 의 도서가 존재하지 않을 경우"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation 에 걸릴 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없는 사용자가 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "데이터베이스 상 해당 ISBN 의 도서가 존재할 경우"
                    )
            }
    )
    @GetMapping("/admin/book/isbnCheck")
    ResponseEntity<Void> checkIfBookExistsForAdmin(
            @Parameter(description = "중복 여부를 확인할 ISBN")
            @RequestParam("isbn") String isbn);



    @Operation(
            summary = "도서 상품 등록",
            description = "Product - 도서 상품을 쇼핑몰에 등록",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "상품 등록 시간 및 상품의 아이디 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation 에 걸릴 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없는 사용자가 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "ISBN 10 중복시 반환"
                    )
            }
    )
    @PostMapping("/admin/book/register")
    ResponseEntity<ProductRegisterResponseDto> saveBookProduct(
            @Parameter(description = "상품 등록을 시도한 도서 상품의 정보")
            @Valid @RequestBody BookProductRegisterRequestDto bookProductRegisterRequestDto);


    @Operation(
            summary = "도서 상품 정보 수정",
            description = "Product - 도서 상품의 정보를 수정",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "상품 등록 시간 및 상품의 아이디 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation 에 걸릴 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없는 사용자가 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "수정 요청에 포함된 상품/태그/카테고리 조회 실패시 반환"
                    )
            }
    )
    @PutMapping("/admin/book/update")
    ResponseEntity<ProductUpdateResponseDto> updateBook(
            @Parameter(description = "상품 수정에 필요한 도서 상품의 정보")
            @Valid @RequestBody BookProductUpdateRequestDto bookProductUpdateRequestDto);




    @Operation(
            summary = "도서 상품 개별 조회 - 사용자용",
            description = "Product - 하나의 도서 상품의 정보를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "도서 상품의 정보를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "데이터베이스상 존재하지 않는 도서 상품의 조회를 요청했을 경우"
                    )
            }
    )
    @GetMapping("/book/{productId}")
    ResponseEntity<BookProductGetResponseDto> getSingleBookInfo(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 상품들에 대한 좋아요 여부를 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "조회에 사용할 도서 상품의 아이디")
            @Min(1) @PathVariable long productId);


    @Operation(
            summary = "도서 상품 개별 조회 - 관리자용 (기능 상의 차이는 없습니다.)",
            description = "Product - 하나의 도서 상품의 정보를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "도서 상품의 정보를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없는 사용자가 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "데이터베이스상 존재하지 않는 도서 상품의 조회를 요청했을 경우"
                    )
            }
    )
    @GetMapping("/admin/book/{productId}")
    ResponseEntity<BookProductGetResponseDto> getSingleBookInfoForAdmin(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 X-User-Id와 X-User-Role 을 통해 상품들에 대한 좋아요 여부 및 권한을 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "조회에 사용할 도서 상품의 아이디")
            @Min(1) @PathVariable long productId);



    @Operation(
            summary = "도서 상품 페이지 조회 - 사용자용",
            description = "Product - 도서 상품의 페이지를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "도서 상품의 페이지를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "최대 페이지보다 많은 페이지를 요청하거나 잘못된 정렬 조건을 지정했을 경우"
                    )
            }
    )
    @GetMapping("/books")
    ResponseEntity<Page<BookProductGetResponseDto>> getAllBookPage(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 상품들에 대한 좋아요 여부를 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "조회할 상품의 상태. Null : 모든 상품. 0 : 판매 중. 1 : 임시 판매 중지 등. 사용자는 기본적으로 판매 중 상품만 조회 가능")
            @RequestParam(name = "productState", required = false) Integer productState);



    @Operation(
            summary = "도서 상품 페이지 조회 - 관리자용 (기능 상의 차이는 없습니다.)",
            description = "Product - 도서 상품의 페이지를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "도서 상품의 페이지를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "최대 페이지보다 많은 페이지를 요청하거나 잘못된 정렬 조건을 지정했을 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없는 사용자가 접근한 경우"
                    )
            }
    )
    @GetMapping("/admin/books")
    ResponseEntity<Page<BookProductGetResponseDto>> getAllBookPageForAdmin(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 X-User-Id와 X-User-Role 을 통해 상품들에 대한 좋아요 여부 및 권한을 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "조회할 상품의 상태. Null : 모든 상품. 0 : 판매 중. 1 : 임시 판매 중지 등.")
            @RequestParam(name = "productState", required = false) Integer productState);



    @Operation(
            summary = "제목에 특정 문자열이 포함된 도서 상품 페이지 조회 - 사용자용",
            description = "Product - 제목에 특정 문자열이 포함된 도서 상품의 페이지를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "제목에 특정 문자열이 포함된 도서 상품의 페이지를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "최대 페이지보다 많은 페이지를 요청하거나 잘못된 정렬 조건을 지정했을 경우"
                    )
            }
    )
    @GetMapping("/books/containing")
    ResponseEntity<Page<BookProductGetResponseDto>> getNameContainingBookPage(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 상품들에 대한 좋아요 여부를 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "조회할 상품의 상태. Null : 모든 상품. 0 : 판매 중. 1 : 임시 판매 중지 등. 사용자는 기본적으로 판매 중 상품만 조회 가능")
            @RequestParam(name = "productState", required = false) Integer productState,
            @Parameter(description = "제목에 포함돼 있는지의 여부를 판단할 검색조건으로서의 문자열")
            @RequestParam("title") String title);



    @Operation(
            summary = "제목에 특정 문자열이 포함된 도서 상품 페이지 조회 - 관리자용 (기능 상의 차이는 없습니다.)",
            description = "Product - 제목에 특정 문자열이 포함된 도서 상품의 페이지를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "제목에 특정 문자열이 포함된 도서 상품의 페이지를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "최대 페이지보다 많은 페이지를 요청하거나 잘못된 정렬 조건을 지정했을 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없는 사용자가 접근한 경우"
                    )
            }
    )
    @GetMapping("/admin/books/containing")
    ResponseEntity<Page<BookProductGetResponseDto>> getNameContainingBookPageForAdmin(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 X-User-Id와 X-User-Role 을 통해 상품들에 대한 좋아요 여부 및 권한을 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "조회할 상품의 상태. Null : 모든 상품. 0 : 판매 중. 1 : 임시 판매 중지 등.")
            @RequestParam(name = "productState", required = false) Integer productState,
            @Parameter(description = "제목에 포함돼 있는지의 여부를 판단할 검색조건으로서의 문자열")
            @RequestParam("title") String title);



    @Operation(
            summary = "특정 카테고리에 속한 도서 상품 페이지 조회 - 사용자용",
            description = "Product - 특정 카테고리에 속한 도서 상품의 페이지를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "특정 카테고리에 속한 도서 상품의 페이지를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "최대 페이지보다 많은 페이지를 요청하거나 잘못된 정렬 조건을 지정했을 경우"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 카테고리에 대한 요청의 경우"
                    )
            }
    )
    @GetMapping("/books/category/{categoryId}")
    ResponseEntity<Map<String, Page<BookProductGetResponseDto>>> getBookPageFilterByCategory(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 상품들에 대한 좋아요 여부를 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "조회할 상품의 상태. Null : 모든 상품. 0 : 판매 중. 1 : 임시 판매 중지 등. 사용자는 기본적으로 판매 중 상품만 조회 가능")
            @RequestParam(name = "productState", required = false) Integer productState,
            @Parameter(description = "조회할 카테고리의 아이디")
            @PathVariable("categoryId") Long categoryId);


    @Operation(
            summary = "특정 카테고리에 속한 도서 상품 페이지 조회 - 관리자용 (기능 상의 차이는 없습니다.)",
            description = "Product - 특정 카테고리에 속한 도서 상품의 페이지를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "특정 카테고리에 속한 도서 상품의 페이지를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "최대 페이지보다 많은 페이지를 요청하거나 잘못된 정렬 조건을 지정했을 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없는 사용자가 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 카테고리에 대한 요청의 경우"
                    )
            }
    )
    @GetMapping("/admin/books/category/{categoryId}")
    ResponseEntity<Map<String, Page<BookProductGetResponseDto>>> getBookPageFilterByCategoryForAdmin(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 X-User-Id와 X-User-Role 을 통해 상품들에 대한 좋아요 여부 및 권한을 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "조회할 상품의 상태. Null : 모든 상품. 0 : 판매 중. 1 : 임시 판매 중지 등.")
            @RequestParam(name = "productState", required = false) Integer productState,
            @Parameter(description = "조회할 카테고리의 아이디")
            @PathVariable("categoryId") Long categoryId);



    @Operation(
            summary = "특정 태그들로 필터링한 도서 상품 페이지 조회 - 사용자용",
            description = "Product - 특정 태그들로 필터링한 도서 상품의 페이지를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "특정 태그들로 필터링한 도서 상품의 페이지를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "최대 페이지보다 많은 페이지를 요청하거나 잘못된 정렬 조건을 지정했을 경우"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 태그에 대한 요청의 경우"
                    )
            }
    )
    @GetMapping("/books/tagFilter")
    ResponseEntity<Page<BookProductGetResponseDto>> getBookPageFilterByTag(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 상품들에 대한 좋아요 여부를 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "필터링할 태그의 이름들")
            @RequestParam("tagName") Set<String> tagNameSet,
            @Parameter(description = "조회할 상품의 상태. Null : 모든 상품. 0 : 판매 중. 1 : 임시 판매 중지 등. 사용자는 기본적으로 판매 중 상품만 조회 가능")
            @RequestParam(name = "productState", required = false) Integer productState,
            @Parameter(description = "필터링의 조건 중 And/Or의 여부. true 라면 모든 태그를 달고 있어야 하고, false 라면 태그 중 하나라도 달고 있으면 됨")
            @RequestParam(value = "isAnd", required = false, defaultValue = "true") Boolean conditionIsAnd);


    @Operation(
            summary = "특정 태그들로 필터링한 도서 상품 페이지 조회 - 관리자용 (기능 상의 차이는 없습니다.)",
            description = "Product - 특정 태그들로 필터링한 도서 상품의 페이지를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "특정 태그들로 필터링한 도서 상품의 페이지를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "최대 페이지보다 많은 페이지를 요청하거나 잘못된 정렬 조건을 지정했을 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없는 사용자가 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "존재하지 않는 태그에 대한 요청의 경우"
                    )
            }
    )
    @GetMapping("/admin/books/tagFilter")
    ResponseEntity<Page<BookProductGetResponseDto>> getBookPageFilterByTagForAdmin(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 X-User-Id와 X-User-Role 을 통해 상품들에 대한 좋아요 여부 및 권한을 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "필터링할 태그의 이름들")
            @RequestParam("tagName") Set<String> tagNameSet,
            @Parameter(description = "조회할 상품의 상태. Null : 모든 상품. 0 : 판매 중. 1 : 임시 판매 중지 등.")
            @RequestParam(name = "productState", required = false) Integer productState,
            @Parameter(description = "필터링의 조건 중 And/Or의 여부. true 라면 모든 태그를 달고 있어야 하고, false 라면 태그 중 하나라도 달고 있으면 됨")
            @RequestParam(value = "isAnd", required = false, defaultValue = "true") Boolean conditionIsAnd);



    @Operation(
            summary = "자신이 좋아요를 눌러 놓은 도서 상품 페이지 조회 - 회원용",
            description = "Product - 자신이 좋아요를 눌러 놓은 도서 상품의 페이지를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "자신이 좋아요를 눌러 놓은 도서 상품의 페이지를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "최대 페이지보다 많은 페이지를 요청하거나 잘못된 정렬 조건을 지정했을 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    )
            }
    )
    @GetMapping("/client/books/like")
    ResponseEntity<Page<BookProductGetResponseDto>> getLikeBookPage(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 상품들에 대한 좋아요 여부를 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "조회할 상품의 상태. Null : 모든 상품. 0 : 판매 중. 1 : 임시 판매 중지 등. 사용자는 기본적으로 판매 중 상품만 조회 가능")
            @RequestParam(name = "productState", required = false) Integer productState);


    @Operation(
            summary = "자신이 좋아요를 눌러 놓은 도서 상품 페이지 조회 - 관리자용 (기능 상의 차이는 없습니다.)",
            description = "Product - 자신이 좋아요를 눌러 놓은 도서 상품의 페이지를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "자신이 좋아요를 눌러 놓은 도서 상품의 페이지를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "최대 페이지보다 많은 페이지를 요청하거나 잘못된 정렬 조건을 지정했을 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "권한 없는 회원이 접근하였을 경우"
                    )
            }
    )
    @GetMapping("/admin/books/like")
    ResponseEntity<Page<BookProductGetResponseDto>> getLikeBookPageForAdmin(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 X-User-Id와 X-User-Role 을 통해 상품들에 대한 좋아요 여부 및 권한을 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "조회할 상품의 상태. Null : 모든 상품. 0 : 판매 중. 1 : 임시 판매 중지 등.")
            @RequestParam(name = "productState", required = false) Integer productState);

}
