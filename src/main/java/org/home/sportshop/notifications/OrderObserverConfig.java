package org.home.sportshop.notifications;

import org.home.sportshop.logging.LoggingService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для демонстрации работы паттерна Observer
 */
@Configuration
public class OrderObserverConfig {
    private final LoggingService logger = LoggingService.getInstance();
    
    /**
     * ApplicationRunner для проверки и вывода информации о зарегистрированных наблюдателях
     */
    @Bean
    public ApplicationRunner orderObserverRunner(OrderNotificationService notificationService) {
        return (ApplicationArguments args) -> {
            int observersCount = notificationService.getObserversCount();
            logger.logInfo("🔔 Паттерн Observer: зарегистрировано " + observersCount + " наблюдателя для уведомлений о заказах");
            
            if (observersCount > 0) {
                logger.logInfo("✅ Система уведомлений о заказах работает корректно");
            } else {
                logger.logWarning("⚠️ Система уведомлений о заказах не имеет зарегистрированных наблюдателей!");
            }
            
            logger.logInfo("ℹ️ При изменении статуса заказа будут уведомлены все зарегистрированные наблюдатели:");
            logger.logInfo("   1) EmailNotificationObserver - отправляет уведомления по email");
            logger.logInfo("   2) SMSNotificationObserver - отправляет SMS-уведомления");
            logger.logInfo("   3) AdminNotificationObserver - уведомляет администраторов системы");
            logger.logInfo("   4) AnalyticsNotificationObserver - собирает аналитику по заказам");
        };
    }
} 