package org.home.sportshop.delivery;

import java.math.BigDecimal;

import org.home.sportshop.logging.LoggingService;
import org.springframework.stereotype.Component;

/**
 * Реализация стратегии самовывоза
 */
@Component
public class PickupDeliveryStrategy implements DeliveryStrategy {
    private static final double MAX_WEIGHT = 50.0; // Максимальный вес для самовывоза
    
    private final LoggingService logger = LoggingService.getInstance();
    
    @Override
    public BigDecimal calculateDeliveryCost(double distance, double weight) {
        logger.logInfo(String.format(
            "Расчет стоимости самовывоза: вес = %.2f кг",
            weight
        ));
        
        if (!isAvailable(distance, weight)) {
            logger.logWarning("Самовывоз недоступен из-за превышения веса");
            return BigDecimal.ZERO;
        }
        
        // Самовывоз бесплатный
        logger.logInfo("Стоимость самовывоза: 0.00 руб. (бесплатно)");
        return BigDecimal.ZERO;
    }
    
    @Override
    public int calculateDeliveryTime(double distance) {
        // Самовывоз доступен в течение 1 дня
        logger.logInfo("Время подготовки заказа для самовывоза: 1 день");
        return 1;
    }
    
    @Override
    public String getDeliveryMethod() {
        return "PICKUP";
    }
    
    @Override
    public boolean isAvailable(double distance, double weight) {
        boolean available = weight <= MAX_WEIGHT;
        
        if (!available) {
            logger.logInfo(String.format(
                "Самовывоз недоступен: вес = %.2f кг (макс. %s)",
                weight, MAX_WEIGHT
            ));
        }
        
        return available;
    }
} 