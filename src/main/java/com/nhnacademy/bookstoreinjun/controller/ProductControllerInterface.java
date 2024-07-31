package com.nhnacademy.bookstoreinjun.controller;

import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.InventorySetRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductGetNameAndPriceResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductLikeRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductLikeResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductStateUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductUpdateResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Product", description = "상품 전반 관련 API")
public interface ProductControllerInterface {

    @Operation(
            summary = "관리자 권한 확인",
            description = "Product - 포장지 등록용 페이지 등에 접근 시 권한을 확인",
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
    @GetMapping("/admin")
    ResponseEntity<Void> checkIfAdmin();

    @Operation(
            summary = "상품명과 판매가만을 조회)",
            description = "Product - 특정 아이디의 상품의 이름과 판매가를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "특정 아이디의 상품의 이름과 판매가를 조회"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 아이디에 해당하는 상품이 조회되지 않는 경우"
                    )
            }
    )
    @GetMapping("/single/{productId}")
    ResponseEntity<ProductGetNameAndPriceResponseDto> getSingleProductNameAndPriceSales(
            @Parameter(description = "조회할 상품의 아이디")
            @PathVariable("productId") Long productId);

    @Operation(
            summary = "상품 정보의 페이지 조회 - 쿠폰 등록시 활용)",
            description = "Product - 상품의, 쿠폰 등록시 필요한 정보들을 페이징 형태로 반환",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "상품의, 쿠폰 등록시 필요한 정보들을 페이징 형태로 반환"
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
    @GetMapping("/admin/page/all")
    ResponseEntity<Page<ProductGetResponseDto>> getAllProducts(
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto);


    @Operation(
            summary = "상품의 상태 변경",
            description = "Product - 상품의 상태 (판매 중, 임시 판매 중지 등) 변경",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "수정 시간을 반환"
                    )
                    ,
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
                    )
            }
    )
    @PutMapping("/admin/update/state")
    ResponseEntity<ProductUpdateResponseDto> updateState(
            @Parameter(description = "상태를 변경할 상품의 아이디와 상태가 담긴 dto")
            @RequestBody @Valid ProductStateUpdateRequestDto productStateUpdateRequestDto);


    @Operation(
            summary = "상품에 대한 좋아요",
            description = "Product - 특정 상품에 대한 특정 회원이 좋아요를 남김",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "좋아요 데이터를 남기는 데 성공한 경우"
                    )
                    ,
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation 에 걸릴 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    )
            }
    )
    @PostMapping("/client/like")
    ResponseEntity<ProductLikeResponseDto> saveBookProductLike(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 회원 아이디를 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "상품의 아이디가 담긴 dto")
            @RequestBody @Valid ProductLikeRequestDto productLikeRequestDto);



    @Operation(
            summary = "상품에 대한 좋아요 취소",
            description = "Product - 특정 상품에 대한 특정 회원이 좋아요를 취소함",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "좋아요를 취소하는데 성공한 경우"
                    )
                    ,
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation 에 걸릴 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 상품에 대한 해당 회원의 좋아요 기록이 조회되지 않을 경우"
                    )
            }
    )
    @DeleteMapping("/client/unlike")
    ResponseEntity<ProductLikeResponseDto> deleteBookProductLike(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 회원 아이디를 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "좋아요를 취소할 상품 아이디")
            @RequestParam("productId") Long productId);


    @Operation(
            summary = "상품의 재고 변경",
            description = "Product - 특정 상품에 대한 재고만 특정 수량으로 변경",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "좋아요를 취소하는데 성공한 경우"
                    )
                    ,
                    @ApiResponse(
                            responseCode = "400",
                            description = "Validation 에 걸릴 경우"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 상품에 대한 업데이트가 이루어지지 않은 경우"
                    )
            }
    )
    @PutMapping("/admin/inventory/set")
    ResponseEntity<Void> setProductInventory(
            @Parameter(description = "재고를 변경한 상품의 아이디와 변경할 재고의 수량이 담긴 dto")
            @RequestBody @Valid InventorySetRequestDto inventorySetRequestDto);
}
