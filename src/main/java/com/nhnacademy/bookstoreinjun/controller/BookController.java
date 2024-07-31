package com.nhnacademy.bookstoreinjun.controller;

import com.nhnacademy.bookstoreinjun.dto.book.BookProductGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.book.aladin.AladinBookResponseDto;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductRegisterResponseDto;
import com.nhnacademy.bookstoreinjun.dto.error.ErrorResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductUpdateResponseDto;
import com.nhnacademy.bookstoreinjun.exception.AladinJsonProcessingException;
import com.nhnacademy.bookstoreinjun.service.aladin.AladinService;
import com.nhnacademy.bookstoreinjun.service.book.BookService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class BookController implements BookControllerInterface{

    private final AladinService aladinService;

    private final BookService bookService;

    private final HttpHeaders header;

    private static final String ID_HEADER = "X-User-Id";


    @ExceptionHandler(AladinJsonProcessingException.class)
    public ResponseEntity<ErrorResponseDto> exceptionHandler(AladinJsonProcessingException ex) {
        return new ResponseEntity<>(new ErrorResponseDto(ex.getMessage()), header, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/admin/book/roleCheck")
    public ResponseEntity<Void> roleCheck(){
        return new ResponseEntity<>(HttpStatus.OK);
    }


    @GetMapping("/admin/book")
    public ResponseEntity<Page<AladinBookResponseDto>> getBooksForAdmin(
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam("title") String title) {
        return new ResponseEntity<>(aladinService.getAladdinBookPage(pageRequestDto, title), header, HttpStatus.OK);
    }


    @GetMapping("/admin/book/isbnCheck")
    public ResponseEntity<Void> checkIfBookExistsForAdmin(@RequestParam("isbn") String isbn){
        boolean duplicate = bookService.checkIfBookExists(isbn);
        return new ResponseEntity<>(null, header, duplicate ? HttpStatus.CONFLICT : HttpStatus.OK);
    }

    @PostMapping("/admin/book/register")
    public ResponseEntity<ProductRegisterResponseDto> saveBookProduct(
            @Valid @RequestBody BookProductRegisterRequestDto bookProductRegisterRequestDto){
        return new ResponseEntity<>(bookService.saveBook(bookProductRegisterRequestDto), header, HttpStatus.CREATED);
    }


    @PutMapping("/admin/book/update")
    public ResponseEntity<ProductUpdateResponseDto> updateBook(
            @Valid @RequestBody BookProductUpdateRequestDto bookProductUpdateRequestDto){
        return new ResponseEntity<>(bookService.updateBook(bookProductUpdateRequestDto), header, HttpStatus.OK);
    }


    @GetMapping("/book/{productId}")
    public ResponseEntity<BookProductGetResponseDto> getSingleBookInfo(
            @RequestHeader HttpHeaders httpHeaders,
            @Min(1) @PathVariable long productId){
        return new ResponseEntity<>(bookService.getBookByProductId(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), productId), header, HttpStatus.OK);
    }


    @GetMapping("/books")
    public ResponseEntity<Page<BookProductGetResponseDto>> getAllBookPage(
            @RequestHeader HttpHeaders httpHeaders,
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam(name = "productState", required = false) Integer productState
    ){
        return new ResponseEntity<>(bookService.getBookPageByProductState(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), pageRequestDto, productState), header, HttpStatus.OK);
    }


    @GetMapping("/books/containing")
    public ResponseEntity<Page<BookProductGetResponseDto>> getNameContainingBookPage(
            @RequestHeader HttpHeaders httpHeaders,
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam(name = "productState", required = false) Integer productState,
            @RequestParam("title") String title){
        return new ResponseEntity<>(bookService.getNameContainingBookPageByProductState(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), pageRequestDto, title, productState), header, HttpStatus.OK);
    }




    @GetMapping("/books/category/{categoryId}")
    public ResponseEntity<Map<String, Page<BookProductGetResponseDto>>> getBookPageFilterByCategory(
            @RequestHeader HttpHeaders httpHeaders,
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam(name = "productState", required = false) Integer productState,
            @PathVariable("categoryId") Long categoryId){
        return new ResponseEntity<>(bookService.getBookPageFilterByCategoryAndProductState(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), pageRequestDto, categoryId, productState), header, HttpStatus.OK);
    }


    @GetMapping("/books/tagFilter")
    public ResponseEntity<Page<BookProductGetResponseDto>> getBookPageFilterByTag(
            @RequestHeader HttpHeaders httpHeaders,
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam("tagName") Set<String> tagNameSet,
            @RequestParam(name = "productState", required = false) Integer productState,
            @RequestParam(value = "isAnd", required = false, defaultValue = "true") Boolean conditionIsAnd){
        return new ResponseEntity<>(bookService.getBookPageFilterByTagsAndProductState(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), pageRequestDto, tagNameSet, conditionIsAnd, productState), header, HttpStatus.OK);
    }

    @GetMapping("/client/books/like")
    public ResponseEntity<Page<BookProductGetResponseDto>> getLikeBookPage(
            @RequestHeader HttpHeaders httpHeaders,
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam(name = "productState", required = false) Integer productState
    ){
        return new ResponseEntity<>(bookService.getLikeBookPage(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), pageRequestDto, productState), header, HttpStatus.OK);
    }


    @GetMapping("/admin/book/{productId}")
    public ResponseEntity<BookProductGetResponseDto> getSingleBookInfoForAdmin(
            @RequestHeader HttpHeaders httpHeaders,
            @Min(1) @PathVariable long productId){
        return new ResponseEntity<>(bookService.getBookByProductId(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), productId), header, HttpStatus.OK);
    }


    @GetMapping("/admin/books")
    public ResponseEntity<Page<BookProductGetResponseDto>> getAllBookPageForAdmin(
            @RequestHeader HttpHeaders httpHeaders,
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam(name = "productState", required = false) Integer productState
    ){
        return new ResponseEntity<>(bookService.getBookPageByProductState(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), pageRequestDto, productState), header, HttpStatus.OK);
    }


    @GetMapping("/admin/books/containing")
    public ResponseEntity<Page<BookProductGetResponseDto>> getNameContainingBookPageForAdmin(
            @RequestHeader HttpHeaders httpHeaders,
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam(name = "productState", required = false) Integer productState,
            @RequestParam("title") String title){
        return new ResponseEntity<>(bookService.getNameContainingBookPageByProductState(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), pageRequestDto, title, productState), header, HttpStatus.OK);
    }


    @GetMapping("/admin/books/category/{categoryId}")
    public ResponseEntity<Map<String, Page<BookProductGetResponseDto>>> getBookPageFilterByCategoryForAdmin(
            @RequestHeader HttpHeaders httpHeaders,
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam(name = "productState", required = false) Integer productState,
            @PathVariable("categoryId") Long categoryId){
        return new ResponseEntity<>(bookService.getBookPageFilterByCategoryAndProductState(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), pageRequestDto, categoryId, productState), header, HttpStatus.OK);
    }


    @GetMapping("/admin/books/tagFilter")
    public ResponseEntity<Page<BookProductGetResponseDto>> getBookPageFilterByTagForAdmin(
            @RequestHeader HttpHeaders httpHeaders,
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam("tagName") Set<String> tagNameSet,
            @RequestParam(name = "productState", required = false) Integer productState,
            @RequestParam(value = "isAnd", required = false, defaultValue = "true") Boolean conditionIsAnd){
        return new ResponseEntity<>(bookService.getBookPageFilterByTagsAndProductState(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), pageRequestDto, tagNameSet, conditionIsAnd, productState), header, HttpStatus.OK);
    }

    @GetMapping("/admin/books/like")
    public ResponseEntity<Page<BookProductGetResponseDto>> getLikeBookPageForAdmin(
            @RequestHeader HttpHeaders httpHeaders,
            @Valid @ModelAttribute PageRequestDto pageRequestDto,
            @RequestParam(name = "productState", required = false) Integer productState
    ){
        return new ResponseEntity<>(bookService.getLikeBookPage(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), pageRequestDto, productState), header, HttpStatus.OK);
    }
}
