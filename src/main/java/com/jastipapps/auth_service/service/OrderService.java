package com.jastipapps.auth_service.service;

import com.jastipapps.auth_service.dto.OrderDtos.*;
import com.jastipapps.auth_service.entity.Order;
import com.jastipapps.auth_service.entity.Product;
import com.jastipapps.auth_service.repository.OrderRepository;
import com.jastipapps.auth_service.repository.ProductRepository;
import com.jastipapps.auth_service.repository.UserRepository;
import com.jastipapps.auth_service.entity.User;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    

    public void updateOrderStatus(UUID orderId, String status, String keterangan, String buktiFoto) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order tidak ditemukan"));
        order.setStatus(status);
        order.setKeteranganStatus(keterangan);
        if (buktiFoto != null && !buktiFoto.isBlank()) {
            order.setBuktiFotoUrl(buktiFoto);
        }
        order.setUpdatedAt(OffsetDateTime.now());
        orderRepository.save(order);
    }

    public List<AdminOrderResponse> getAllOrders(String status, String orderIdSearch) {
        List<Order> orders = orderRepository.searchOrders(status, orderIdSearch);

        return orders.stream()
                .map(order -> {
                    Product product = productRepository.findById(order.getProductId()).orElse(null);
                    User user = userRepository.findById(order.getUserId()).orElse(null);
                    return new AdminOrderResponse(
                            order.getId(), order.getProductId(),
                            product != null ? product.getNamaProduk() : "Produk tidak ditemukan",
                            product != null ? product.getFotoUrl() : null,
                            order.getJumlah(), order.getCatatan(), order.getHargaSatuan(),
                            order.getTotalHarga(), order.getStatus(), order.getCreatedAt(),
                            user != null ? user.getFullName() : "User tidak ditemukan",
                            user != null ? user.getEmail() : "-",
                            order.getKeteranganStatus(),
                            order.getBuktiFotoUrl()
                    );
                })
                .toList();
    }


    public OrderResponse createOrder(CreateOrderRequest req, UUID userId) {
        Product product = productRepository.findById(req.productId())
                .orElseThrow(() -> new IllegalArgumentException("Produk tidak ditemukan"));

        if (!"tersedia".equals(product.getStatus())) {
            throw new IllegalArgumentException("Produk sudah tidak tersedia");
        }
        if (req.jumlah() > product.getKuota()) {
            throw new IllegalArgumentException("Jumlah melebihi kuota tersisa (" + product.getKuota() + ")");
        }

        Order order = new Order();
        order.setProductId(product.getId());
        order.setUserId(userId);
        order.setJumlah(req.jumlah());
        order.setCatatan(req.catatan());
        order.setHargaSatuan(product.getHargaTotal());
        order.setStatus("pending");
        order.setCreatedAt(OffsetDateTime.now());
        order.setUpdatedAt(OffsetDateTime.now());

        Order saved = orderRepository.save(order);

        // Kurangi kuota produk
        product.setKuota(product.getKuota() - req.jumlah());
        productRepository.save(product);

        Order refreshed = orderRepository.findById(saved.getId()).orElseThrow();
        return toResponse(refreshed, product);
    }

    public List<OrderResponse> getMyOrders(UUID userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId).stream()
                .map(order -> {
                    Product product = productRepository.findById(order.getProductId()).orElse(null);
                    return toResponse(order, product);
                })
                .toList();
    }

    private OrderResponse toResponse(Order order, Product product) {
        return new OrderResponse(
                order.getId(),
                order.getProductId(),
                product != null ? product.getNamaProduk() : "Produk tidak ditemukan",
                product != null ? product.getFotoUrl() : null,
                order.getJumlah(),
                order.getCatatan(),
                order.getHargaSatuan(),
                order.getTotalHarga(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getKeteranganStatus(),
                order.getBuktiFotoUrl()
        );
    }
}