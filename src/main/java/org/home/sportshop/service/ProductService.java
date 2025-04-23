package org.home.sportshop.service;

import java.util.List;

import org.home.sportshop.logging.LoggingService;
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
    private final LoggingService logger = LoggingService.getInstance();

    @Autowired
    public ProductService(ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        logger.logInfo("ProductService инициализирован");
    }

    public List<Product> getAllProducts() {
        logger.logInfo("Запрос на получение всех продуктов");
        List<Product> products = productRepository.findAll(Sort.by(Sort.Direction.DESC, "updatedAt"));
        logger.logInfo("Получено продуктов: " + products.size());
        return products;
    }

    public Product addProduct(Product product) {
        logger.logInfo("Добавление нового продукта: " + product.getName());
        Product savedProduct = productRepository.save(product);
        logger.logInfo("Продукт успешно добавлен с ID: " + savedProduct.getId());
        return savedProduct;
    }

    public Product updateProduct(Long id, Product productDetails) {
        logger.logInfo("Обновление продукта с ID: " + id);
        Product product = productRepository.findById(id).orElseThrow(() -> {
            String errorMessage = "Продукт с ID: " + id + " не найден";
            logger.logError(errorMessage);
            return new RuntimeException(errorMessage);
        });
        
        product.setName(productDetails.getName());
        product.setPrice(productDetails.getPrice());
        product.setDescription(productDetails.getDescription());
        product.setImageUrl(productDetails.getImageUrl());
        product.setStock(productDetails.getStock());
        
        Product updatedProduct = productRepository.save(product);
        logger.logInfo("Продукт с ID: " + id + " успешно обновлен");
        return updatedProduct;
    }

    public void deleteProduct(Long id) {
        logger.logWarning("Удаление продукта с ID: " + id);
        Product product = getProductById(id);
        
        // Проверяем, используется ли продукт в заказах
        if (orderItemRepository.existsByProductId(id)) {
            String errorMessage = "Cannot delete product with id " + id + " as it is used in orders";
            logger.logError(errorMessage);
            throw new IllegalStateException(errorMessage);
        }
        
        productRepository.delete(product);
        logger.logInfo("Продукт с ID: " + id + " успешно удален");
    }

    public Product getProductById(Long id) {
        logger.logInfo("Получение продукта с ID: " + id);
        try {
            Product product = productRepository.findById(id).orElseThrow(() -> {
                String errorMessage = "Продукт с ID: " + id + " не найден";
                logger.logError(errorMessage);
                return new RuntimeException(errorMessage);
            });
            logger.logInfo("Продукт с ID: " + id + " успешно найден");
            return product;
        } catch (Exception e) {
            logger.logError("Ошибка при получении продукта с ID: " + id + ": " + e.getMessage());
            throw e;
        }
    }
}