package com.nhnacademy.bookstoreinjun.service.product;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstoreinjun.dto.page.PageRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.InventoryDecreaseRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.InventoryIncreaseRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.InventorySetRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductGetNameAndPriceResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductLikeRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductLikeResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductStateUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductUpdateResponseDto;
import com.nhnacademy.bookstoreinjun.entity.Product;
import com.nhnacademy.bookstoreinjun.entity.ProductLike;
import com.nhnacademy.bookstoreinjun.exception.DuplicateException;
import com.nhnacademy.bookstoreinjun.exception.NotFoundIdException;
import com.nhnacademy.bookstoreinjun.exception.PageOutOfRangeException;
import com.nhnacademy.bookstoreinjun.exception.XUserIdNotFoundException;
import com.nhnacademy.bookstoreinjun.repository.ProductLikeRepository;
import com.nhnacademy.bookstoreinjun.repository.ProductRepository;
import com.nhnacademy.bookstoreinjun.repository.QuerydslRepository;
import com.nhnacademy.bookstoreinjun.util.PageableUtil;
import com.nhnacademy.bookstoreinjun.util.SortCheckUtil;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final ProductLikeRepository productLikeRepository;

    private final QuerydslRepository querydslRepository;

    private static final int DEFAULT_PAGE_SIZE = 10;

    private static final String DEFAULT_SORT = "productId";

    private static final String TYPE = "product";


    private final ObjectMapper objectMapper;

    private ProductGetResponseDto makeProductGetResponseDtoFromProduct(Product product) {
        return ProductGetResponseDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productState(product.getProductState())
                .productPriceStandard(product.getProductPriceStandard())
                .productPriceSales(product.getProductPriceSales())
                .productThumbNailImage(product.getProductThumbnailUrl())
                .build();
    }

    private Page<ProductGetResponseDto> makeProductGetResponseDtoPage(Pageable pageable, Page<Product> productPage) {
        int total = productPage.getTotalPages();
        int maxPage = pageable.getPageNumber() + 1;

        if (total != -0 && total < maxPage){
            throw new PageOutOfRangeException(total, maxPage);
        }

        return productPage.map(this::makeProductGetResponseDtoFromProduct);
    }


    @Override
    public Page<ProductGetResponseDto> findAllPage(@Valid PageRequestDto pageRequestDto) {
        Pageable pageable = PageableUtil.makePageable(pageRequestDto, DEFAULT_PAGE_SIZE, DEFAULT_SORT);

        try {
            Page<Product> productPage = productRepository.findAll(pageable);
            return makeProductGetResponseDtoPage(pageable, productPage);
        }catch (PropertyReferenceException e) {
            throw SortCheckUtil.throwInvalidSortNameException(pageable);
        }
    }

    @Override
    public ProductGetNameAndPriceResponseDto getSingleProductInfo(Long productId) {
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundIdException(TYPE, productId));
        return ProductGetNameAndPriceResponseDto.builder()
                .productId(product.getProductId())
                .productName(product.getProductName())
                .productPriceSales(product.getProductPriceSales())
                .build();
    }


    @Override
    public ProductLikeResponseDto saveProductLike(Long clientIdOfHeader, ProductLikeRequestDto productLikeRequestDto){
        if (clientIdOfHeader ==- 1){
            throw new XUserIdNotFoundException();
        }

        log.info("Saving product like request: {} with id {}", productLikeRequestDto, clientIdOfHeader);

        Long productId = productLikeRequestDto.productId();

        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundIdException(TYPE, productId));

        if (productLikeRepository.existsByClientIdAndProduct(clientIdOfHeader, product)){
            throw new DuplicateException("Product Like");
        }else {
            productLikeRepository.save(ProductLike.builder()
                    .clientId(clientIdOfHeader)
                    .product(product)
                    .build());
            return new ProductLikeResponseDto();
        }
    }


    @Override
    public ProductLikeResponseDto deleteProductLike(Long clientIdOfHeader, Long productId) {
        if (clientIdOfHeader ==- 1){
            throw new XUserIdNotFoundException();
        }
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundIdException(TYPE, productId));
        if (!productLikeRepository.existsByClientIdAndProduct(clientIdOfHeader, product)){
            throw new NotFoundIdException("Product Like", clientIdOfHeader);
        }else {
            ProductLike productLike = productLikeRepository.findByClientIdAndProduct(clientIdOfHeader, product);
            productLikeRepository.delete(productLike);
            return new ProductLikeResponseDto();
        }
    }


    @Override
    public ProductUpdateResponseDto updateProductState(ProductStateUpdateRequestDto productStateUpdateRequestDto) {
        log.info("update state called, dto : {}", productStateUpdateRequestDto);
        long result = querydslRepository.setProductState(productStateUpdateRequestDto.productId(), productStateUpdateRequestDto.productState());
        if (result != 1){
            throw new NotFoundIdException(TYPE, productStateUpdateRequestDto.productId());
        }else {
            return new ProductUpdateResponseDto(LocalDateTime.now());
        }
    }



    @RabbitListener(queues = "${rabbit.inventory.decrease.queue.name}")
    @Override
    public void decreaseProductInventoryQueue(String message){
        InventoryDecreaseRequestDto inventoryDecreaseRequestDto;
        log.info("Trying decrease product inventory : {}", message);
        try {
            inventoryDecreaseRequestDto = objectMapper.readValue(message, InventoryDecreaseRequestDto.class);
            long updatedRow = querydslRepository.decreaseProductInventory(inventoryDecreaseRequestDto);
            if (updatedRow != inventoryDecreaseRequestDto.decreaseInfo().size()){
                log.warn("Decreasing product inventory succeeded, but there were issues with some items. For example, the request might contain non-existing product IDs. OrderId : {}", inventoryDecreaseRequestDto.orderId());
            }else {
                log.info("Decreasing product inventory succeeded as requested");
            }
        } catch (JsonProcessingException | JpaSystemException e) {
            log.error("Decreasing product inventory failed, error message : {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbit.inventory.decrease.dlq.queue.name}")
    void saveDecreaseDlqLog(String message){
        log.error("Decreasing inventory finally failed processing rabbitMq message - {}", message);
    }

    @RabbitListener(queues = "${rabbit.inventory.increase.queue.name}")
    @Override
    public void increaseProductInventoryQueue(String message){
        List<InventoryIncreaseRequestDto> inventoryIncreaseRequestDtoList;
        log.info("increase product inventory queue: {}", message);
        try {
            inventoryIncreaseRequestDtoList = objectMapper.readValue(message, new TypeReference<List<InventoryIncreaseRequestDto>>() {});
            long updatedRow = querydslRepository.increaseProductInventory(inventoryIncreaseRequestDtoList);
            if (updatedRow != inventoryIncreaseRequestDtoList.size()){
                log.warn("Increasing product inventory succeeded, but there were issues with some items. For example, the request might contain non-existing product IDs. updatedRow: {}, size: {}", updatedRow, inventoryIncreaseRequestDtoList.size());
            }else {
                log.info("Increasing product inventory succeeded as requested");
            }
        } catch (JsonProcessingException  | JpaSystemException e) {
            log.error("Increasing product inventory failed, error message : {}", e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbit.inventory.increase.dlq.queue.name}")
    public void saveIncreaseDlqLog(String message){
        log.error("Increasing inventory finally failed processing rabbitMq message - {}", message);
    }

    @Override
    public ResponseEntity<Void> setProductInventory(InventorySetRequestDto inventorySetRequestDto) {
        long updatedRow = querydslRepository.setProductInventory(inventorySetRequestDto);
        if (updatedRow != 1){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
