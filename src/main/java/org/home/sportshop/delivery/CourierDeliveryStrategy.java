package org.home.sportshop.delivery;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.home.sportshop.logging.LoggingService;
import org.springframework.stereotype.Component;

/**
 * Стратегия доставки курьером
 */
@Component
public class CourierDeliveryStrategy implements DeliveryStrategy {

    private static final double BASE_COST = 300.0; // Базовая стоимость доставки в рублях
    private static final double COST_PER_KM = 20.0; // Стоимость за километр
    private static final double COST_PER_KG = 50.0; // Стоимость за килограмм
    private static final double MAX_DISTANCE = 30.0; // Максимальное расстояние доставки в км
    private static final double MAX_WEIGHT = 15.0; // Максимальный вес в кг

    private final LoggingService logger = LoggingService.getInstance();

    @Override
    public BigDecimal calculateDeliveryCost(double distance, double weight) {
        if (!isAvailable(distance, weight)) {
            logger.logWarning("Доставка курьером недоступна для расстояния " + distance + " км и веса " + weight + " кг");
            return BigDecimal.ZERO;
        }

        double cost = BASE_COST + (distance * COST_PER_KM) + (weight * COST_PER_KG);
        logger.logInfo("Рассчитана стоимость доставки курьером: " + cost + " руб. для расстояния " + distance + " км и веса " + weight + " кг");
        
        return BigDecimal.valueOf(cost).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public int calculateDeliveryTime(double distance) {
        if (distance > MAX_DISTANCE) {
            logger.logWarning("Превышено максимальное расстояние для доставки курьером: " + distance + " км");
            return -1;
        }

        int days = distance <= 10 ? 1 : 2;
        logger.logInfo("Рассчитано время доставки курьером: " + days + " дн. для расстояния " + distance + " км");
        return days;
    }

    @Override
    public boolean isAvailable(double distance, double weight) {
        boolean available = distance <= MAX_DISTANCE && weight <= MAX_WEIGHT;
        if (!available) {
            logger.logWarning("Доставка курьером недоступна для расстояния " + distance + " км и веса " + weight + " кг");
        }
        return available;
    }

    @Override
    public String getDeliveryMethod() {
        return "COURIER";
    }
} 