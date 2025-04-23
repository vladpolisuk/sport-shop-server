package org.home.sportshop.delivery;

import java.math.BigDecimal;

/**
 * Интерфейс стратегии доставки
 */
public interface DeliveryStrategy {
    /**
     * Рассчитать стоимость доставки
     * 
     * @param distance Расстояние в километрах
     * @param weight Вес в килограммах
     * @return Стоимость доставки
     */
    BigDecimal calculateDeliveryCost(double distance, double weight);
    
    /**
     * Рассчитать время доставки
     * 
     * @param distance Расстояние в километрах
     * @return Время доставки в днях
     */
    int calculateDeliveryTime(double distance);
    
    /**
     * Проверить доступность доставки
     * 
     * @param distance Расстояние в километрах
     * @param weight Вес в килограммах
     * @return true, если доставка доступна, иначе false
     */
    boolean isAvailable(double distance, double weight);
    
    /**
     * Получить название метода доставки
     * 
     * @return Название метода доставки
     */
    String getDeliveryMethod();
} 