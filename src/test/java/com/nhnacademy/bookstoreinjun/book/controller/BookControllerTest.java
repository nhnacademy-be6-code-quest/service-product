package com.nhnacademy.bookstoreinjun.book.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.nhnacademy.bookstoreinjun.config.HeaderConfig;
import com.nhnacademy.bookstoreinjun.config.SecurityConfig;
import com.nhnacademy.bookstoreinjun.controller.BookController;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.exception.AladinJsonProcessingException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundIdException;
import com.nhnacademy.bookstoreinjun.service.aladin.AladinService;
import com.nhnacademy.bookstoreinjun.service.book.BookService;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
@WebMvcTest(BookController.class)
@Import({SecurityConfig.class, HeaderConfig.class})
@DisplayName("도서 컨트롤러 테스트")
class BookControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    HttpHeaders header;

    @MockBean
    AladinService aladinService;

    @MockBean
    BookService bookService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        objectMapper.registerModule(new JavaTimeModule());
    }

    @DisplayName("도서 조회 성공 테스트 - 알라딘 api")
    @Test
    void test1() throws Exception {
        mockMvc.perform(get("/api/product/book")
                        .header("X-User-Role", "ROLE_ADMIN")
                        .header("X-User-Id", 1)
                        .param("title","이해"))
                .andExpect(status().isOk())
                .andExpect(header().stringValues("Content-Type", "application/json"));

        verify(aladinService,times(1)).getAladdinBookPage(any(), eq("이해"));
    }

    @DisplayName("도서 조회 실패 테스트 - 알라딘 api")
    @Test
    @WithMockUser(roles = "ADMIN")
    void test4() throws Exception {
        when(aladinService.getAladdinBookPage(any(), eq("이해"))).thenThrow(new AladinJsonProcessingException("error"));

        mockMvc.perform(get("/api/product/book")
                        .header("X-User-Role", "ROLE_ADMIN")
                        .header("X-User-Id", 1)
                        .param("title","이해"))
                .andExpect(status().is5xxServerError());
    }

    @DisplayName("도서 상품 중복 확인 테스트")
    @Test
    @WithMockUser(roles = "ADMIN")
    void isbnCheckTest() throws Exception {
        mockMvc.perform(get("/api/product/book/isbnCheck")
                        .param("isbn", "1234567890"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    @DisplayName("도서 상품 등록 성공 테스트")
    @Test
    @WithMockUser(roles = "ADMIN")
    void test2() throws Exception {

        BookProductRegisterRequestDto bookProductRegisterRequestDto = BookProductRegisterRequestDto.builder()
                .title("test title")
                .pubDate(LocalDate.now())
                .publisher("test publisher")
                .author("test author")
                .cover("test cover")
                .isbn("123456789a")
                .isbn13("123456789abcd")
                .productName("test product name")
                .productDescription("test product description")
                .productInventory(0)
                .productPriceStandard(1)
                .productPriceSales(1)
                .packable(false)
                .categories(
                        Set.copyOf(List.of("category1"))
                )
                .tags(
                        Set.copyOf(Arrays.asList("test tag1","test tag2")))
                .build();

        String json = objectMapper.writeValueAsString(bookProductRegisterRequestDto);

        mockMvc.perform(post("/api/product/admin/book/register")
                        .with(csrf())
                        .header("X-User-Role", "ROLE_ADMIN")
                        .header("X-User-Id", 1)
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andDo(print());

        verify(bookService,times(1)).saveBook(any());
    }

    @DisplayName("도서 상품 등록 실패 테스트 - 상품명 제약 조건 위반 (최소 2 글자)")
    @Test
    @WithMockUser(roles = "ADMIN")
    void test5() throws Exception {
        BookProductRegisterRequestDto bookProductRegisterRequestDto = BookProductRegisterRequestDto.builder()
                .title("test title")
                .pubDate(LocalDate.now())
                .publisher("test publisher")
                .author("test author")
                .cover("test cover")
                .isbn("123456789a")
                .isbn13("123456789abcd")
                .productName("T")
                .productDescription("test product description")
                .productInventory(0)
                .productPriceStandard(1)
                .productPriceSales(1)
                .packable(false)
                .categories(Set.copyOf(Arrays.asList("test category1","test category2")))
                .tags(Set.copyOf(Arrays.asList("test tag1","test tag2")))
                .build();

        String json = objectMapper.writeValueAsString(bookProductRegisterRequestDto);

        mockMvc.perform(post("/api/product/admin/book/register")
                        .with(csrf())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Role", "ROLE_ADMIN")
                        .header("X-User-Id", 1))
                .andExpect(status().isInternalServerError())
                .andDo(print());

        verify(bookService,times(0)).saveBook(any());
    }

    @DisplayName("도서 상품 업데이트 성공")
    @Test
    @WithMockUser(roles = "ADMIN")
    void test6() throws Exception {
        BookProductUpdateRequestDto bookProductUpdateRequestDto = BookProductUpdateRequestDto.builder()
                .productId(1L)
                .productName("updated name")
                .productDescription("test product description")
                .productInventory(0)
                .productPriceSales(1)
                .productState(1)
                .packable(false)
                .categories(Set.copyOf(Arrays.asList("test category1","test category2")))
                .tags(Set.copyOf(Arrays.asList("test tag1","test tag2")))
                .build();

        String json = objectMapper.writeValueAsString(bookProductUpdateRequestDto);

        mockMvc.perform(put("/api/product/admin/book/update")
                        .with(csrf())
                        .content(json)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-User-Role", "ROLE_ADMIN")
                        .header("X-User-Id", 1))
                .andExpect(status().isOk())
                .andDo(print());

        verify(bookService,times(0)).saveBook(any());
    }


    @DisplayName("도서 조회 성공 - 개별 도서")
    @Test
    @WithMockUser(roles = "CLIENT")
    void getIndividualBookTestSuccess() throws Exception {
        mockMvc.perform(get("/api/product/book/1"))
                .andExpect(status().isOk());
    }

    @DisplayName("도서 조회 실패 - 개별 도서 (존재하지 않는 도서)")
    @Test
    @WithMockUser(roles = "CLIENT")
    void getIndividualBookTestFailure() throws Exception {
        when(bookService.getBookByProductId(any(), eq(1L))).thenThrow(NotFoundIdException.class);

        mockMvc.perform(get("/api/product/book/1"))
                .andExpect(status().isNotFound());
    }

    @DisplayName("도서 페이지 조회 성공")
    @WithMockUser(roles = "CLIENT")
    @Test
    void getBookPageSuccess() throws Exception {
        PageRequestDto dto = PageRequestDto.builder().build();

        String json = objectMapper.writeValueAsString(dto);

        mockMvc.perform(get("/api/product/books")
                .content(json)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @DisplayName("도서 페이지 조회 - 제목 포함")
    @WithMockUser(roles = "CLIENT")
    @Test
    void getBookPageByTitle() throws Exception {
        mockMvc.perform(get("/api/product/books/containing")
                                .param("title", "test"))
                .andExpect(status().isOk());
    }

    @DisplayName("도서 페이지 조회 - 카테고리")
    @WithMockUser(roles = "CLIENT")
    @Test
    void getBookPageByCategory() throws Exception {
        mockMvc.perform(get("/api/product/books/category/1"))
                .andExpect(status().isOk());
    }

    @DisplayName("도서 페이지 조회 - 태그")
    @WithMockUser(roles = "CLIENT")
    @Test
    void getBookPageByTag() throws Exception {
        mockMvc.perform(get("/api/product/books/tagFilter")
                        .param("tagName", "test,test2"))
                .andExpect(status().isOk());
    }

    @DisplayName("도서 페이지 조회 - 나의 좋아요")
    @WithMockUser(roles = "CLIENT")
    @Test
    void getBookPageByMyLike() throws Exception {
        mockMvc.perform(get("/api/product/client/books/like")
                        .header("X-User-Id", 1))
                .andExpect(status().isOk());
    }
}
