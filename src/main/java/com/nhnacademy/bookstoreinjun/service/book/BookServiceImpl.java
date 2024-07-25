package com.nhnacademy.bookstoreinjun.service.book;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.book.BookProductUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductRegisterResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductUpdateResponseDto;
import com.nhnacademy.bookstoreinjun.entity.Book;
import com.nhnacademy.bookstoreinjun.entity.Product;
import com.nhnacademy.bookstoreinjun.entity.ProductCategory;
import com.nhnacademy.bookstoreinjun.entity.ProductCategoryRelation;
import com.nhnacademy.bookstoreinjun.entity.ProductTag;
import com.nhnacademy.bookstoreinjun.entity.Tag;
import com.nhnacademy.bookstoreinjun.exception.DuplicateException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundIdException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundNameException;
import com.nhnacademy.bookstoreinjun.exception.XUserIdNotFoundException;
import com.nhnacademy.bookstoreinjun.repository.QuerydslRepository;
import com.nhnacademy.bookstoreinjun.repository.BookRepository;
import com.nhnacademy.bookstoreinjun.repository.ProductCategoryRepository;
import com.nhnacademy.bookstoreinjun.repository.ProductRepository;
import com.nhnacademy.bookstoreinjun.repository.TagRepository;
import com.nhnacademy.bookstoreinjun.service.product_category_relation.ProductCategoryRelationService;
import com.nhnacademy.bookstoreinjun.service.product_tag.ProductTagService;
import com.nhnacademy.bookstoreinjun.util.PageableUtil;
import com.nhnacademy.bookstoreinjun.util.ProductCheckUtil;
import com.nhnacademy.bookstoreinjun.util.SortCheckUtil;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {
    private final BookRepository bookRepository;

    private final QuerydslRepository querydslRepository;

    private final ProductRepository productRepository;

    private final TagRepository tagRepository;

    private final ProductCategoryRepository productCategoryRepository;

    private final ProductCategoryRelationService productCategoryRelationService;

    private final ProductTagService productTagService;

    private final ProductCheckUtil productCheckUtil;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String TYPE = "book";

    private static final String CATEGORY = "category";

    private static final int DEFAULT_PAGE_SIZE = 10;
    private static final String DEFAULT_SORT = "product.productRegisterDate";


    private void saveProductCategoryRelation(Set<String> categories, Product product){
        for (String categoryName : categories) {
            ProductCategory productCategory = productCategoryRepository.findByCategoryName(categoryName);
            if (productCategory == null){
                throw new NotFoundNameException(CATEGORY, categoryName);
            }

            ProductCategoryRelation productCategoryRelation = ProductCategoryRelation.builder()
                    .productCategory(productCategory)
                    .product(product)
                    .build();

            productCategoryRelationService.saveProductCategoryRelation(productCategoryRelation);
        }
    }

    private void saveProductTag(Set<String> tags, Product product){
        if (tags != null){
            for (String tagName : tags) {
                Tag tag = tagRepository.findByTagName(tagName);
                if (tag == null) {
                    throw new NotFoundNameException("tag", tagName);
                }

                ProductTag productTag = ProductTag.builder()
                        .product(product)
                        .tag(tag)
                        .build();
                productTagService.saveProductTag(productTag);
            }
        }
    }

    @Override
    public boolean checkIfBookExists(String isbn) {
        return bookRepository.existsByIsbn(isbn);
    }

    @Override
    public BookProductGetResponseDto getBookByProductId(Long clientIdOfHeader, Long productId) {
        if(!productRepository.existsById(productId)){
            throw new NotFoundIdException(TYPE, productId);
        }
        try {
            return querydslRepository.findBookByProductId(clientIdOfHeader, productId);
        }catch (NullPointerException e){
            throw new NotFoundIdException(TYPE, productId);
        }
    }


    @Override
    public Page<BookProductGetResponseDto> getBookPageByProductState(Long clientIdOfHeader, PageRequestDto pageRequestDto, Integer productState
    ) {
        Pageable pageable = PageableUtil.makePageable(pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT);

        try{
            Page<BookProductGetResponseDto> result = querydslRepository.findAllBookPage(clientIdOfHeader, pageable, productState);

            PageableUtil.pageNumCheck(result, pageable);

            return result;
        }catch (InvalidDataAccessApiUsageException e) {
            throw SortCheckUtil.throwInvalidSortNameException(pageable);
        }
    }


    @Override
    public Page<BookProductGetResponseDto> getNameContainingBookPageByProductState(Long clientIdOfHeader, PageRequestDto pageRequestDto, String title, Integer productState
    ) {
        Pageable pageable = PageableUtil.makePageable(pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
        try{
            Page<BookProductGetResponseDto> result = querydslRepository.findNameContainingBookPage(clientIdOfHeader, pageable, title,productState);

            PageableUtil.pageNumCheck(result, pageable);

            return result;
        }catch (InvalidDataAccessApiUsageException e) {
            throw SortCheckUtil.throwInvalidSortNameException(pageable);
        }
    }


    @Override
    public Page<BookProductGetResponseDto> getBookPageFilterByTagsAndProductState(Long clientIdOfHeader, PageRequestDto pageRequestDto, Set<String> tags, Boolean conditionIsAnd, Integer productState
    ) {
        Pageable pageable = PageableUtil.makePageable(pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT);

        try{
            Page<BookProductGetResponseDto> result = querydslRepository.findBooksByTagFilter(clientIdOfHeader, tags, conditionIsAnd, pageable, productState);
            PageableUtil.pageNumCheck(result, pageable);

            return result;
        }catch (InvalidDataAccessApiUsageException e){
            throw SortCheckUtil.throwInvalidSortNameException(pageable);
        }
    }



    @Override
    public Map<String, Page<BookProductGetResponseDto>> getBookPageFilterByCategoryAndProductState
            (Long clientIdOfHeader, PageRequestDto pageRequestDto, Long categoryId, Integer productState) {
        Pageable pageable = PageableUtil.makePageable(pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT);

        try {
            ProductCategory productCategory = productCategoryRepository.findById(categoryId).orElseThrow(() -> new NotFoundIdException(CATEGORY, categoryId));
            String categoryJson = objectMapper.writeValueAsString(productCategory);
            Page<BookProductGetResponseDto> result = querydslRepository.findBooksByCategoryFilter(clientIdOfHeader, categoryId, pageable, productState);

            PageableUtil.pageNumCheck(result, pageable);

            return Map.of(categoryJson, result);
        }catch (InvalidDataAccessApiUsageException e){
            throw SortCheckUtil.throwInvalidSortNameException(pageable);
        }catch (JsonProcessingException e) {
            log.warn("JSON parsing failed; the category might be causing a circular reference issue: categoryId={}", categoryId, e);
            throw new NotFoundIdException(CATEGORY, categoryId);
        }
    }

    @Override
    public Page<BookProductGetResponseDto> getLikeBookPage(Long clientIdOfHeader, PageRequestDto pageRequestDto, Integer productState) {
        if (clientIdOfHeader ==- 1){
            throw new XUserIdNotFoundException();
        }
        Pageable pageable = PageableUtil.makePageable(pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT);
        try {
            Page<BookProductGetResponseDto> result = querydslRepository.findLikeBooks(clientIdOfHeader, pageable, productState);

            PageableUtil.pageNumCheck(result, pageable);

            return result;
        }catch (InvalidDataAccessApiUsageException e){
            throw SortCheckUtil.throwInvalidSortNameException(pageable);
        }
    }

    @Override
    public ProductRegisterResponseDto saveBook(BookProductRegisterRequestDto bookProductRegisterRequestDto) {
        log.info("trying save book : {}", bookProductRegisterRequestDto);
        String isbn = bookProductRegisterRequestDto.isbn();
        if (isbn != null && bookRepository.existsByIsbn(isbn)){
            log.info("book already exists : {}", bookProductRegisterRequestDto);
            throw new DuplicateException(TYPE);
        }else{
            Product product = productRepository.save(
                    Product.builder()
                    .productName(bookProductRegisterRequestDto.productName())
                    .productPriceStandard(bookProductRegisterRequestDto.productPriceStandard())
                    .productPriceSales(bookProductRegisterRequestDto.productPriceSales())
                    .productInventory(bookProductRegisterRequestDto.productInventory())
                    .productThumbnailUrl(bookProductRegisterRequestDto.cover())
                    .productDescription(bookProductRegisterRequestDto.productDescription())
                    .productPackable(bookProductRegisterRequestDto.packable())
                    .build());

            productCheckUtil.checkProduct(product);

            Set<String> categories = bookProductRegisterRequestDto.categories();
            Set<String> tags = bookProductRegisterRequestDto.tags();

            saveProductCategoryRelation(categories, product);
            saveProductTag(tags, product);

            bookRepository.save(
                    Book.builder()
                    .title(bookProductRegisterRequestDto.title())
                    .publisher(bookProductRegisterRequestDto.publisher())
                    .author(bookProductRegisterRequestDto.author())
                    .pubDate(bookProductRegisterRequestDto.pubDate())
                    .isbn(bookProductRegisterRequestDto.isbn())
                    .isbn13(bookProductRegisterRequestDto.isbn13())
                    .product(product)
                    .build());

            log.info("book save success, {}", bookProductRegisterRequestDto);
            return new ProductRegisterResponseDto(product.getProductId(), product.getProductRegisterDate());
        }
    }


    @Override
    public ProductUpdateResponseDto updateBook(BookProductUpdateRequestDto bookProductUpdateRequestDto) {
        log.info("trying update book : {}", bookProductUpdateRequestDto);
        Long productId = bookProductUpdateRequestDto.productId();
        Product product = productRepository.findByProductId(productId);
        productCheckUtil.checkProduct(product);

        product.setProductDescription(bookProductUpdateRequestDto.productDescription());
        product.setProductInventory(bookProductUpdateRequestDto.productInventory());
        product.setProductState(bookProductUpdateRequestDto.productState());
        product.setProductPriceSales(bookProductUpdateRequestDto.productPriceSales());
        product.setProductPackable(bookProductUpdateRequestDto.packable());

        Product updateProduct = productRepository.save(product);
        Set<String> categories = bookProductUpdateRequestDto.categories();
        Set<String> tags = bookProductUpdateRequestDto.tags();

        productCategoryRelationService.clearProductCategoryRelationsByProduct(updateProduct);
        productTagService.clearTagsByProduct(updateProduct);

        saveProductCategoryRelation(categories, updateProduct);
        saveProductTag(tags, updateProduct);

        return new ProductUpdateResponseDto(LocalDateTime.now());
    }
}
