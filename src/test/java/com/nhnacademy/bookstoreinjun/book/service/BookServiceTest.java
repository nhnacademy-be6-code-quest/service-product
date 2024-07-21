package com.nhnacademy.bookstoreinjun.book.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.nhnacademy.bookstoreinjun.dto.book.BookProductGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.entity.Book;
import com.nhnacademy.bookstoreinjun.entity.Product;
import com.nhnacademy.bookstoreinjun.entity.ProductCategory;
import com.nhnacademy.bookstoreinjun.entity.ProductCategoryRelation;
import com.nhnacademy.bookstoreinjun.entity.ProductTag;
import com.nhnacademy.bookstoreinjun.entity.Tag;
import com.nhnacademy.bookstoreinjun.exception.DuplicateException;
import com.nhnacademy.bookstoreinjun.exception.InvalidSortNameException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundIdException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundNameException;
import com.nhnacademy.bookstoreinjun.exception.PageOutOfRangeException;
import com.nhnacademy.bookstoreinjun.exception.XUserIdNotFoundException;
import com.nhnacademy.bookstoreinjun.repository.QuerydslRepository;
import com.nhnacademy.bookstoreinjun.repository.BookRepository;
import com.nhnacademy.bookstoreinjun.repository.ProductCategoryRepository;
import com.nhnacademy.bookstoreinjun.repository.ProductRepository;
import com.nhnacademy.bookstoreinjun.repository.TagRepository;
import com.nhnacademy.bookstoreinjun.service.book.BookServiceImpl;
import com.nhnacademy.bookstoreinjun.service.product_category_relation.ProductCategoryRelationService;
import com.nhnacademy.bookstoreinjun.service.product_tag.ProductTagService;
import com.nhnacademy.bookstoreinjun.util.ProductCheckUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

@ExtendWith(MockitoExtension.class)
@DisplayName("도서 서비스 테스트")
class BookServiceTest {

    @InjectMocks
    private BookServiceImpl bookService;

    @Mock
    private BookRepository bookRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductCategoryRelationService productCategoryRelationService;

    @Mock
    private ProductTagService productTagService;

    @Mock
    private QuerydslRepository querydslRepository;

    @Mock
    private ProductCheckUtil productCheckUtil;

    @DisplayName("도서 등록 성공 테스트")
    @Test
    void saveTest(){
        when(bookRepository.save(any(Book.class))).thenReturn(new Book());
        when(productRepository.save(any(Product.class))).thenReturn(new Product());
        when(productCategoryRepository.findByCategoryName(any())).thenReturn(new ProductCategory());
        when(tagRepository.findByTagName(any())).thenReturn(new Tag());
        BookProductRegisterRequestDto dto = BookProductRegisterRequestDto.builder()
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
                .categories(Set.of("test category1","test category2"))
                .tags(Set.of("test tag1","test tag2"))
                .build();

        bookService.saveBook(dto);

        verify(bookRepository,times(1)).save(any(Book.class));
        verify(productRepository,times(1)).save(any(Product.class));
        verify(tagRepository,times(2)).findByTagName(any(String.class));
        verify(productCategoryRepository,times(2)).findByCategoryName(any(String.class));
        verify(productTagService,times(2)).saveProductTag(any(ProductTag.class));
        verify(productCategoryRelationService,times(2)).saveProductCategoryRelation(any(ProductCategoryRelation.class));
    }

    @DisplayName("도서 등록 실패 테스트 - 중복되는 isbn")
    @Test
    void saveTest2(){
        when(bookRepository.existsByIsbn("123456789a")).thenReturn(true);

        BookProductRegisterRequestDto dto = BookProductRegisterRequestDto.builder()
                .isbn("123456789a")
                .build();

        assertThrows(DuplicateException.class, () -> bookService.saveBook(dto));
    }

