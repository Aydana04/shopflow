package com.shopflow.service;

import com.shopflow.model.Cart;
import com.shopflow.model.CartItem;
import com.shopflow.model.Order;
import com.shopflow.model.Product;
import com.shopflow.model.Role;
import com.shopflow.model.User;

import com.shopflow.repository.CartItemRepository;
import com.shopflow.repository.CartRepository;
import com.shopflow.repository.OrderRepository;
import com.shopflow.repository.ProductRepository;
import com.shopflow.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.*;

public class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private UserRepository userRepository;

    private OrderService orderService;

    @BeforeEach
    void setUp() {

        MockitoAnnotations.openMocks(this);

        orderService = new OrderService(
                orderRepository,
                cartRepository,
                cartItemRepository,
                productRepository,
                userRepository
        );
    }

    @Test
    void shouldCheckoutSuccessfully() {

        User user = User.builder()
                .id(1L)
                .email("test@shopflow.com")
                .password("password")
                .role(Role.CUSTOMER)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("MacBook Air")
                .description("Apple laptop")
                .price(new BigDecimal("1299.99"))
                .stockQuantity(10)
                .isActive(true)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>())
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(3)
                .build();

        cart.getItems().add(cartItem);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        Order result = orderService.checkout(1L);

        assertNotNull(result);

        assertEquals(
                new BigDecimal("3899.97"),
                result.getTotalAmount()
        );

        assertEquals(
                1,
                result.getItems().size()
        );

        assertEquals(
                3,
                result.getItems().get(0).getQuantity()
        );

        assertEquals(
                new BigDecimal("1299.99"),
                result.getItems().get(0).getPriceAtPurchase()
        );

        assertEquals(
                7,
                product.getStockQuantity()
        );

        assertEquals(
                0,
                cart.getItems().size()
        );

        verify(userRepository).findById(1L);

        verify(cartRepository).findByUser(user);

        verify(productRepository)
                .findByIdForUpdate(1L);

        verify(orderRepository)
                .save(any(Order.class));

        verify(cartItemRepository)
                .deleteAll(anyList());
    }

    @Test
    void shouldThrowExceptionWhenCartIsEmpty() {

        User user = User.builder()
                .id(1L)
                .email("test@shopflow.com")
                .password("password")
                .role(Role.CUSTOMER)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>())
                .build();

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> orderService.checkout(1L)
                );

        assertEquals(
                "Cart is empty",
                exception.getMessage()
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenStockIsNotEnough() {

        User user = User.builder()
                .id(1L)
                .email("test@shopflow.com")
                .password("password")
                .role(Role.CUSTOMER)
                .build();

        Product product = Product.builder()
                .id(1L)
                .name("MacBook Air")
                .price(new BigDecimal("1299.99"))
                .stockQuantity(2)
                .isActive(true)
                .build();

        Cart cart = Cart.builder()
                .id(1L)
                .user(user)
                .items(new ArrayList<>())
                .build();

        CartItem cartItem = CartItem.builder()
                .id(1L)
                .cart(cart)
                .product(product)
                .quantity(3)
                .build();

        cart.getItems().add(cartItem);

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        when(cartRepository.findByUser(user))
                .thenReturn(Optional.of(cart));

        when(productRepository.findByIdForUpdate(1L))
                .thenReturn(Optional.of(product));

        RuntimeException exception =
                assertThrows(
                        RuntimeException.class,
                        () -> orderService.checkout(1L)
                );

        assertEquals(
                "Not enough stock for: MacBook Air",
                exception.getMessage()
        );

        assertEquals(
                2,
                product.getStockQuantity()
        );

        verify(orderRepository, never())
                .save(any(Order.class));
    }
}