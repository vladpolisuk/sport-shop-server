package org.home.sportshop.notifications;

import org.home.sportshop.model.Order;

/**
 * Класс события, содержащий информацию о заказе
 * для паттерна Observer (Наблюдатель)
 */
public class OrderEvent {
    private final Order order;
    private final String oldStatus;
    private final String newStatus;
    
    public OrderEvent(Order order, String oldStatus, String newStatus) {
        this.order = order;
        this.oldStatus = oldStatus;
        this.newStatus = newStatus;
    }

    public Order getOrder() {
        return order;
    }

    public String getOldStatus() {
        return oldStatus;
    }

    public String getNewStatus() {
        return newStatus;
    }
} 