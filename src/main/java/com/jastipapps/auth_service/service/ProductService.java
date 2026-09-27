package com.jastipapps.auth_service.service;

import com.jastipapps.auth_service.dto.ProductDtos.*;
import com.jastipapps.auth_service.entity.Product;
import com.jastipapps.auth_service.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;

    public Page<ProductResponse> listProducts(String status, String search, String kategori, Pageable pageable) {
        String effectiveStatus = (status != null && !status.isBlank()) ? status : "tersedia";
        Page<Product> products = productRepository.searchProducts(effectiveStatus, search, kategori, pageable);
        return products.map(this::toResponse);
    }

    public ProductResponse createProduct(CreateProductRequest req, UUID userId) {
        Product product = new Product();
        product.setNamaProduk(req.namaProduk());
        product.setDeskripsi(req.deskripsi());
        product.setKategori(req.kategori());
        product.setFotoUrl(req.fotoUrl());
        product.setSumber(req.sumber());
        product.setHargaAsli(req.hargaAsli());
        product.setBiayaJasa(req.biayaJasa());
        product.setKuota(req.kuota() != null ? req.kuota() : 0);
        product.setStatus("tersedia");
        product.setCreatedBy(userId);
        product.setCreatedAt(OffsetDateTime.now());
        product.setUpdatedAt(OffsetDateTime.now());

        Product saved = productRepository.save(product);

        // Ambil ulang dari DB supaya hargaTotal (generated column) ikut terisi
        Product refreshed = productRepository.findById(saved.getId()).orElseThrow();
        return toResponse(refreshed);
    }

    public ProductResponse getProduct(UUID id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan"));
        return toResponse(product);
    }

    private ProductResponse toResponse(Product p) {
        return new ProductResponse(
                p.getId(), p.getNamaProduk(), p.getDeskripsi(), p.getKategori(),
                p.getFotoUrl(), p.getSumber(), p.getHargaAsli(), p.getBiayaJasa(),
                p.getHargaTotal(), p.getStatus(), p.getKuota()
        );
    }
        public ProductResponse updateProduct(UUID id, CreateProductRequest req) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan"));

        product.setNamaProduk(req.namaProduk());
        product.setDeskripsi(req.deskripsi());
        product.setKategori(req.kategori());
        product.setFotoUrl(req.fotoUrl());
        product.setSumber(req.sumber());
        product.setHargaAsli(req.hargaAsli());
        product.setBiayaJasa(req.biayaJasa());
        product.setKuota(req.kuota() != null ? req.kuota() : product.getKuota());
        product.setUpdatedAt(java.time.OffsetDateTime.now());

        productRepository.save(product);
        Product refreshed = productRepository.findById(id).orElseThrow();
        return toResponse(refreshed);
    }

    public void updateStatus(UUID id, String status) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan"));
        product.setStatus(status);
        product.setUpdatedAt(java.time.OffsetDateTime.now());
        productRepository.save(product);
    }

    public void deleteProduct(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalArgumentException("Produk tidak ditemukan");
        }
        productRepository.deleteById(id);
    }
}