package com.nhnacademy.bookstoreinjun.controller;

import com.nhnacademy.bookstoreinjun.dto.cart.CartGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.cart.CartRequestDto;
import com.nhnacademy.bookstoreinjun.dto.cart.SaveCartResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@Tag(name = "Cart", description = "장바구니 관련 API")
public interface CartControllerInterface {

    @Operation(
            summary = "회원 장바구니 복구",
            description = "Cart - 회원 장바구니 정보를 웹 쿠키에 복구",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 장바구니 정보 (상품 아이디/수량 리스트)를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    )
            }
    )
    @GetMapping("/client/cart/restore")
    ResponseEntity<List<CartRequestDto>> restoreClientCartList(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 조회할 장바구니 데이터를 판단")
            @RequestHeader HttpHeaders httpHeaders);


    @Operation(
            summary = "회원 장바구니에 담긴 상품 리스트 정보 조회",
            description = "Cart - 회원 장바구니에 담긴 상품들의 정보를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "회원 장바구니에 담긴 상품들의 정보 (가격/카테고리/태그 등) 를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "장바구니에 담긴 상품의 아이디에 해당하는 상품 데이터를 조회할 수 없을 경우"
                    )
            }
    )
    @GetMapping("/client/cart")
    ResponseEntity<List<CartGetResponseDto>> getClientCartList(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 조회할 장바구니 데이터를 판단")
            @RequestHeader HttpHeaders httpHeaders);



    @Operation(
            summary = "비회원 장바구니에 담긴 상품 리스트 정보 조회",
            description = "Cart - 비회원 장바구니에 담긴 상품들의 정보를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "비회원 장바구니에 담긴 상품들의 정보 (가격/카테고리/태그 등) 를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "장바구니에 담긴 상품의 아이디에 해당하는 상품 데이터를 조회할 수 없을 경우"
                    )
            }
    )
    @PostMapping("/guest/cart")
    ResponseEntity<List<CartGetResponseDto>> getGuestCartList(
            @Parameter(description = "장바구니 정보 (상품 아이디/수량)의 리스트")
            @RequestBody List<@Valid CartRequestDto> cartRequestDtoList);





    @Operation(
            summary = "회원 장바구니에 특정 상품을 추가",
            description = "Cart - 회원 장바구니에 특정 상품을 특정 갯수만큼 담는 경우",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "장바구니에 추가로 저장한 상품 수량을 반환"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "장바구니에 담으려 한 상품의 아이디에 해당하는 상품 데이터를 조회할 수 없을 경우"
                    )
            }
    )
    @PostMapping("/client/cart/add")
    ResponseEntity<SaveCartResponseDto> addClientCartItem(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 조회할 장바구니 데이터를 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "장바구니에 담으려는 상품의 아이디와 수량")
            @RequestBody @Valid CartRequestDto cartRequestDto);


    @Operation(
            summary = "비회원 장바구니에 특정 상품을 추가",
            description = "Cart - 비회원 장바구니에 특정 상품을 특정 갯수만큼 담는 경우",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "장바구니에 추가로 저장한 상품 수량을 반환"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "장바구니에 담으려 한 상품의 아이디에 해당하는 상품 데이터를 조회할 수 없을 경우"
                    )
            }
    )
    @PostMapping("/guest/cart/add")
    ResponseEntity<SaveCartResponseDto> addGuestCartItem(
            @Parameter(description = "장바구니에 담으려는 상품의 아이디와 수량")
            @RequestBody @Valid CartRequestDto cartRequestDto);




    @Operation(
            summary = "회원 장바구니에 특정 상품의 수량을 변경",
            description = "Cart - 회원 장바구니에 특정 상품을 특정 갯수로 변경",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "변경된 장바구니 상품 수량을 반환"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "수량을 변경하려 한 상품의 아이디에 해당하는 상품 데이터를 조회할 수 없을 경우"
                    )
            }
    )
    @PutMapping("/client/cart/update")
    ResponseEntity<SaveCartResponseDto> updateClientCartItem(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 조회할 장바구니 데이터를 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "장바구니에서 수량을 변경하려는 상품의 아이디와 그 수량")
            @RequestBody @Valid CartRequestDto cartRequestDto
    );


    @Operation(
            summary = "비회원 장바구니에 특정 상품의 수량을 변경",
            description = "Cart - 비회원 장바구니에 특정 상품을 특정 갯수로 변경",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "변경된 장바구니 상품 수량을 반환"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "수량을 변경하려 한 상품의 아이디에 해당하는 상품 데이터를 조회할 수 없을 경우"
                    )
            }
    )
    @PutMapping("/guest/cart/update")
    ResponseEntity<SaveCartResponseDto> updateGuestCartItem(
            @Parameter(description = "장바구니에서 수량을 변경하려는 상품의 아이디와 그 수량")
            @RequestBody @Valid CartRequestDto cartRequestDto);



    @Operation(
            summary = "회원 장바구니에서 특정 상품을 제거",
            description = "Cart - 회원 장바구니에 특정 상품을 제거하기",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "해당 상품을 제거하는 데 성공한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "장바구니에서 제거하려 한 상품의 아이디에 해당하는 상품 데이터를 조회할 수 없을 경우"
                    )
            }
    )
    @DeleteMapping("/client/cart/items/{productId}")
    ResponseEntity<Void> deleteCartItem(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 조회할 장바구니 데이터를 판단")
            @RequestHeader HttpHeaders httpHeaders,
            @Parameter(description = "장바구니에서 제거하려는 상품의 아이디")
            @PathVariable Long productId);


    @Operation(
            summary = "회원 장바구니에서 모든 상품을 제거",
            description = "Cart - 회원 장바구니에 모든 상품을 제거하기",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모든 상품을 제거하는 데 성공한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "장바구니에서 제거하려 한 상품의 아이디에 해당하는 상품 데이터를 조회할 수 없을 경우"
                    )
            }
    )
    @DeleteMapping("/client/cart/all")
    ResponseEntity<Void> clearAllCart(
            @Parameter(description = "요청의 헤더. 헤더에 담긴 User_Id를 통해 초기화 할 장바구니 데이터를 판단")
            @RequestHeader HttpHeaders httpHeaders);

}