    @DisplayName("도서 업데이트 성공 테스트")
    @Test
    void updateTestSuccess(){
        Book testBook = new Book();
        Product testProduct = new Product();
        testBook.setProduct(testProduct);

        when(productRepository.findByProductId(1L)).thenReturn(testProduct);
        when(productCategoryRepository.findByCategoryName("test category1")).thenReturn(new ProductCategory());
        when(productCategoryRepository.findByCategoryName("test category2")).thenReturn(new ProductCategory());

        when(tagRepository.findByTagName("test tag1")).thenReturn(new Tag());
        when(tagRepository.findByTagName("test tag2")).thenReturn(new Tag());

        BookProductUpdateRequestDto dto = BookProductUpdateRequestDto.builder()
                .productId(1L)
                .productName("update product name")
                .productDescription("update product description")
                .packable(true)
                .productPriceSales(123)
                .productInventory(1234)
                .productState(1)
                .categories(Set.of("test category1","test category2"))
                .tags(Set.of("test tag1","test tag2"))
                .build();

        bookService.updateBook(dto);
        verify(productRepository,times(1)).findByProductId(1L);
        verify(productCheckUtil,times(1)).checkProduct(any());
        verify(productRepository,times(1)).save(any(Product.class));
        verify(productCategoryRelationService,times(1)).clearProductCategoryRelationsByProduct(any());
        verify(productTagService,times(1)).clearTagsByProduct(any());
        verify(productCategoryRelationService,times(2)).saveProductCategoryRelation(any());
        verify(productTagService,times(2)).saveProductTag(any());
    }

    @DisplayName("도서 업데이트 실패 테스트 - 존재하지 않는 도서")
    @Test
    void updateTestFailByNotExistingBook(){
        doThrow(NotFoundIdException.class).when(productCheckUtil).checkProduct(any());

        BookProductUpdateRequestDto dto = BookProductUpdateRequestDto.builder()
                .productId(1L)
                .productName("update product name")
                .productDescription("update product description")
                .packable(true)
                .productPriceSales(123)
                .productInventory(1234)
                .productState(1)
                .categories(Set.of("test category1", "test category2"))
                .tags(Set.of("test tag1"))
                .build();

        assertThrows(NotFoundIdException.class, () -> bookService.updateBook(dto));
    }


    @DisplayName("도서 업데이트 실패 테스트 - 존재하지 않는 태그")
    @Test
    void updateTestFailureByNotExistingTag() {
        Book testBook = new Book();
        Product testProduct = new Product();
        testBook.setProduct(testProduct);

        when(productRepository.findByProductId(1L)).thenReturn(testProduct);

        when(productCategoryRepository.findByCategoryName("test category1")).thenReturn(new ProductCategory());
        when(productCategoryRepository.findByCategoryName("test category2")).thenReturn(new ProductCategory());

        when(tagRepository.findByTagName("test tag1")).thenReturn(null);

        BookProductUpdateRequestDto dto = BookProductUpdateRequestDto.builder()
                .productId(1L)
                .productName("update product name")
                .productDescription("update product description")
                .packable(true)
                .productPriceSales(123)
                .productInventory(1234)
                .productState(1)
                .categories(Set.of("test category1", "test category2"))
                .tags(Set.of("test tag1"))
                .build();

        assertThrows(NotFoundNameException.class, () -> bookService.updateBook(dto));
    }


    @DisplayName("도서 업데이트 실패 테스트 - 존재하지 않는 카테고리")
    @Test
    void updateTestFailureByNotExistingProductCategory() {
        Book testBook = new Book();
        Product testProduct = new Product();
        testBook.setProduct(testProduct);

        when(productRepository.findByProductId(1L)).thenReturn(testProduct);

        when(productCategoryRepository.findByCategoryName("test category1")).thenReturn(null);

        BookProductUpdateRequestDto dto = BookProductUpdateRequestDto.builder()
                .productId(1L)
                .productName("update product name")
                .productDescription("update product description")
                .packable(true)
                .productPriceSales(123)
                .productInventory(1234)
                .productState(1)
                .categories(Set.of("test category1"))
                .build();

        assertThrows(NotFoundNameException.class, () -> bookService.updateBook(dto));
    }

    @DisplayName("도서 조회 성공 테스트 - 개별 도서")
    @Test
    void getIndividualBookTestSuccess(){
        Book testBook = new Book();
        testBook.setBookId(1L);
        Product testProduct = new Product();
        testProduct.setProductId(3L);
        testProduct.setProductCategoryRelations(new ArrayList<>());
        testProduct.setProductTags(new ArrayList<>());

        testBook.setProduct(testProduct);

        when(productRepository.existsById(1L)).thenReturn(true);

        when(querydslRepository.findBookByProductId(1L, 1L)).thenReturn(BookProductGetResponseDto.builder()
                .categorySet(Set.of(
                        ProductCategory.builder().build(),
                        ProductCategory.builder().build()
                ))
                .tagSet(Set.of(
                        Tag.builder().build(),
                        Tag.builder().build(),
                        Tag.builder().build()
                        ))
                .build());
        BookProductGetResponseDto dto = bookService.getBookByProductId(1L,1L);

        verify(productRepository,times(1)).existsById(1L);
        verify(querydslRepository,times(1)).findBookByProductId(1L,1L);

        assertNotNull(dto);
        assertEquals(2, dto.categorySet().size());
        assertEquals(3, dto.tagSet().size());
    }

