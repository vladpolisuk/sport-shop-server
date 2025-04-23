package org.home.sportshop.notifications;

import java.util.ArrayList;
import java.util.List;

import org.home.sportshop.logging.LoggingService;
import org.home.sportshop.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Реализация паттерна Observer (Наблюдатель)
 * Сервис уведомлений о заказах
 */
@Service
public class OrderNotificationService {
    private final List<OrderObserver> observers = new ArrayList<>();
    private final LoggingService logger = LoggingService.getInstance();
    
    // Инжектируем все реализации OrderObserver через Spring
    private final List<OrderObserver> availableObservers;
    
    @Autowired
    public OrderNotificationService(List<OrderObserver> availableObservers) {
        this.availableObservers = availableObservers;
        logger.logInfo("OrderNotificationService инициализирован с " + availableObservers.size() + " доступными наблюдателями");
    }
    
    /**
     * Автоматически регистрируем всех наблюдателей при инициализации бина
     */
    @PostConstruct
    public void init() {
        logger.logInfo("Регистрация наблюдателей...");
        for (OrderObserver observer : availableObservers) {
            registerObserver(observer);
        }
    }
    
    /**
     * Регистрация нового наблюдателя
     */
    public void registerObserver(OrderObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
            logger.logInfo("Наблюдатель зарегистрирован: " + observer.getClass().getSimpleName());
        }
    }
    
    /**
     * Удаление наблюдателя
     */
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
        logger.logInfo("Наблюдатель удален: " + observer.getClass().getSimpleName());
    }
    
    /**
     * Уведомление всех наблюдателей об изменении статуса заказа
     */
    public void notifyOrderStatusChanged(Order order, String oldStatus, String newStatus) {
        if (observers.isEmpty()) {
            logger.logWarning("Нет зарегистрированных наблюдателей для уведомления об изменении статуса заказа");
            return;
        }
        
        OrderEvent event = new OrderEvent(order, oldStatus, newStatus);
        
        logger.logInfo("Уведомление " + observers.size() + " наблюдателей об изменении статуса заказа #" + order.getId());
        for (OrderObserver observer : observers) {
            try {
                observer.update(event);
            } catch (Exception e) {
                logger.logError("Ошибка при уведомлении наблюдателя " + observer.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        
        logger.logInfo(String.format(
            "Статус заказа #%d изменен с '%s' на '%s'",
            order.getId(), oldStatus, newStatus
        ));
    }
    
    /**
     * Получить количество зарегистрированных наблюдателей
     */
    public int getObserversCount() {
        return observers.size();
    }
} 