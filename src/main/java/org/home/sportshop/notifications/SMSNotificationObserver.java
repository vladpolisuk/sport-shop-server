package org.home.sportshop.notifications;

import org.home.sportshop.logging.LoggingService;
import org.springframework.stereotype.Component;

/**
 * Конкретная реализация наблюдателя для отправки SMS-уведомлений
 */
@Component
public class SMSNotificationObserver implements OrderObserver {
    private final LoggingService logger = LoggingService.getInstance();
    
    @Override
    public void update(OrderEvent event) {
        // Здесь бы была реальная логика отправки SMS
        String phoneNumber = event.getOrder().getCustomer().getPhone();
        String message = generateMessage(event);
        
        logger.logInfo(String.format(
            "SMS NOTIFICATION: Отправка SMS на номер %s: %s",
            phoneNumber, message
        ));
    }
    
    private String generateMessage(OrderEvent event) {
        return String.format(
            "Уважаемый клиент! Статус вашего заказа #%d изменен с '%s' на '%s'. " +
            "С уважением, Sport Shop.",
            event.getOrder().getId(),
            event.getOldStatus(),
            event.getNewStatus()
        );
    }
} 