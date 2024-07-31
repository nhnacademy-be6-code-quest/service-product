package com.nhnacademy.bookstoreinjun.service.cart;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nhnacademy.bookstoreinjun.dto.cart.CartCheckoutRequestDto;
import com.nhnacademy.bookstoreinjun.dto.cart.CartRequestDto;
import com.nhnacademy.bookstoreinjun.dto.cart.CartGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.cart.SaveCartResponseDto;
import com.nhnacademy.bookstoreinjun.entity.Cart;
import com.nhnacademy.bookstoreinjun.entity.Product;
import com.nhnacademy.bookstoreinjun.exception.NotFoundIdException;
import com.nhnacademy.bookstoreinjun.exception.XUserIdNotFoundException;
import com.nhnacademy.bookstoreinjun.repository.QuerydslRepository;
import com.nhnacademy.bookstoreinjun.repository.CartRepository;
import com.nhnacademy.bookstoreinjun.repository.ProductRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;

    private final ProductRepository productRepository;

    private final QuerydslRepository querydslRepository;

    private final ObjectMapper objectMapper;

    private static final String PRODUCT ="product";

    private Product getProduct(Long clientIdOfHeader, CartRequestDto cartRequestDto) {
        if (clientIdOfHeader == -1){
            throw new XUserIdNotFoundException();
        }
        Long productId = cartRequestDto.productId();
        return productRepository.findById(productId).orElseThrow(() -> new NotFoundIdException(PRODUCT, productId));
    }

    private Long getQuantity(List<Cart> cartList){
        if (cartList == null || cartList.isEmpty()) {
            return 0L;
        }else {
            return cartList.stream().map(Cart::getQuantity).reduce(0L, Long::sum);
        }
    }

    @Override
    public List<CartRequestDto> restoreClientCartList(Long clientIdOfHeader) {
        if (clientIdOfHeader == -1){
            throw new XUserIdNotFoundException();
        }
        log.info("Restore client cart list with id {}", clientIdOfHeader);
        List<Cart> cartList = cartRepository.findAllByClientIdAndCartRemoveTypeIsNull(clientIdOfHeader);

        Map<Long, Long> productQuantityMap = new HashMap<>();
        for (Cart cart : cartList) {
            Long productId = cart.getProduct().getProductId();
            Long currentQuantity = productQuantityMap.getOrDefault(productId, 0L);
            productQuantityMap.put(productId, currentQuantity + cart.getQuantity());
        }

        List<CartRequestDto> result = new ArrayList<>();
        for (Map.Entry<Long, Long> entry : productQuantityMap.entrySet()) {
            result.add(CartRequestDto.builder()
                    .productId(entry.getKey())
                    .quantity(entry.getValue())
                    .build());
        }

        return result;
    }

    @Override
    public SaveCartResponseDto addClientCartItem(Long clientIdOfHeader, CartRequestDto cartRequestDto) {
        if (clientIdOfHeader == -1){
            throw new XUserIdNotFoundException();
        }
        Product product = getProduct(clientIdOfHeader, cartRequestDto);
        long productInventory = product.getProductInventory();

        Long productId = cartRequestDto.productId();
        List<Cart> cartList = cartRepository.findByClientIdAndProduct_ProductIdAndCartRemoveTypeIsNull(clientIdOfHeader, productId);

        Long quantity = Math.min(cartRequestDto.quantity(), productInventory - getQuantity(cartList));

        cartRepository.save(Cart.builder()
                .clientId(clientIdOfHeader)
                .product(product)
                .quantity(quantity)
                .addedToCartAt(LocalDateTime.now())
                .build());
        return new SaveCartResponseDto(quantity);
    }

    @Override
    public SaveCartResponseDto checkCartRequestOfGuest(CartRequestDto cartRequestDto){
        Product product = productRepository.findById(cartRequestDto.productId()).orElseThrow(() -> new NotFoundIdException(PRODUCT, cartRequestDto.productId()));
        return new SaveCartResponseDto(Math.min(cartRequestDto.quantity(), product.getProductInventory()));
    }


    @Override
    public SaveCartResponseDto setClientCartItemQuantity(Long clientIdOfHeader, CartRequestDto cartRequestDto) {
        if (clientIdOfHeader == -1){
            throw new XUserIdNotFoundException();
        }
        Product product = getProduct(clientIdOfHeader, cartRequestDto);

        Long productId = cartRequestDto.productId();
        List<Cart> cartList = cartRepository.findByClientIdAndProduct_ProductIdAndCartRemoveTypeIsNull(clientIdOfHeader, productId);

        long productInventory = product.getProductInventory();

        Long quantity = Math.min(cartRequestDto.quantity() - getQuantity(cartList), productInventory - getQuantity(cartList));
        cartRepository.save(Cart.builder()
                .clientId(clientIdOfHeader)
                .product(product)
                .quantity(quantity)
                .addedToCartAt(LocalDateTime.now())
                .build());
        return new SaveCartResponseDto(quantity);
    }

    @Override
    public void deleteCartItem(Long clientIdOfHeader, Long productId) {
        log.info("trying delete cart item with client : {}, product : {}", clientIdOfHeader, productId);
        if (clientIdOfHeader == -1){
            throw new XUserIdNotFoundException();
        } else if (querydslRepository.deleteCartItem(clientIdOfHeader, productId)){
            log.info("delete cart item success");
        } else{
            throw new NotFoundIdException(PRODUCT, productId);
        }
    }

    @Override
    public void clearAllCart(Long clientIdOfHeader) {
        if (clientIdOfHeader == -1){
            throw new XUserIdNotFoundException();
        } else if (querydslRepository.deleteAllCart(clientIdOfHeader)){
            log.info("Clear cart succeeded with clientId {}", clientIdOfHeader);
        } else{
            log.warn("Clear cart succeeded with clientId {}, but no columns were updated. There may be an issue, such as the cart already being empty.", clientIdOfHeader);
        }
    }

    @RabbitListener(queues = "${rabbit.cart.checkout.queue.name}")
    @Override
    public void checkOutCart(String message) {
        log.info("Trying checkout cart - message : {}", message);
        try {
            CartCheckoutRequestDto requestDto = objectMapper.readValue(message, CartCheckoutRequestDto.class);
            List<Long> productIdList = requestDto.productIdList();
            long updatedRow = querydslRepository.checkOutCart(requestDto);
            if (updatedRow == productIdList.size()){
                log.info("Checkout cart succeeded as requested");
            }else {
                log.warn("Checkout cart succeeded, but there were issues with some items. For example, the request might contain non-existing product IDs. checked out : {}, request: {}", updatedRow, productIdList.size());
            }
        }catch (JsonProcessingException e){
            log.warn("Invalid rabbit mq message for json processing - {}, Caused by {}", message, e.getMessage());
        }
    }

    @RabbitListener(queues = "${rabbit.cart.checkout.dlq.queue.name}")
    public void saveDlqLog(String message){
        log.error("Checkout cart finally failed processing rabbitMq message - {}", message);
    }



    @Override
    public List<CartGetResponseDto> getClientCart(Long clientIdOfHeader) {
        if (clientIdOfHeader == -1){
            throw new XUserIdNotFoundException();
        }else{
            return querydslRepository.getClientCart(clientIdOfHeader);
        }
    }

    @Override
    public List<CartGetResponseDto> getGuestCart(List<CartRequestDto> cartRequestDtoList) {
        return querydslRepository.getGuestCart(cartRequestDtoList);
    }
}
