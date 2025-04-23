package org.home.sportshop.notifications;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.home.sportshop.logging.LoggingService;
import org.home.sportshop.model.Order;
import org.springframework.stereotype.Component;

/**
 * Конкретная реализация наблюдателя для сбора аналитических данных
 */
@Component
public class AnalyticsNotificationObserver implements OrderObserver {
    private final LoggingService logger = LoggingService.getInstance();
    
    @Override
    public void update(OrderEvent event) {
        Order order = event.getOrder();
        String oldStatus = event.getOldStatus();
        String newStatus = event.getNewStatus();
        
        // Сбор аналитических данных о изменении статуса заказа
        // В реальном приложении здесь была бы логика записи в базу данных или отправки в аналитическую систему
        
        if ("IN_WORK".equals(oldStatus) && "COMPLETED".equals(newStatus)) {
            // Например, расчет времени выполнения заказа
            LocalDateTime createdAt = order.getCreatedAt();
            LocalDateTime now = LocalDateTime.now();
            long minutesTaken = ChronoUnit.MINUTES.between(createdAt, now);
            
            logger.logInfo(String.format(
                "ANALYTICS: Заказ #%d выполнен за %d минут. Итоговая сумма: %s руб.",
                order.getId(),
                minutesTaken,
                order.getTotalPrice()
            ));
        } else if ("COMPLETED".equals(newStatus)) {
            logger.logInfo(String.format(
                "ANALYTICS: Заказ #%d изменил статус на '%s'. " +
                "Количество товаров: %d, сумма: %s руб.",
                order.getId(),
                newStatus,
                order.getOrderItems().size(),
                order.getTotalPrice()
            ));
        }
    }
} 