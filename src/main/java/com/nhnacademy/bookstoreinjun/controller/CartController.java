package com.nhnacademy.bookstoreinjun.controller;

import com.nhnacademy.bookstoreinjun.dto.cart.CartRequestDto;
import com.nhnacademy.bookstoreinjun.dto.cart.CartGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.cart.SaveCartResponseDto;
import com.nhnacademy.bookstoreinjun.service.cart.CartService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.math.NumberUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class CartController implements CartControllerInterface{
    private final CartService cartService;

    private final HttpHeaders header;

    private static final String ID_HEADER = "X-User-Id";


    @GetMapping("/client/cart/restore")
    public ResponseEntity<List<CartRequestDto>> restoreClientCartList(
            @RequestHeader HttpHeaders httpHeaders) {
        return new ResponseEntity<>(cartService.restoreClientCartList(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L)), header, HttpStatus.OK);
    }

    @GetMapping("/client/cart")
    public ResponseEntity<List<CartGetResponseDto>> getClientCartList(
            @RequestHeader HttpHeaders httpHeaders) {
        return new ResponseEntity<>(cartService.getClientCart(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L)), header, HttpStatus.OK);
    }

    @PostMapping("/guest/cart")
    public ResponseEntity<List<CartGetResponseDto>> getGuestCartList(
            @RequestBody List<@Valid CartRequestDto> cartRequestDtoList) {
        return new ResponseEntity<>(cartService.getGuestCart(cartRequestDtoList), header, HttpStatus.OK);
    }


    @PostMapping("/client/cart/add")
    public ResponseEntity<SaveCartResponseDto> addClientCartItem(
            @RequestHeader HttpHeaders httpHeaders,
            @RequestBody @Valid CartRequestDto cartRequestDto){
        return new ResponseEntity<>(cartService.addClientCartItem(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), cartRequestDto), header, HttpStatus.OK);
    }


    @PostMapping("/guest/cart/add")
    public ResponseEntity<SaveCartResponseDto> addGuestCartItem(
            @RequestBody @Valid CartRequestDto cartRequestDto){
        return new ResponseEntity<>(cartService.checkCartRequestOfGuest(cartRequestDto), header, HttpStatus.OK);
    }

    @PutMapping("/client/cart/update")
    public ResponseEntity<SaveCartResponseDto> updateClientCartItem(
            @RequestHeader HttpHeaders httpHeaders,
            @RequestBody @Valid CartRequestDto cartRequestDto
    ){
        return new ResponseEntity<>(cartService.setClientCartItemQuantity(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), cartRequestDto), header, HttpStatus.OK);
    }

    @PutMapping("/guest/cart/update")
    public ResponseEntity<SaveCartResponseDto> updateGuestCartItem(
            @RequestBody @Valid CartRequestDto cartRequestDto){
        return new ResponseEntity<>(cartService.checkCartRequestOfGuest(cartRequestDto), header, HttpStatus.OK);
    }

    @DeleteMapping("/client/cart/items/{productId}")
    public ResponseEntity<Void> deleteCartItem(
            @RequestHeader HttpHeaders httpHeaders,
            @PathVariable Long productId){
        cartService.deleteCartItem(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L), productId);
        return new ResponseEntity<>(null, header, HttpStatus.OK);
    }

    @DeleteMapping("/client/cart/all")
    public ResponseEntity<Void> clearAllCart(
            @RequestHeader HttpHeaders httpHeaders
    ){
        cartService.clearAllCart(NumberUtils.toLong(httpHeaders.getFirst(ID_HEADER), -1L));
        return new ResponseEntity<>(null, header, HttpStatus.OK);
    }
}
