package com.nhnacademy.bookstoreinjun.controller;

import com.nhnacademy.bookstoreinjun.dto.packaging.PackagingGetResponseDto;
import com.nhnacademy.bookstoreinjun.dto.packaging.PackagingRegisterRequestDto;
import com.nhnacademy.bookstoreinjun.dto.packaging.PackagingUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductRegisterResponseDto;
import com.nhnacademy.bookstoreinjun.dto.product.ProductUpdateResponseDto;
import com.nhnacademy.bookstoreinjun.service.packaging.PackagingService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
public class PackagingController {

    private final PackagingService packagingService;

    private final HttpHeaders header;


    @GetMapping("/packaging/single/byProduct/{productId}")
    public ResponseEntity<PackagingGetResponseDto> getPackageInfoByProductId(@PathVariable("productId") Long productId) {
        return new ResponseEntity<>(packagingService.getPackageInfoByProductId(productId), header, HttpStatus.OK);
    }

    @PostMapping("/admin/packaging/register")
    public ResponseEntity<ProductRegisterResponseDto> insertPackage(@Valid @RequestBody PackagingRegisterRequestDto req) {
        return new ResponseEntity<>(packagingService.registerPackage(req), header, HttpStatus.CREATED);
    }

    @PutMapping("/admin/packaging/update")
    public ResponseEntity<ProductUpdateResponseDto> updatePackage(@Valid @RequestBody PackagingUpdateRequestDto req) {
        return new ResponseEntity<>(packagingService.updatePackageInfo(req), header, HttpStatus.OK);
    }

    @GetMapping("/packaging/all")
    public ResponseEntity<List<PackagingGetResponseDto>> getAllPackage(
            @RequestParam(name = "productState", required = false) Integer productState
    ) {
        return new ResponseEntity<>(packagingService.getAllPackages(productState), header, HttpStatus.OK);
    }

    @GetMapping("/packaging/page")
    public ResponseEntity<Page<PackagingGetResponseDto>> getPackagePage(
            @RequestParam(name = "productState", required = false) Integer productState,
            @Min(1) @RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
        return new ResponseEntity<>(packagingService.getPackagesPage(productState, page, size), header, HttpStatus.OK);
    }

    @GetMapping("/admin/packaging/roleCheck")
    public ResponseEntity<Void> roleCheck(){
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @GetMapping("/admin/packaging/page")
    public ResponseEntity<Page<PackagingGetResponseDto>> getPackagePageForAdmin(
            @RequestParam(name = "productState", required = false) Integer productState,
            @Min(1) @RequestParam(name = "page") int page, @RequestParam(name = "size") int size) {
        return new ResponseEntity<>(packagingService.getPackagesPage(productState, page, size), header, HttpStatus.OK);
    }
}
