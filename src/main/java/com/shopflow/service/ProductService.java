package com.shopflow.service;

import com.shopflow.model.Category;
import com.shopflow.model.Product;
import com.shopflow.repository.CategoryRepository;
import com.shopflow.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository,
                          CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public Product create(Product product, Long categoryId) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setCategory(category);

        return productRepository.save(product);
    }

    public Page<Product> getAll(Pageable pageable) {
        return productRepository.findAll(pageable);
    }

    public Page<Product> searchByName(
            String name,
            Pageable pageable
    ) {
        return productRepository.findByNameContainingIgnoreCase(
                name,
                pageable
        );
    }

    public Page<Product> filterByActive(
            Boolean isActive,
            Pageable pageable
    ) {
        return productRepository.findByIsActive(
                isActive,
                pageable
        );
    }

    public Product getById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));
    }

    public Product update(
            Long id,
            Product updatedProduct,
            Long categoryId
    ) {

        Product product = getById(id);

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new RuntimeException("Category not found"));

        product.setName(updatedProduct.getName());
        product.setDescription(updatedProduct.getDescription());
        product.setPrice(updatedProduct.getPrice());
        product.setStockQuantity(updatedProduct.getStockQuantity());
        product.setIsActive(updatedProduct.getIsActive());
        product.setCategory(category);

        return productRepository.save(product);
    }

    public void delete(Long id) {
        Product product = getById(id);
        productRepository.delete(product);
    }
}