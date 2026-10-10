package com.shopflow.service;

import com.shopflow.model.Category;
import com.shopflow.model.Product;
import com.shopflow.repository.CategoryRepository;
import com.shopflow.repository.ProductRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        productService = new ProductService(
                productRepository,
                categoryRepository
        );
    }

    @Test
    void shouldCreateProduct() {

        Category category = Category.builder()
                .id(1L)
                .name("Electronics")
                .build();

        Product product = Product.builder()
                .name("MacBook Air")
                .description("Apple laptop")
                .price(new BigDecimal("1299.99"))
                .stockQuantity(10)
                .isActive(true)
                .build();

        when(categoryRepository.findById(1L))
                .thenReturn(Optional.of(category));

        when(productRepository.save(any(Product.class)))
                .thenAnswer(invocation ->
                        invocation.getArgument(0)
                );

        Product result =
                productService.create(product, 1L);

        assertNotNull(result);
        assertEquals("MacBook Air", result.getName());
        assertEquals(category, result.getCategory());

        verify(categoryRepository).findById(1L);
        verify(productRepository).save(product);
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {

        Product product = Product.builder()
                .name("MacBook Air")
                .price(new BigDecimal("1299.99"))
                .stockQuantity(10)
                .isActive(true)
                .build();

        when(categoryRepository.findById(99L))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> productService.create(product, 99L)
        );

        assertEquals(
                "Category not found",
                exception.getMessage()
        );

        verify(productRepository, never())
                .save(any(Product.class));
    }
}