    @DisplayName("도서 조회 실패 테스트 - 개별 도서 : 존재하지 않는 상품 id")
    @Test
    void getIndividualBookTestFailure() {
        assertThrows(NotFoundIdException.class, () -> bookService.getBookByProductId(1L, 1L));
    }

    @DisplayName("도서 조회 실패 테스트 - 도서가 아닌 상품 Id로 조회")
    @Test
    void getIndividualBookTestFailure2(){
        when(productRepository.existsById(1L)).thenReturn(true);

        when(querydslRepository.findBookByProductId(1L, 1L)).thenThrow(NullPointerException.class);

        assertThrows(NotFoundIdException.class, () -> bookService.getBookByProductId(1L,1L));
    }


    @DisplayName("도서 조회 성공 테스트 - 모든 도서 페이지")
    @Test
    void getBookPageTestSuccess(){
        BookProductGetResponseDto testBook1 = BookProductGetResponseDto.builder().build();
        BookProductGetResponseDto testBook2 = BookProductGetResponseDto.builder().build();

        PageRequestDto dto = PageRequestDto.builder().build();

        when(querydslRepository.findAllBookPage(eq(1L), any(), eq(0))).thenReturn(new PageImpl<>(Arrays.asList(testBook1, testBook2)));
        Page<BookProductGetResponseDto> bookPage = bookService.getBookPageByProductState(1L, dto,0);

        assertNotNull(bookPage);
        verify(querydslRepository,times(1)).findAllBookPage(eq(1L), any(), eq(0));
        assertEquals(2, bookPage.getTotalElements());
    }


    @DisplayName("도서 조회 실패 테스트 - 모든 도서 페이지 : 너무 큰 수의 페이지")
    @Test
    void getBookPageTestFailureByOutOfPageRange(){
        PageRequestDto dto = PageRequestDto.builder()
                .page(10)
                .build();


        when(querydslRepository.findAllBookPage(eq(1L),any(), eq(0))).thenReturn(new PageImpl<>(new ArrayList<>()));
        assertThrows(PageOutOfRangeException.class, () -> bookService.getBookPageByProductState(1L, dto, 0));
    }


    @DisplayName("도서 조회 실패 테스트 - 모든 도서 페이지 : 잘못된 정렬 조건")
    @Test
    void getBookPageTestFailureByWrongSort(){
        PageRequestDto dto = PageRequestDto.builder()
                .sort("wrong sort")
                .build();

        when(querydslRepository.findAllBookPage(eq(1L), any(), eq(0))).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(InvalidSortNameException.class, () -> bookService.getBookPageByProductState(1L, dto, 0));
    }


    @DisplayName("도서 조회 성공 테스트 - 특정 제목 포함 도서 페이지")
    @Test
    void getNameContainingBookPageTestSuccess(){
        BookProductGetResponseDto testBook1 = BookProductGetResponseDto.builder().build();
        BookProductGetResponseDto testBook2 = BookProductGetResponseDto.builder().build();

        PageRequestDto dto = PageRequestDto.builder().build();

        when(querydslRepository.findNameContainingBookPage(eq(1L), any(), eq("test"),eq(0))).thenReturn(new PageImpl<>(Arrays.asList(testBook1, testBook2)));
        Page<BookProductGetResponseDto> bookPage = bookService.getNameContainingBookPageByProductState(1L, dto,"test",0);

        assertNotNull(bookPage);
        verify(querydslRepository,times(1)).findNameContainingBookPage(eq(1L), any(), eq("test"), eq(0));
        assertEquals(2, bookPage.getTotalElements());
    }

    @DisplayName("도서 조회 실패 테스트 - 특정 제목 포함 도서 페이지 : 너무 큰 수의 페이지")
    @Test
    void getNameContainingBookPageTestFailureByOutOfPageRange(){
        PageRequestDto dto = PageRequestDto.builder()
                .page(10)
                .build();


        when(querydslRepository.findNameContainingBookPage(eq(1L),any(), eq("test"), eq(0))).thenReturn(new PageImpl<>(new ArrayList<>()));
        assertThrows(PageOutOfRangeException.class, () -> bookService.getNameContainingBookPageByProductState(1L, dto, "test", 0));
    }


