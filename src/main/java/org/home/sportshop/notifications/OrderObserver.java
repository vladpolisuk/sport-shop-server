package org.home.sportshop.notifications;

/**
 * Интерфейс наблюдателя для паттерна Observer (Наблюдатель)
 */
public interface OrderObserver {
    void update(OrderEvent event);
} 