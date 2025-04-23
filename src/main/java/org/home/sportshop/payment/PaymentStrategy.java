package org.home.sportshop.payment;

import java.math.BigDecimal;

/**
 * Интерфейс стратегии оплаты для паттерна Strategy
 */
public interface PaymentStrategy {
    boolean processPayment(String orderId, BigDecimal amount);
    String getPaymentMethod();
} 