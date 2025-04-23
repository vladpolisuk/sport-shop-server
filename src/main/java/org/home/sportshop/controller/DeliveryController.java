package org.home.sportshop.controller;

import java.math.BigDecimal;
import java.util.Map;

import org.home.sportshop.delivery.DeliveryService;
import org.home.sportshop.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер для работы с доставкой
 */
@RestController
@RequestMapping("/delivery")
@CrossOrigin(origins = "http://localhost:5500")
public class DeliveryController {
    
    private final DeliveryService deliveryService;
    private final LoggingService logger = LoggingService.getInstance();
    
    @Autowired
    public DeliveryController(DeliveryService deliveryService) {
        this.deliveryService = deliveryService;
    }
    
    /**
     * Получение доступных методов доставки
     */
    @GetMapping("/methods")
    public ResponseEntity<Map<String, String>> getDeliveryMethods() {
        logger.logInfo("Запрос на получение доступных методов доставки");
        return ResponseEntity.ok(deliveryService.getAvailableDeliveryMethods());
    }
    
    /**
     * Получение доступных методов доставки с их ID
     */
    @GetMapping("/methods/ids")
    public ResponseEntity<Map<Long, String>> getDeliveryMethodsWithIds() {
        logger.logInfo("Запрос на получение доступных методов доставки с ID");
        return ResponseEntity.ok(deliveryService.getAvailableDeliveryMethodsWithIds());
    }
    
    /**
     * Расчет стоимости доставки по коду метода
     */
    @GetMapping("/cost")
    public ResponseEntity<BigDecimal> calculateDeliveryCost(
            @RequestParam String method,
            @RequestParam double distance,
            @RequestParam double weight) {
        
        logger.logInfo("Запрос на расчет стоимости доставки: метод=" + method + 
                      ", расстояние=" + distance + " км, вес=" + weight + " кг");
        
        if (!deliveryService.isDeliveryMethodAvailable(method)) {
            logger.logWarning("Запрошен неизвестный метод доставки: " + method);
            return ResponseEntity.badRequest().build();
        }
        
        BigDecimal cost = deliveryService.calculateDeliveryCost(method, distance, weight);
        return ResponseEntity.ok(cost);
    }
    
    /**
     * Расчет стоимости доставки по ID метода
     */
    @GetMapping("/cost/by-id")
    public ResponseEntity<BigDecimal> calculateDeliveryCostById(
            @RequestParam Long methodId,
            @RequestParam double distance,
            @RequestParam double weight) {
        
        logger.logInfo("Запрос на расчет стоимости доставки: methodId=" + methodId + 
                      ", расстояние=" + distance + " км, вес=" + weight + " кг");
        
        if (!deliveryService.isDeliveryMethodIdAvailable(methodId)) {
            logger.logWarning("Запрошен неизвестный ID метода доставки: " + methodId);
            return ResponseEntity.badRequest().build();
        }
        
        BigDecimal cost = deliveryService.calculateDeliveryCostById(methodId, distance, weight);
        return ResponseEntity.ok(cost);
    }
    
    /**
     * Расчет времени доставки по коду метода
     */
    @GetMapping("/time")
    public ResponseEntity<Integer> calculateDeliveryTime(
            @RequestParam String method,
            @RequestParam double distance) {
        
        logger.logInfo("Запрос на расчет времени доставки: метод=" + method + 
                      ", расстояние=" + distance + " км");
        
        if (!deliveryService.isDeliveryMethodAvailable(method)) {
            logger.logWarning("Запрошен неизвестный метод доставки: " + method);
            return ResponseEntity.badRequest().build();
        }
        
        int time = deliveryService.calculateDeliveryTime(method, distance);
        return ResponseEntity.ok(time);
    }
    
    /**
     * Расчет времени доставки по ID метода
     */
    @GetMapping("/time/by-id")
    public ResponseEntity<Integer> calculateDeliveryTimeById(
            @RequestParam Long methodId,
            @RequestParam double distance) {
        
        logger.logInfo("Запрос на расчет времени доставки: methodId=" + methodId + 
                      ", расстояние=" + distance + " км");
        
        if (!deliveryService.isDeliveryMethodIdAvailable(methodId)) {
            logger.logWarning("Запрошен неизвестный ID метода доставки: " + methodId);
            return ResponseEntity.badRequest().build();
        }
        
        int time = deliveryService.calculateDeliveryTimeById(methodId, distance);
        return ResponseEntity.ok(time);
    }
    
    /**
     * Проверка доступности доставки по коду метода
     */
    @GetMapping("/available")
    public ResponseEntity<Boolean> isDeliveryAvailable(
            @RequestParam String method,
            @RequestParam double distance,
            @RequestParam double weight) {
        
        logger.logInfo("Запрос на проверку доступности доставки: метод=" + method +
                      ", расстояние=" + distance + " км, вес=" + weight + " кг");
        
        if (!deliveryService.isDeliveryMethodAvailable(method)) {
            logger.logWarning("Запрошен неизвестный метод доставки: " + method);
            return ResponseEntity.badRequest().build();
        }
        
        boolean available = deliveryService.isDeliveryAvailable(method, distance, weight);
        return ResponseEntity.ok(available);
    }
    
    /**
     * Проверка доступности доставки по ID метода
     */
    @GetMapping("/available/by-id")
    public ResponseEntity<Boolean> isDeliveryAvailableById(
            @RequestParam Long methodId,
            @RequestParam double distance,
            @RequestParam double weight) {
        
        logger.logInfo("Запрос на проверку доступности доставки: methodId=" + methodId +
                      ", расстояние=" + distance + " км, вес=" + weight + " кг");
        
        if (!deliveryService.isDeliveryMethodIdAvailable(methodId)) {
            logger.logWarning("Запрошен неизвестный ID метода доставки: " + methodId);
            return ResponseEntity.badRequest().build();
        }
        
        boolean available = deliveryService.isDeliveryAvailableById(methodId, distance, weight);
        return ResponseEntity.ok(available);
    }
} 