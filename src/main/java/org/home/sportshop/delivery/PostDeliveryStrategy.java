package org.home.sportshop.delivery;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.home.sportshop.logging.LoggingService;
import org.springframework.stereotype.Component;

/**
 * Реализация стратегии доставки почтой
 */
@Component
public class PostDeliveryStrategy implements DeliveryStrategy {
    private static final double BASE_COST = 150.0; // Базовая стоимость в рублях
    private static final double COST_PER_KM = 5.0; // Стоимость за километр
    private static final double COST_PER_KG = 30.0; // Стоимость за килограмм
    private static final double MAX_WEIGHT = 20.0; // Максимальный вес в кг
    private static final int AVERAGE_SPEED_KM_PER_DAY = 150; // Средняя скорость доставки
    
    private final LoggingService logger = LoggingService.getInstance();
    
    @Override
    public BigDecimal calculateDeliveryCost(double distance, double weight) {
        logger.logInfo("Calculating post delivery cost for distance: " + distance + " km, weight: " + weight + " kg");
        
        if (!isAvailable(distance, weight)) {
            logger.logWarning("Post delivery is not available for the given parameters");
            return BigDecimal.ZERO;
        }
        
        // Почтовая доставка имеет фиксированную стоимость + надбавки за расстояние и вес
        double cost = BASE_COST + (distance * COST_PER_KM) + (weight * COST_PER_KG);
        
        return BigDecimal.valueOf(cost).setScale(2, RoundingMode.HALF_UP);
    }
    
    @Override
    public int calculateDeliveryTime(double distance) {
        logger.logInfo("Calculating post delivery time for distance: " + distance + " km");
        
        if (distance <= 0) {
            return 3; // Минимальное время доставки - 3 дня
        }
        
        // Почтовая доставка обычно занимает больше времени
        int days = (int) Math.ceil(distance / AVERAGE_SPEED_KM_PER_DAY) + 2;
        return Math.max(3, days); // Не менее 3 дней
    }
    
    @Override
    public String getDeliveryMethod() {
        return "POST";
    }
    
    @Override
    public boolean isAvailable(double distance, double weight) {
        boolean available = weight <= MAX_WEIGHT && distance > 0;
        
        if (!available) {
            logger.logWarning("Post delivery is not available for weight: " + weight + " kg (max: " + MAX_WEIGHT + " kg)");
        }
        
        return available;
    }
} 