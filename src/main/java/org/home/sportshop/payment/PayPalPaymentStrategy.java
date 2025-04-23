package org.home.sportshop.payment;

import java.math.BigDecimal;

import org.home.sportshop.logging.LoggingService;

/**
 * Реализация стратегии оплаты через PayPal
 */
public class PayPalPaymentStrategy implements PaymentStrategy {
    private final LoggingService logger = LoggingService.getInstance();
    
    @Override
    public boolean processPayment(String orderId, BigDecimal amount) {
        // Реальная логика взаимодействия с API PayPal
        logger.logInfo("Обработка платежа через PayPal для заказа: " + orderId + " на сумму: " + amount);
        // Симуляция успешной оплаты
        return true;
    }
    
    @Override
    public String getPaymentMethod() {
        return "PAYPAL";
    }
} 