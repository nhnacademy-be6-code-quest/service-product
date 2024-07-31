package com.nhnacademy.bookstoreinjun.controller;

import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagRegisterResponseDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagUpdateResponseDto;
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

@Tag(name = "Tag", description = "태그 관련 API")
public interface TagControllerInterface {

    @Operation(
            summary = "태그 신규 등록 ",
            description = "Tag - 상품에 달 새로운 태그를 등록",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "등록된 태그의 아이디와 이름을 반환"
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
                            description = "관리자 권한이 아닐 경우 반환 (필터에 의해 동작)"
                    )
            }
    )
    @PostMapping("/admin/tag/register")
    ResponseEntity<TagRegisterResponseDto> createTag(
            @Parameter(description = "등록할 태그의 이름이 담긴 dto")
            @Valid @RequestBody TagRegisterRequestDto tagRegisterRequestDto);


    @Operation(
            summary = "태그명 변경",
            description = "Tag - 기존 태그의 이름을 다른 이름으로 변경",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "태그의 수정 전 이름과 수정 후 이름, 수정 시간을 반환"
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
                            description = "관리자 권한이 아닐 경우 반환 (필터에 의해 동작)"
                    )
            }
    )
    @PutMapping("/admin/tag/update")
    ResponseEntity<TagUpdateResponseDto> updateTag(
            @Parameter(description = "수정 전, 수정 후 태그의 이름이 담긴 dto")
            @Valid @RequestBody TagUpdateRequestDto tagUpdateRequestDto);


    @Operation(
            summary = "태그 삭제",
            description = "Tag - 기존 태그를 삭제",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "태그 삭제 성공시 반환"
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "비회원이 접근한 경우"
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "관리자 권한이 아닐 경우 반환 (필터에 의해 동작)"
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "해당 태그가 조회되지 않을 경우"
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "해당 태그를 달고 있는 상품이 존재할 경우"
                    )
            }
    )
    @DeleteMapping("/admin/tag/delete/{tagId}")
    ResponseEntity<Void> deleteTag(
            @Parameter(description = "삭제할 태그의 아이디")
            @PathVariable("tagId") Long tagId);


    @Operation(
            summary = "모든 태그 페이지 조회",
            description = "Tag - 상품을 등록할 때, 태그를 다는데 필요한 페이지 조회",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "태그의 페이지를 반환"
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
                            description = "관리자 권한이 아닐 경우 반환 (필터에 의해 동작)"
                    )
            }
    )
    @GetMapping("/admin/tags/all")
    ResponseEntity<Page<TagGetResponseDto>> getAllTags(
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto);



    @Operation(
            summary = "특정 문자열을 포함한 태그 페이지 조회",
            description = "Tag - 상품을 등록할 때, 태그를 다는데 필요한 페이지 조회 (특정 문자열 포함)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "특정 문자열을 포함한 태그의 페이지를 반환"
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
                            description = "관리자 권한이 아닐 경우 반환 (필터에 의해 동작)"
                    )
            }
    )
    @GetMapping("/admin/tags/containing")
    ResponseEntity<Page<TagGetResponseDto>> getNameContainingTagPage(
            @Parameter(description = "페이지 요청의 파라미터. page, size, 정렬 기준, 오름차순/내림차순 여부 포함")
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @Parameter(description = "검색 조건으로서의 문자열")
            @NotBlank @RequestParam("tagName") String tagName);
}
