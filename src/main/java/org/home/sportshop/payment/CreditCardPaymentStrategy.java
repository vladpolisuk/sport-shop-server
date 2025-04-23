package org.home.sportshop.payment;

import java.math.BigDecimal;

import org.home.sportshop.logging.LoggingService;

/**
 * Реализация стратегии оплаты кредитной картой
 */
public class CreditCardPaymentStrategy implements PaymentStrategy {
    private final LoggingService logger = LoggingService.getInstance();
    
    @Override
    public boolean processPayment(String orderId, BigDecimal amount) {
        // Реальная логика взаимодействия с платежным шлюзом
        logger.logInfo("Обработка платежа по кредитной карте для заказа: " + orderId + " на сумму: " + amount);
        // Симуляция успешной оплаты
        return true;
    }
    
    @Override
    public String getPaymentMethod() {
        return "CREDIT_CARD";
    }
} 