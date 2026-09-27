package com.jastipapps.auth_service.controller;

import com.jastipapps.auth_service.dto.ProductDtos.*;
import com.jastipapps.auth_service.security.JwtUtil;
import com.jastipapps.auth_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final JwtUtil jwtUtil;

    // GET /api/products?status=tersedia&page=0&size=10
    @GetMapping
    public ResponseEntity<Page<ProductResponse>> listProducts(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String kategori,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(productService.listProducts(status, search, kategori, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProduct(@PathVariable UUID id) {
        return ResponseEntity.ok(productService.getProduct(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(
            @Valid @RequestBody CreateProductRequest req,
            @RequestHeader("Authorization") String authHeader
    ) {
        String token = authHeader.replace("Bearer ", "");
        UUID userId = UUID.fromString(jwtUtil.extractUserId(token));
        return ResponseEntity.ok(productService.createProduct(req, userId));
    }

        @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(
            @PathVariable UUID id,
            @Valid @RequestBody CreateProductRequest req
    ) {
        return ResponseEntity.ok(productService.updateProduct(id, req));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<String> updateStatus(
            @PathVariable UUID id,
            @RequestBody java.util.Map<String, String> body
    ) {
        productService.updateStatus(id, body.get("status"));
        return ResponseEntity.ok("Status berhasil diubah");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable UUID id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Produk berhasil dihapus");
    }
}