package org.home.sportshop.notifications;

import org.home.sportshop.logging.LoggingService;
import org.springframework.stereotype.Component;

/**
 * Конкретная реализация наблюдателя для отправки email-уведомлений
 */
@Component
public class EmailNotificationObserver implements OrderObserver {
    private final LoggingService logger = LoggingService.getInstance();
    
    @Override
    public void update(OrderEvent event) {
        // Здесь должна быть реальная логика отправки email
        logger.logInfo(String.format(
            "EMAIL NOTIFICATION: Заказ #%d изменил статус на '%s'",
            event.getOrder().getId(), 
            event.getNewStatus()
        ));
        
        // sendEmail(event.getOrder().getCustomer().getEmail(), ...);
    }
} 