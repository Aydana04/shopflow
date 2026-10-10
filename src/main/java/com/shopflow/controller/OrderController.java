package com.shopflow.controller;

import com.shopflow.model.Order;
import com.shopflow.model.OrderStatus;
import com.shopflow.model.User;
import com.shopflow.repository.UserRepository;
import com.shopflow.service.OrderService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserRepository userRepository;

    public OrderController(
            OrderService orderService,
            UserRepository userRepository
    ) {
        this.orderService = orderService;
        this.userRepository = userRepository;
    }

    private Long getCurrentUserId(Authentication authentication) {

        String email = authentication.getName();

        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return user.getId();
    }


    @PostMapping("/checkout")
    public ResponseEntity<Order> checkout(
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(
                orderService.checkout(userId)
        );
    }


    @GetMapping("/my")
    public ResponseEntity<List<Order>> getMyOrders(
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(
                orderService.getOrdersByUser(userId)
        );
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {

        return ResponseEntity.ok(
                orderService.getAllOrders()
        );
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<Order> getById(
            @PathVariable Long id
    ) {

        return ResponseEntity.ok(
                orderService.getById(id)
        );
    }


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}/status")
    public ResponseEntity<Order> updateStatus(
            @PathVariable Long id,
            @RequestParam OrderStatus status
    ) {

        return ResponseEntity.ok(
                orderService.updateStatus(id, status)
        );
    }
}