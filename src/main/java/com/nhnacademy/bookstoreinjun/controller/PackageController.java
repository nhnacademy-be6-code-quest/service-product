package com.nhnacademy.bookstoreinjun.controller;

import com.nhnacademy.bookstoreinjun.dto.packaging.PackageInfoResponseDto;
import com.nhnacademy.bookstoreinjun.dto.packaging.PackageInsertRequestDto;
import com.nhnacademy.bookstoreinjun.dto.packaging.PackageUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.service.PackagingService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/package")
public class PackageController {

    private final PackagingService packagingService;

    @GetMapping
    public ResponseEntity<PackageInfoResponseDto> getPackageInfoByID(@RequestParam Long packageId) {
        PackageInfoResponseDto info = packagingService.getPackageInfoById(packageId);
        if (info == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(info);
    }

    @GetMapping
    public ResponseEntity<PackageInfoResponseDto> getPackageInfoByName(@RequestParam String packageName) {
        PackageInfoResponseDto info = packagingService.getPackageInfoByPackageName(packageName);
        if (info == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(info);
    }

    @GetMapping
    public ResponseEntity<PackageInfoResponseDto> getPackageInfoByName(@RequestParam Long productId) {
        PackageInfoResponseDto info = packagingService.getPackageInfoByProductId(productId);
        if (info == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(info);
    }

    @PutMapping
    public ResponseEntity<Boolean> updatePackage(@RequestBody PackageUpdateRequestDto req) {
        if (packagingService.updatePackageInfo(req)) {
            return ResponseEntity.ok(true);
        }
        return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    }

    @PostMapping
    public ResponseEntity<Boolean> insertPackage(@RequestBody PackageInsertRequestDto req) {
        if (packagingService.registerPackage(req)) {
            return ResponseEntity.ok(true);
        }
        return new ResponseEntity<>(false, HttpStatus.NOT_FOUND);
    }

    @GetMapping
    public ResponseEntity<List<PackageInfoResponseDto>> getAllPackage() {
        return new ResponseEntity<>(packagingService.getAllPackages(), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<Page<PackageInfoResponseDto>> getAllPackageByProductId(@RequestParam(name = "page") int page, @RequestParam(name = "page") int size) {
        return new ResponseEntity<>(packagingService.getPackagesPage(page, size), HttpStatus.OK);
    }
}
