package org.home.sportshop.payment;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.home.sportshop.logging.LoggingService;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Сервис для работы со стратегиями оплаты
 * Реализует контекст из паттерна Strategy
 */
@Service
public class PaymentService {
    private final Map<String, PaymentStrategy> strategies = new HashMap<>();
    private final LoggingService logger = LoggingService.getInstance();
    
    // Маппинг ID методов оплаты к их кодам
    private static final Map<Long, String> ID_TO_CODE_MAP = Map.of(
        1L, "CREDIT_CARD",
        2L, "PAYPAL",
        3L, "CASH_ON_DELIVERY"
    );
    
    // Маппинг кодов методов оплаты к их ID
    private static final Map<String, Long> CODE_TO_ID_MAP = Map.of(
        "CREDIT_CARD", 1L,
        "PAYPAL", 2L,
        "CASH_ON_DELIVERY", 3L
    );
    
    @PostConstruct
    public void init() {
        // Регистрируем доступные стратегии оплаты
        registerStrategy(new CreditCardPaymentStrategy());
        registerStrategy(new PayPalPaymentStrategy());
        registerStrategy(new CashOnDeliveryPaymentStrategy());
        
        logger.logInfo("PaymentService инициализирован с " + strategies.size() + " стратегиями оплаты");
    }
    
    /**
     * Регистрация новой стратегии оплаты
     */
    public void registerStrategy(PaymentStrategy strategy) {
        strategies.put(strategy.getPaymentMethod(), strategy);
        logger.logInfo("Зарегистрирована стратегия оплаты: " + strategy.getPaymentMethod());
    }
    
    /**
     * Получение стратегии оплаты по её идентификатору
     */
    public PaymentStrategy getStrategy(String paymentMethod) {
        PaymentStrategy strategy = strategies.get(paymentMethod);
        if (strategy == null) {
            logger.logError("Стратегия оплаты не найдена: " + paymentMethod);
        }
        return strategy;
    }
    
    /**
     * Получение стратегии оплаты по ID метода оплаты
     */
    public PaymentStrategy getStrategyById(Long paymentMethodId) {
        String code = getPaymentMethodCodeById(paymentMethodId);
        if (code == null) {
            logger.logError("Стратегия оплаты не найдена для ID: " + paymentMethodId);
            return null;
        }
        return getStrategy(code);
    }
    
    /**
     * Обработка платежа с использованием выбранной стратегии
     */
    public boolean processPayment(String paymentMethod, String orderId, BigDecimal amount) {
        PaymentStrategy strategy = getStrategy(paymentMethod);
        if (strategy == null) {
            logger.logError("Неизвестный метод оплаты: " + paymentMethod);
            return false;
        }
        
        logger.logInfo("Выбрана стратегия оплаты: " + paymentMethod);
        
        try {
            return strategy.processPayment(orderId, amount);
        } catch (Exception e) {
            logger.logError("Ошибка при обработке платежа: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Обработка платежа с использованием ID метода оплаты
     */
    public boolean processPaymentById(Long paymentMethodId, String orderId, BigDecimal amount) {
        String code = getPaymentMethodCodeById(paymentMethodId);
        if (code == null) {
            logger.logError("Неизвестный ID метода оплаты: " + paymentMethodId);
            return false;
        }
        return processPayment(code, orderId, amount);
    }
    
    /**
     * Получение списка доступных методов оплаты
     */
    public Map<String, String> getAvailablePaymentMethods() {
        Map<String, String> methods = new HashMap<>();
        methods.put("CREDIT_CARD", "Оплата кредитной картой");
        methods.put("PAYPAL", "Оплата через PayPal");
        methods.put("CASH_ON_DELIVERY", "Оплата наличными при доставке");
        return methods;
    }
    
    /**
     * Получение списка доступных методов оплаты с их ID
     */
    public Map<Long, String> getAvailablePaymentMethodsWithIds() {
        Map<Long, String> methods = new HashMap<>();
        methods.put(1L, "Оплата кредитной картой");
        methods.put(2L, "Оплата через PayPal");
        methods.put(3L, "Оплата наличными при доставке");
        return methods;
    }
    
    /**
     * Проверка доступности метода оплаты
     */
    public boolean isPaymentMethodAvailable(String paymentMethod) {
        return strategies.containsKey(paymentMethod);
    }
    
    /**
     * Проверка доступности метода оплаты по ID
     */
    public boolean isPaymentMethodIdAvailable(Long paymentMethodId) {
        return ID_TO_CODE_MAP.containsKey(paymentMethodId) && 
               strategies.containsKey(ID_TO_CODE_MAP.get(paymentMethodId));
    }
    
    /**
     * Получение списка всех зарегистрированных стратегий
     */
    public List<String> getRegisteredStrategies() {
        return strategies.keySet().stream().collect(Collectors.toList());
    }
    
    /**
     * Получение кода метода оплаты по его ID
     */
    public String getPaymentMethodCodeById(Long id) {
        return ID_TO_CODE_MAP.get(id);
    }
    
    /**
     * Получение ID метода оплаты по его коду
     */
    public Long getPaymentMethodIdByCode(String code) {
        return CODE_TO_ID_MAP.get(code);
    }
} 