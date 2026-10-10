package com.shopflow.controller;

import com.shopflow.model.Product;
import com.shopflow.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping
    public ResponseEntity<Product> create(
            @RequestParam Long categoryId,
            @RequestBody Product product
    ) {
        return ResponseEntity.ok(
                productService.create(product, categoryId)
        );
    }

    @GetMapping
    public ResponseEntity<Page<Product>> getAll(
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                productService.getAll(pageable)
        );
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Product>> searchByName(
            @RequestParam String name,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                productService.searchByName(name, pageable)
        );
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<Product>> filterByActive(
            @RequestParam Boolean isActive,
            Pageable pageable
    ) {
        return ResponseEntity.ok(
                productService.filterByActive(isActive, pageable)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getById(
            @PathVariable Long id
    ) {
        return ResponseEntity.ok(
                productService.getById(id)
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<Product> update(
            @PathVariable Long id,
            @RequestParam Long categoryId,
            @RequestBody Product product
    ) {
        return ResponseEntity.ok(
                productService.update(id, product, categoryId)
        );
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id
    ) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}