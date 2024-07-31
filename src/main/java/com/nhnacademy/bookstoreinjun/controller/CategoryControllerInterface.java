package com.nhnacademy.bookstoreinjun.controller;


import com.nhnacademy.bookstoreinjun.dto.category.CategoryGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.category.CategoryNodeResponseDto;
import com.nhnacademy.bookstoreinjun.dto.category.CategoryRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.category.CategoryRegisterResponseDto;
import com.nhnacademy.bookstoreinjun.dto.category.CategoryUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.category.CategoryUpdateResponseDto;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

@Tag(name = "Category", description = "카테고리 관련 API")
public interface CategoryControllerInterface {
    @Operation(
            summary = "신규 카테고리 등록",
            description = "Category - 데이터베이스에 새로운 카테고리 데이터를 등록",
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "등록된 카테고리명과 그 상위 카테고리명을 반환"
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
    @PostMapping("/admin/category/register")
    ResponseEntity<CategoryRegisterResponseDto> createCategory(
            @Parameter(description = "등록할 카테고리명과, 그 상위 카테고리명")
            @Valid @RequestBody CategoryRegisterRequestDto categoryRegisterRequestDto);


    @Operation(
            summary = "기존 카테고리 수정",
            description = "Category - 데이터베이스의 기존 카테고리 데이터를 변경",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "기존 카테고리명과 변경된 카테고리명을 반환"
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
    @PutMapping("/admin/category/update")
    ResponseEntity<CategoryUpdateResponseDto> updateCategory(
            @Parameter(description = "현재 카테고리명과, 변경할 카테고리명")
            @Valid @RequestBody CategoryUpdateRequestDto categoryUpdateRequestDto);



    @Operation(
            summary = "기존 카테고리 삭제",
            description = "Category - 데이터베이스의 기존 카테고리 데이터를 완전히 삭제",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "카테고리 삭제에 성공한 경우"
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
                            description = "연관 되어있는 상품이 존재해서 삭제하지 못하는 경우"
                    )
            }
    )
    @DeleteMapping("/admin/category/delete/{categoryId}")
    ResponseEntity<Void> deleteCategory(
            @Parameter(description = "삭제할 카테고리의 아이디")
            @PathVariable("categoryId") Long categoryId);



    @Operation(
            summary = "모든 카테고리 페이지 조회",
            description = "Category - 모든 카테고리의 페이지를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "카테고리의 페이지 반환"
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
    @GetMapping("/admin/categories/all")
    ResponseEntity<Page<CategoryGetResponseDto>> getAllCategories(
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto);

    @Operation(
            summary = "특정 문자열이 포함된 카테고리 페이지 조회",
            description = "Category - 특정 문자열이 포함된 카테고리의 페이지를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "특정 문자열이 포함된 카테고리의 페이지 반환"
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
    @GetMapping("/admin/categories/containing")
    ResponseEntity<Page<CategoryGetResponseDto>> getNameContainingCategories(
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "검색 기준으로 사용할 문자열")
            @NotBlank @RequestParam("categoryName") String categoryName);

    @Operation(
            summary = "특정 카테고리를 상위 카테고리로 갖는 카테고리 페이지 조회",
            description = "Category - 특정 카테고리를 상위 카테고리로 갖는 카테고리 페이지를 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "특정 카테고리를 상위 카테고리로 갖는 카테고리의 페이지 반환"
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
    @GetMapping("/admin/categories/{categoryId}/sub")
    ResponseEntity<Page<CategoryGetResponseDto>> getSubCategories(
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "조건으로 이용할 상위 카테고리의 아이디")
            @PathVariable("categoryId") Long categoryId);


    @Operation(
            summary = "모든 카테고리를 트리 형태로 조회",
            description = "Category - 모든 카테고리를 트리 형태로 조회 (어떤 카테고리가 어떤 카테고리의 상위/하위인지 나오게)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "모든 카테고리를 트리 형태로 반환"
                    )
            }
    )
    @GetMapping("/categories/tree")
    ResponseEntity<CategoryNodeResponseDto> getCategoriesTree();
}
