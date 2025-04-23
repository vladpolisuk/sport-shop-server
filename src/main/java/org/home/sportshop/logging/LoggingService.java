package org.home.sportshop.logging;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Реализация паттерна Singleton 
 * Сервис логирования, который существует в единственном экземпляре
 */
public class LoggingService {
    private static LoggingService instance;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    // Приватный конструктор
    private LoggingService() {
        System.out.println("Инициализация LoggingService...");
    }
    
    // Метод для получения единственного экземпляра класса
    public static synchronized LoggingService getInstance() {
        if (instance == null) {
            instance = new LoggingService();
        }
        return instance;
    }
    
    public void logInfo(String message) {
        log("INFO", message);
    }
    
    public void logError(String message) {
        log("ERROR", message);
    }
    
    public void logWarning(String message) {
        log("WARNING", message);
    }
    
    private void log(String level, String message) {
        System.out.println(
            String.format("[%s] %s: %s", 
                LocalDateTime.now().format(formatter),
                level,
                message
            )
        );
    }
} 