    @DisplayName("도서 조회 실패 테스트 - 특정 제목 포함 도서 페이지 : 잘못된 정렬 조건")
    @Test
    void getNameContainingBookPageTestFailureByWrongSort(){
        PageRequestDto dto = PageRequestDto.builder()
                .sort("wrong sort")
                .build();

        when(querydslRepository.findNameContainingBookPage(eq(1L), any(), eq("test"), eq(0))).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(InvalidSortNameException.class, () -> bookService.getNameContainingBookPageByProductState(1L, dto, "test",0));
    }


    @DisplayName("도서 조회 성공 테스트 - 태그로 필터링 된 도서 페이지")
    @Test
    void getBookPageFilterByTagsTestSuccess(){
        BookProductGetResponseDto testBook1 = BookProductGetResponseDto.builder().build();
        BookProductGetResponseDto testBook2 = BookProductGetResponseDto.builder().build();

        PageRequestDto dto = PageRequestDto.builder().build();

        when(querydslRepository.findBooksByTagFilter(eq(1L),  eq(Set.copyOf(Arrays.asList("tag1", "tag2"))), eq(true), any(), eq(0))).thenReturn(new PageImpl<>(Arrays.asList(testBook1, testBook2)));
        Page<BookProductGetResponseDto> bookPage = bookService.getBookPageFilterByTagsAndProductState(1L, dto, Set.copyOf(Arrays.asList("tag1", "tag2")), true, 0);

        assertNotNull(bookPage);
        verify(querydslRepository,times(1)).findBooksByTagFilter(eq(1L), eq(Set.copyOf(Arrays.asList("tag1", "tag2"))), eq(true), any(), eq(0));
        assertEquals(2, bookPage.getTotalElements());
    }


    @DisplayName("도서 조회 실패 테스트 - 태그로 필터링 된 도서 페이지 : 너무 큰 수의 페이지")
    @Test
    void getBookPageFilterByTagsTestFailureByOutOfPageRange(){
        PageRequestDto dto = PageRequestDto.builder()
                .page(10)
                .build();
        Set<String> tags = Set.copyOf(Arrays.asList("tag1", "tag2"));
        when(querydslRepository.findBooksByTagFilter(eq(1L),eq(tags), eq(true), any(), eq(0))).thenReturn(new PageImpl<>(new ArrayList<>()));
        assertThrows(PageOutOfRangeException.class, () -> bookService.getBookPageFilterByTagsAndProductState(1L, dto, tags, true, 0));
    }


    @DisplayName("도서 조회 실패 테스트 - 태그로 필터링 된 도서 페이지 : 잘못된 정렬 조건")
    @Test
    void getBookPageFilterByTagsTestFailureByWrongSort(){
        PageRequestDto dto = PageRequestDto.builder()
                .sort("wrong sort")
                .build();

        Set<String> tags = Set.copyOf(Arrays.asList("tag1", "tag2"));
        when(querydslRepository.findBooksByTagFilter(eq(1L),eq(tags), eq(true), any(), eq(0))).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(InvalidSortNameException.class, () -> bookService.getBookPageFilterByTagsAndProductState(1L, dto, tags, true, 0));
    }


    @DisplayName("도서 조회 성공 테스트 - 카테고리로 필터링 된 도서 페이지")
    @Test
    void getBookPageFilterByCategoryTestSuccess(){
        BookProductGetResponseDto testBook1 = BookProductGetResponseDto.builder().build();
        BookProductGetResponseDto testBook2 = BookProductGetResponseDto.builder().build();

        PageRequestDto dto = PageRequestDto.builder().build();

        when(querydslRepository.findBooksByCategoryFilter(eq(1L),eq(1L), any(), eq(0))).thenReturn(new PageImpl<>(Arrays.asList(testBook1, testBook2)));
        Page<BookProductGetResponseDto> bookPage = bookService.getBookPageFilterByCategoryAndProductState(1L, dto, 1L,  0);

        assertNotNull(bookPage);
        verify(querydslRepository,times(1)).findBooksByCategoryFilter(eq(1L),eq(1L), any(), eq(0));
        assertEquals(2, bookPage.getTotalElements());
    }

