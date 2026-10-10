package com.shopflow.controller;

import com.shopflow.model.Cart;
import com.shopflow.model.User;
import com.shopflow.repository.UserRepository;
import com.shopflow.service.CartService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cart")
public class CartController {

    private final CartService cartService;
    private final UserRepository userRepository;

    public CartController(
            CartService cartService,
            UserRepository userRepository
    ) {
        this.cartService = cartService;
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

    @GetMapping
    public ResponseEntity<Cart> getCart(
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(
                cartService.getCart(userId)
        );
    }

    @PostMapping("/add")
    public ResponseEntity<Cart> addProduct(
            Authentication authentication,
            @RequestParam Long productId,
            @RequestParam Integer quantity
    ) {

        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(
                cartService.addProduct(
                        userId,
                        productId,
                        quantity
                )
        );
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<Cart> updateQuantity(
            Authentication authentication,
            @PathVariable Long itemId,
            @RequestParam Integer quantity
    ) {

        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(
                cartService.updateQuantity(
                        userId,
                        itemId,
                        quantity
                )
        );
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<Cart> removeItem(
            Authentication authentication,
            @PathVariable Long itemId
    ) {

        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(
                cartService.removeItem(
                        userId,
                        itemId
                )
        );
    }

    @DeleteMapping("/clear")
    public ResponseEntity<Cart> clearCart(
            Authentication authentication
    ) {

        Long userId = getCurrentUserId(authentication);

        return ResponseEntity.ok(
                cartService.clearCart(userId)
        );
    }
}