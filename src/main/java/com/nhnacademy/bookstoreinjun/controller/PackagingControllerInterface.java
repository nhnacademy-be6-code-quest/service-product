package com.nhnacademy.bookstoreinjun.controller;

import com.nhnacademy.bookstoreinjun.dto.packaging.PackagingGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.packaging.PackagingRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.packaging.PackagingUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductRegisterResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductUpdateResponseDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Packaging", description = "포장지 관련 API")
public interface PackagingControllerInterface {

    @Operation(
            summary = "관리자 권한 확인",
            description = "Packaging - 포장지 등록용 페이지 등에 접근 시 권한을 확인",
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
    @GetMapping("/admin/packaging/roleCheck")
    ResponseEntity<Void> roleCheck();


    @Operation(
            summary = "특정 포장지 정보 조회",
            description = "Packaging - 데이터베이스에서 포장지 정보 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "등록된 카테고리명과 그 상위 카테고리명을 반환"
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
    @GetMapping("/admin/packaging/single/byProduct/{productId}")
    ResponseEntity<PackagingGetResponseDto> getPackageInfoByProductId(
            @Parameter(description = "조회할 포장지 상품의 아이디")
            @PathVariable("productId") Long productId);


    @Operation(
            summary = "새로운 포장지 상품 등록",
            description = "Packaging - 데이터베이스에 새로운 포장지 상품 등록",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "등록된 시간과 상품의 아이디를 반환"
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
                    )
            }
    )
    @PostMapping("/admin/packaging/register")
    ResponseEntity<ProductRegisterResponseDto> insertPackage(
            @Parameter(description = "등록할 포장지 상품의 정보")
            @Valid @RequestBody PackagingRegisterRequestDto req);



    @Operation(
            summary = "포장지 상품 정보 수정",
            description = "Packaging - 데이터베이스의 포장지 정보 수정",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "수정이 이루어진 시간을 반환"
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
                    ),@ApiResponse(
                            responseCode = "404",
                            description = "해당 아이디의 포장지 상품이 조회되지 않을 경우"
                    )
            }
    )
    @PutMapping("/admin/packaging/update")
    ResponseEntity<ProductUpdateResponseDto> updatePackage(
            @Parameter(description = "변경할 포장지 상품의 id와 변경할 정보")
            @Valid @RequestBody PackagingUpdateRequestDto req);


    @Operation(
            summary = "모든 포장지 상품 조회",
            description = "Packaging - 모든 포장지 상품 데이터를 반환",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모든 포장지 상품을 반환"
                    )
            }
    )
    @GetMapping("/packaging/all")
    ResponseEntity<List<PackagingGetResponseDto>> getAllPackage(
            @Parameter(description = "조회할 상품의 상태. Null : 모든 상품. 0 : 판매 중. 1 : 임시 판매 중지 등. 사용자는 기본적으로 판매 중 상품만 조회 가능")
            @RequestParam(name = "productState", required = false) Integer productState);



    @Operation(
            summary = "모든 포장지 페이지 조회 - 사용자용",
            description = "Packaging - 모든 포장지 상품의 페이지를 반환",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모든 포장지 상품의 페이지를 반환"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "최대 페이지보다 많은 페이지를 요청하거나 잘못된 정렬 조건을 지정했을 경우"
                    )
            }
    )
    @GetMapping("/packaging/page")
    ResponseEntity<Page<PackagingGetResponseDto>> getPackagePage(
            @Parameter(description = "조회할 상품의 상태. Null : 모든 상품. 0 : 판매 중. 1 : 임시 판매 중지 등. 사용자는 기본적으로 판매 중 상품만 조회 가능")
            @RequestParam(name = "productState", required = false) Integer productState,
            @Parameter(description = "조회할 페이지")
            @Min(1) @RequestParam(name = "page") int page,
            @Parameter(description = "조회할 페이지의, 페이지 당 사이즈")
            @RequestParam(name = "size") int size);


    @Operation(
            summary = "모든 포장지 페이지 조회 - 관리자용 (기능상 차이는 없습니다.)",
            description = "Packaging - 모든 포장지 상품의 페이지를 반환",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모든 포장지 상품의 페이지를 반환"
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
    @GetMapping("/admin/packaging/page")
    ResponseEntity<Page<PackagingGetResponseDto>> getPackagePageForAdmin(
            @Parameter(description = "조회할 상품의 상태. Null : 모든 상품. 0 : 판매 중. 1 : 임시 판매 중지 등.")
            @RequestParam(name = "productState", required = false) Integer productState,
            @Parameter(description = "조회할 페이지")
            @Min(1) @RequestParam(name = "page") int page,
            @Parameter(description = "조회할 페이지의, 페이지 당 사이즈")
            @RequestParam(name = "size") int size);
}
