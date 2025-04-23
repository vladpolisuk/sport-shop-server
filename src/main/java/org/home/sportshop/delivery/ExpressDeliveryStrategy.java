package org.home.sportshop.delivery;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.home.sportshop.logging.LoggingService;
import org.springframework.stereotype.Component;

/**
 * Стратегия экспресс-доставки
 */
@Component
public class ExpressDeliveryStrategy implements DeliveryStrategy {

    private static final double BASE_COST = 500.0; // Базовая стоимость доставки в рублях
    private static final double COST_PER_KM = 30.0; // Стоимость за километр
    private static final double COST_PER_KG = 80.0; // Стоимость за килограмм
    private static final double MAX_DISTANCE = 100.0; // Максимальное расстояние доставки в км
    private static final double MAX_WEIGHT = 10.0; // Максимальный вес в кг

    private final LoggingService logger = LoggingService.getInstance();

    @Override
    public BigDecimal calculateDeliveryCost(double distance, double weight) {
        if (!isAvailable(distance, weight)) {
            logger.logWarning("Экспресс-доставка недоступна для расстояния " + distance + " км и веса " + weight + " кг");
            return BigDecimal.ZERO;
        }

        double cost = BASE_COST + (distance * COST_PER_KM) + (weight * COST_PER_KG);
        logger.logInfo("Рассчитана стоимость экспресс-доставки: " + cost + " руб. для расстояния " + distance + " км и веса " + weight + " кг");
        
        return BigDecimal.valueOf(cost).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int calculateDeliveryTime(double distance) {
        if (!isAvailable(distance, 0)) {
            logger.logWarning("Экспресс-доставка недоступна для расстояния " + distance + " км");
            return -1;
        }

        // Экспресс-доставка занимает всего 1 день вне зависимости от расстояния (в пределах допустимого)
        logger.logInfo("Рассчитано время экспресс-доставки: 1 день для расстояния " + distance + " км");
        return 1;
    }

    @Override
    public boolean isAvailable(double distance, double weight) {
        boolean available = distance <= MAX_DISTANCE && weight <= MAX_WEIGHT;
        if (!available) {
            logger.logWarning("Экспресс-доставка недоступна для расстояния " + distance + " км и веса " + weight + " кг");
        }
        return available;
    }

    @Override
    public String getDeliveryMethod() {
        return "EXPRESS";
    }
} 