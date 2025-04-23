package org.home.sportshop.controller;

import java.util.List;

import org.home.sportshop.logging.LoggingService;
import org.home.sportshop.model.Product;
import org.home.sportshop.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/products")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "https://vladpolisuk-sport-shop.vercel.app"}, allowCredentials = "true")
public class ProductController {
    private final ProductService productService;
    private final LoggingService logger = LoggingService.getInstance();

    @Autowired
    public ProductController(ProductService productService) {
        this.productService = productService;
        logger.logInfo("ProductController инициализирован");
    }

    @GetMapping
    public List<Product> getAllProducts() {
        logger.logInfo("Запрос на получение всех продуктов");
        List<Product> products = productService.getAllProducts();
        logger.logInfo("Возвращено продуктов: " + products.size());
        return products;
    }

    @GetMapping("/{id}")
    public Product getProductById(@PathVariable Long id) {
        logger.logInfo("Запрос на получение продукта с ID: " + id);
        try {
            Product product = productService.getProductById(id);
            logger.logInfo("Продукт с ID: " + id + " успешно найден");
            return product;
        } catch (Exception e) {
            logger.logError("Ошибка при получении продукта с ID: " + id + ": " + e.getMessage());
            throw e;
        }
    }

    @PostMapping
    public Product addProduct(@RequestBody Product product) {
        logger.logInfo("Запрос на добавление нового продукта: " + product.getName());
        try {
            Product savedProduct = productService.addProduct(product);
            logger.logInfo("Продукт успешно добавлен с ID: " + savedProduct.getId());
            return savedProduct;
        } catch (Exception e) {
            logger.logError("Ошибка при добавлении продукта: " + e.getMessage());
            throw e;
        }
    }
    
    @PutMapping("/{id}")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        logger.logInfo("Запрос на обновление продукта с ID: " + id);
        try {
            Product updatedProduct = productService.updateProduct(id, product);
            logger.logInfo("Продукт с ID: " + id + " успешно обновлен");
            return updatedProduct;
        } catch (Exception e) {
            logger.logError("Ошибка при обновлении продукта с ID: " + id + ": " + e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        logger.logWarning("Запрос на удаление продукта с ID: " + id);
        try {
            productService.deleteProduct(id);
            logger.logInfo("Продукт с ID: " + id + " успешно удален");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.logError("Ошибка при удалении продукта с ID: " + id + ": " + e.getMessage());
            throw e;
        }
    }
}