    @DisplayName("도서 조회 실패 테스트 - 카테고리로 필터링 된 도서 페이지 : 너무 큰 수의 페이지")
    @Test
    void getBookPageFilterByCategoryTestFailureByOutOfPageRange(){
        PageRequestDto dto = PageRequestDto.builder()
                .page(10)
                .build();

        when(querydslRepository.findBooksByCategoryFilter(eq(1L),eq(1L), any(), eq(0))).thenReturn(new PageImpl<>(new ArrayList<>()));
        assertThrows(PageOutOfRangeException.class, () -> bookService.getBookPageFilterByCategoryAndProductState(1L, dto, 1L,  0));
    }


    @DisplayName("도서 조회 실패 테스트 - 카테고리로 필터링 된 도서 페이지 : 잘못된 정렬 조건")
    @Test
    void getBookPageFilterByCategoryTestFailureByWrongSort(){
        PageRequestDto dto = PageRequestDto.builder()
                .sort("wrong sort")
                .build();

        when(querydslRepository.findBooksByCategoryFilter(eq(1L),eq(1L), any(), eq(0))).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(InvalidSortNameException.class, () -> bookService.getBookPageFilterByCategoryAndProductState(1L, dto, 1L,  0));
    }


    @DisplayName("도서 조회 성공 테스트 - 좋아요를 누른 도서 페이지")
    @Test
    void getLikeBookPageTestSuccess(){
        BookProductGetResponseDto testBook1 = BookProductGetResponseDto.builder().build();
        BookProductGetResponseDto testBook2 = BookProductGetResponseDto.builder().build();

        PageRequestDto dto = PageRequestDto.builder().build();

        when(querydslRepository.findLikeBooks(eq(1L),any(), eq(0))).thenReturn(new PageImpl<>(Arrays.asList(testBook1, testBook2)));
        Page<BookProductGetResponseDto> bookPage = bookService.getLikeBookPage(1L, dto,0);

        assertNotNull(bookPage);
        verify(querydslRepository,times(1)).findLikeBooks(eq(1L),any(), eq(0));
        assertEquals(2, bookPage.getTotalElements());
    }


    @DisplayName("도서 조회 실패 테스트 - 좋아요를 누른 도서 페이지 : 존재하지 않는 유저")
    @Test
    void getLikeBookPageTestFailureByWrongUserId(){
        PageRequestDto dto = PageRequestDto.builder()
                .build();

        assertThrows(XUserIdNotFoundException.class, () -> bookService.getLikeBookPage(-1L, dto,0));
    }

    @DisplayName("도서 조회 실패 테스트 - 좋아요를 누른 도서 페이지 : 너무 큰 수의 페이지")
    @Test
    void getLikeBookPageTestFailureByOutOfPageRange(){
        PageRequestDto dto = PageRequestDto.builder()
                .page(10)
                .build();

        when(querydslRepository.findLikeBooks(eq(1L),any(), eq(0))).thenReturn(new PageImpl<>(new ArrayList<>()));
        assertThrows(PageOutOfRangeException.class, () -> bookService.getLikeBookPage(1L, dto,0));
    }


    @DisplayName("도서 조회 실패 테스트 - 좋아요를 누른 도서 페이지 : 잘못된 정렬 조건")
    @Test
    void getLikeBookPageTestFailureByWrongSort(){
        PageRequestDto dto = PageRequestDto.builder()
                .sort("wrong sort")
                .build();

        when(querydslRepository.findLikeBooks(eq(1L),any(), eq(0))).thenThrow(InvalidDataAccessApiUsageException.class);
        assertThrows(InvalidSortNameException.class, () -> bookService.getLikeBookPage(1L, dto,0));
    }


    @DisplayName("도서 중복 체크 테스트 - 중복된 도서가 있을 경우")
    @Test
    void checkIfBookExistTest(){
        when(bookRepository.existsByIsbn(any())).thenReturn(true);
        Boolean result = bookService.checkIfBookExists("1");
        assertNotNull(result);
        assertTrue(result);
    }

    @DisplayName("도서 중복 체크 테스트 - 중복된 도서가 없을 경우")
    @Test
    void checkIfBookExistTest2(){
        when(bookRepository.existsByIsbn(any())).thenReturn(false);
        Boolean result = bookService.checkIfBookExists("1");
        assertNotNull(result);
        assertFalse(result);
    }
}
