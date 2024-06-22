package com.nhnacademy.bookstoreinjun.service;

import com.nhnacademy.bookstoreinjun.dto.packaging.PackageInfoResponseDto;
import com.nhnacademy.bookstoreinjun.dto.packaging.PackageInsertRequestDto;
import com.nhnacademy.bookstoreinjun.dto.packaging.PackageUpdateRequestDto;
import com.nhnacademy.bookstoreinjun.entity.Packaging;
import com.nhnacademy.bookstoreinjun.repository.PackagingRepository;
import com.nhnacademy.bookstoreinjun.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PackagingService {
    private final PackagingRepository packageRepository;
    private final ProductRepository productRepository;

    public PackageInfoResponseDto getPackageInfoById(Long packageId) {
        Packaging packaging = packageRepository.findById(packageId).orElse(null);
        if (packaging == null) {
            return null;
        }
        return new PackageInfoResponseDto(packaging.getPackageId(), packaging.getPackageName(), packaging.getProduct());
    }

    public PackageInfoResponseDto getPackageInfoByProductId(Long productId) {
        Packaging packaging = packageRepository.findByProduct_ProductId(productId).orElse(null);
        if (packaging == null) {
            return null;
        }
        return new PackageInfoResponseDto(packaging.getPackageId(), packaging.getPackageName(), packaging.getProduct());
    }

    public PackageInfoResponseDto getPackageInfoByPackageName(String PackageName) {
        Packaging packaging = packageRepository.findByPackageName(PackageName).orElse(null);
        if (packaging == null) {
            return null;
        }
        return new PackageInfoResponseDto(packaging.getPackageId(), packaging.getPackageName(), packaging.getProduct());
    }

    public boolean updatePackageInfo(PackageUpdateRequestDto packaging) {
        if (!packageRepository.findById(packaging.getPackageId()).isPresent()
                || !productRepository.findById(packaging.getProductId()).isPresent()) {
            return false;
        }
        packageRepository.save(Packaging.builder()
                .packageId(packaging.getPackageId())
                .packageName(packaging.getPackageName())
                .product(productRepository.findByProductId(packaging.getProductId()))
                .build());
        return true;
    }

    public boolean registerPackage(PackageInsertRequestDto packaging) {
        if (!packageRepository.findById(packaging.getPackageId()).isPresent()
                || !productRepository.findById(packaging.getProductId()).isPresent()) {
            return false;
        }
        packageRepository.save(Packaging.builder()
                .packageId(packaging.getPackageId())
                .packageName(packaging.getPackageName())
                .product(productRepository.findByProductId(packaging.getProductId()))
                .build());
        return true;
    }

    public List<PackageInfoResponseDto> getAllPackages() {
        return packageRepository.findAll().stream()
                .map(packaging -> new PackageInfoResponseDto(
                        packaging.getPackageId(),
                        packaging.getPackageName(),
                        packaging.getProduct()
                ))
                .collect(Collectors.toList());
    }

    public Page<PackageInfoResponseDto> getPackagesPage(int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by("packageId").descending());
        return packageRepository.findAll(pageRequest).map(packaging -> new PackageInfoResponseDto(
                packaging.getPackageId(),
                packaging.getPackageName(),
                packaging.getProduct()
        ));
    }
}
