package com.shopflow.service;

import com.shopflow.model.Cart;
import com.shopflow.model.CartItem;
import com.shopflow.model.Product;
import com.shopflow.model.User;
import com.shopflow.repository.CartItemRepository;
import com.shopflow.repository.CartRepository;
import com.shopflow.repository.ProductRepository;
import com.shopflow.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    public CartService(
            CartRepository cartRepository,
            CartItemRepository cartItemRepository,
            ProductRepository productRepository,
            UserRepository userRepository
    ) {
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.userRepository = userRepository;
    }

    public Cart getCart(Long userId) {

        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new RuntimeException("User not found")
                );

        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart cart = Cart.builder()
                            .user(user)
                            .build();

                    return cartRepository.save(cart);
                });
    }

    public Cart addProduct(
            Long userId,
            Long productId,
            Integer quantity
    ) {

        if (quantity == null || quantity <= 0) {
            throw new RuntimeException(
                    "Quantity must be greater than 0"
            );
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() ->
                        new RuntimeException("Product not found")
                );

        if (!Boolean.TRUE.equals(product.getIsActive())) {
            throw new RuntimeException(
                    "Product is not active"
            );
        }

        if (quantity > product.getStockQuantity()) {
            throw new RuntimeException(
                    "Not enough product in stock"
            );
        }

        Cart cart = getCart(userId);

        CartItem existingItem = cart.getItems()
                .stream()
                .filter(item ->
                        item.getProduct()
                                .getId()
                                .equals(productId)
                )
                .findFirst()
                .orElse(null);

        if (existingItem != null) {

            int newQuantity =
                    existingItem.getQuantity() + quantity;

            if (newQuantity > product.getStockQuantity()) {
                throw new RuntimeException(
                        "Not enough product in stock"
                );
            }

            existingItem.setQuantity(newQuantity);

            cartItemRepository.save(existingItem);

        } else {

            CartItem cartItem = CartItem.builder()
                    .cart(cart)
                    .product(product)
                    .quantity(quantity)
                    .build();

            cart.getItems().add(cartItem);

            cartItemRepository.save(cartItem);
        }

        return cart;
    }
    public Cart updateQuantity(
            Long userId,
            Long itemId,
            Integer quantity
    ) {
        if (quantity == null || quantity <= 0) {
            throw new RuntimeException("Quantity must be greater than 0");
        }

        Cart cart = getCart(userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("This item does not belong to this cart");
        }

        Product product = item.getProduct();

        if (quantity > product.getStockQuantity()) {
            throw new RuntimeException("Not enough product in stock");
        }

        item.setQuantity(quantity);
        cartItemRepository.save(item);

        return cart;
    }

    public Cart removeItem(
            Long userId,
            Long itemId
    ) {
        Cart cart = getCart(userId);

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (!item.getCart().getId().equals(cart.getId())) {
            throw new RuntimeException("This item does not belong to this cart");
        }

        cart.getItems().remove(item);
        cartItemRepository.delete(item);

        return cart;
    }

    public Cart clearCart(Long userId) {

        Cart cart = getCart(userId);

        cartItemRepository.deleteAll(cart.getItems());
        cart.getItems().clear();

        return cart;
    }
}