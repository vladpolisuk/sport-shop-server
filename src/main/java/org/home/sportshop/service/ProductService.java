package org.home.sportshop.service;

import java.util.List;

import org.home.sportshop.model.Product;
import org.home.sportshop.repository.OrderItemRepository;
import org.home.sportshop.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class ProductService {
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
    }

    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Long id, Product productDetails) {
        Product product = productRepository.findById(id).orElseThrow();
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setDescription(productDetails.getDescription());
        product.setImageUrl(productDetails.getImageUrl());
        product.setStock(productDetails.getStock());
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        Product product = getProductById(id);
        
        // Проверяем, используется ли продукт в заказах
        if (orderItemRepository.existsByProductId(id)) {
            throw new IllegalStateException("Cannot delete product with id " + id + " as it is used in orders");
        }
        
        productRepository.delete(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).orElseThrow();
    }
}