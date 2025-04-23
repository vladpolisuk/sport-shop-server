package org.home.sportshop.delivery;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.home.sportshop.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Контекст стратегии доставки, использующий паттерн стратегия
 * для выбора подходящей стратегии доставки
 */
@Service
public class DeliveryContext {
    private final LoggingService logger = LoggingService.getInstance();
    private final Map<String, DeliveryStrategy> strategies = new HashMap<>();
    
    @Autowired
    public DeliveryContext(List<DeliveryStrategy> deliveryStrategies) {
        logger.logInfo("Initializing delivery strategies");
        deliveryStrategies.forEach(strategy -> {
            strategies.put(strategy.getDeliveryMethod(), strategy);
            logger.logInfo("Registered delivery strategy: " + strategy.getDeliveryMethod());
        });
    }
    
    /**
     * Установить стратегию доставки
     * 
     * @param deliveryMethod Метод доставки
     * @return Выбранная стратегия доставки
     */
    public DeliveryStrategy setStrategy(String deliveryMethod) {
        logger.logInfo("Setting delivery strategy: " + deliveryMethod);
        DeliveryStrategy strategy = strategies.get(deliveryMethod);
        
        if (strategy == null) {
            logger.logWarning("Delivery strategy not found: " + deliveryMethod);
            throw new IllegalArgumentException("Неизвестный метод доставки: " + deliveryMethod);
        }
        
        return strategy;
    }
    
    /**
     * Рассчитать стоимость доставки с использованием указанной стратегии
     * 
     * @param deliveryMethod Метод доставки
     * @param distance Расстояние в километрах
     * @param weight Вес в килограммах
     * @return Стоимость доставки
     */
    public BigDecimal calculateDeliveryCost(String deliveryMethod, double distance, double weight) {
        logger.logInfo("Calculating delivery cost for method: " + deliveryMethod);
        DeliveryStrategy strategy = setStrategy(deliveryMethod);
        return strategy.calculateDeliveryCost(distance, weight);
    }
    
    /**
     * Рассчитать время доставки с использованием указанной стратегии
     * 
     * @param deliveryMethod Метод доставки
     * @param distance Расстояние в километрах
     * @return Время доставки в днях
     */
    public int calculateDeliveryTime(String deliveryMethod, double distance) {
        logger.logInfo("Calculating delivery time for method: " + deliveryMethod);
        DeliveryStrategy strategy = setStrategy(deliveryMethod);
        return strategy.calculateDeliveryTime(distance);
    }
    
    /**
     * Проверить доступность доставки с использованием указанной стратегии
     * 
     * @param deliveryMethod Метод доставки
     * @param distance Расстояние в километрах
     * @param weight Вес в килограммах
     * @return true, если доставка доступна, иначе false
     */
    public boolean isDeliveryAvailable(String deliveryMethod, double distance, double weight) {
        logger.logInfo("Checking availability for delivery method: " + deliveryMethod);
        DeliveryStrategy strategy = setStrategy(deliveryMethod);
        return strategy.isAvailable(distance, weight);
    }
    
    /**
     * Получить список всех доступных методов доставки
     * 
     * @return Список методов доставки
     */
    public List<String> getAvailableDeliveryMethods() {
        return strategies.keySet().stream().toList();
    }
} 