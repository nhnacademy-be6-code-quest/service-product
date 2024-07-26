package com.nhnacademy.bookstoreinjun.tag.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstoreinjun.config.HeaderConfig;
import com.nhnacademy.bookstoreinjun.config.SecurityConfig;
import com.nhnacademy.bookstoreinjun.controller.TagController;
import com.nhnacademy.bookstoreinjun.dto.tag.TagRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.tag.TagUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.exception.DuplicateException;
import com.nhnacademy.bookstoreinjun.service.tag.TagService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TagController.class)
@Import({SecurityConfig.class, HeaderConfig.class})

class TagControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TagService tagService;

    @DisplayName("태그 등록 성공 테스트")
    @Test
    void testCreateTagSuccess() throws Exception {
        TagRegisterRequestDto dto = TagRegisterRequestDto.builder()
                        .tagName("test tag")
                        .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(post("/api/product/admin/tag/register")
                        .with(csrf())
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_ADMIN")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        verify(tagService,times(1)).saveTag(dto);
    }

    @DisplayName("태그 등록 실패 테스트 - 중복된 태그명")
    @Test
    void testCreateTagFailureByDuplicateTagName() throws Exception {
        TagRegisterRequestDto dto = TagRegisterRequestDto.builder()
                .tagName("test tag")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dto);
        when(tagService.saveTag(dto)).thenThrow(DuplicateException.class);

        mockMvc.perform(post("/api/product/admin/tag/register")
                        .with(csrf())
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_ADMIN")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());

        verify(tagService,times(1)).saveTag(dto);
    }

    @DisplayName("태그 수정 성공 테스트")
    @Test
    void testUpdateTagSuccess() throws Exception {
        TagUpdateRequestDto dto = TagUpdateRequestDto.builder()
                .currentTagName("test tag")
                .newTagName("update tag")
                .build();

        ObjectMapper objectMapper = new ObjectMapper();
        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(put("/api/product/admin/tag/update")
                        .with(csrf())
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_ADMIN")
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(tagService,times(1)).updateTag(dto);
    }

    @DisplayName("태그 삭제 성공 테스트")
    @Test
    void testDeleteTagSuccess() throws Exception {
        mockMvc.perform(delete("/api/product/admin/tag/delete/1")
                        .with(csrf())
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_ADMIN"))
                .andExpect(status().isOk());

        verify(tagService,times(1)).deleteTag(1L);
    }

    @DisplayName("태그 페이지 조회 성공 테스트 - 모든 태그")
    @Test
    void testGetAllTagSuccess() throws Exception {

        mockMvc.perform(get("/api/product/admin/tags/all")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_ADMIN"))
                .andExpect(status().isOk());

        verify(tagService,times(1)).getAllTagPage(any());
    }

    @DisplayName("태그 페이지 조회 성공 테스트 - 특정 이름 포함 태그")
    @Test
    void testGetNameContainingTagSuccess() throws Exception {

        mockMvc.perform(get("/api/product/admin/tags/containing")
                        .header("X-User-Id", "1")
                        .header("X-User-Role", "ROLE_ADMIN")
                        .param("tagName", "test"))
                .andExpect(status().isOk());

        verify(tagService,times(1)).getNameContainingTagPage(any(), eq("test"));
    }
}
