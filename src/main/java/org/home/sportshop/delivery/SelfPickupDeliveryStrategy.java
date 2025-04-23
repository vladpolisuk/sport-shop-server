package org.home.sportshop.delivery;

import java.math.BigDecimal;

import org.home.sportshop.logging.LoggingService;
import org.springframework.stereotype.Component;

/**
 * Стратегия самовывоза товаров
 */
@Component
public class SelfPickupDeliveryStrategy implements DeliveryStrategy {

    private final LoggingService logger = LoggingService.getInstance();
    
    @Override
    public BigDecimal calculateDeliveryCost(double distance, double weight) {
        logger.logInfo("Calculating self-pickup cost - free of charge");
        return BigDecimal.ZERO; // Самовывоз бесплатный
    }

    @Override
    public int calculateDeliveryTime(double distance) {
        logger.logInfo("Self-pickup delivery time is 0 days");
        return 0; // Моментальная доставка (самовывоз)
    }

    @Override
    public boolean isAvailable(double distance, double weight) {
        // Самовывоз всегда доступен, вне зависимости от веса и расстояния
        logger.logInfo("Self-pickup is always available");
        return true;
    }

    @Override
    public String getDeliveryMethod() {
        return "SELF_PICKUP";
    }
} 