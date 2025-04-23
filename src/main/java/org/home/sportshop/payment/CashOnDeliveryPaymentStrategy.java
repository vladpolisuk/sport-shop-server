package org.home.sportshop.payment;

import java.math.BigDecimal;

import org.home.sportshop.logging.LoggingService;

/**
 * Реализация стратегии оплаты наличными при доставке
 */
public class CashOnDeliveryPaymentStrategy implements PaymentStrategy {
    private final LoggingService logger = LoggingService.getInstance();
    
    @Override
    public boolean processPayment(String orderId, BigDecimal amount) {
        // В данной стратегии нет фактической обработки платежа, так как оплата происходит при доставке
        logger.logInfo(String.format(
            "Заказ #%s на сумму %s руб. оформлен с оплатой при доставке",
            orderId, amount
        ));
        
        // Проверка, не превышает ли сумма заказа лимит для оплаты наличными
        if (amount.compareTo(new BigDecimal("50000")) > 0) {
            logger.logWarning(String.format(
                "Заказ #%s превышает лимит для оплаты наличными (50000 руб.): %s руб.",
                orderId, amount
            ));
            return false;
        }
        
        // Отметка заказа как "ожидающего оплаты при доставке"
        logger.logInfo(String.format(
            "Заказ #%s отмечен как 'Ожидает оплаты при доставке'",
            orderId
        ));
        
        return true;
    }
    
    @Override
    public String getPaymentMethod() {
        return "CASH_ON_DELIVERY";
    }
} 