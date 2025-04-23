package org.home.sportshop.notifications;

import org.home.sportshop.logging.LoggingService;
import org.home.sportshop.model.Order;
import org.springframework.stereotype.Component;

/**
 * Конкретная реализация наблюдателя для уведомления администраторов
 */
@Component
public class AdminNotificationObserver implements OrderObserver {
    private final LoggingService logger = LoggingService.getInstance();
    
    @Override
    public void update(OrderEvent event) {
        Order order = event.getOrder();
        
        // Здесь бы была логика отправки уведомления администраторам
        // Например, через веб-сокеты или административную панель
        
        logger.logInfo(String.format(
            "ADMIN NOTIFICATION: Заказ #%d клиента %s (ID: %d) изменил статус с '%s' на '%s'. " +
            "Сумма заказа: %s руб.",
            order.getId(),
            order.getCustomer().getName(),
            order.getCustomer().getId(),
            event.getOldStatus(),
            event.getNewStatus(),
            order.getTotalPrice()
        ));
    }
} 