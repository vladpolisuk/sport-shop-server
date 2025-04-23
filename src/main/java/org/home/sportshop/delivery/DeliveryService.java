package org.home.sportshop.delivery;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.home.sportshop.logging.LoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

/**
 * Сервис для работы со стратегиями доставки
 * Реализует контекст из паттерна Strategy
 */
@Service
public class DeliveryService {
    private final Map<String, DeliveryStrategy> strategies = new HashMap<>();
    private final LoggingService logger = LoggingService.getInstance();
    
    // Маппинг ID методов доставки к их кодам
    private static final Map<Long, String> ID_TO_CODE_MAP = Map.of(
        1L, "COURIER",
        2L, "POST",
        3L, "SELF_PICKUP",
        4L, "EXPRESS"
    );
    
    // Маппинг кодов методов доставки к их ID
    private static final Map<String, Long> CODE_TO_ID_MAP = Map.of(
        "COURIER", 1L,
        "POST", 2L,
        "SELF_PICKUP", 3L,
        "EXPRESS", 4L
    );
    
    @Autowired
    private CourierDeliveryStrategy courierDeliveryStrategy;
    
    @Autowired
    private PostDeliveryStrategy postDeliveryStrategy;
    
    @Autowired
    private SelfPickupDeliveryStrategy selfPickupDeliveryStrategy;
    
    @Autowired
    private ExpressDeliveryStrategy expressDeliveryStrategy;
    
    @PostConstruct
    public void init() {
        // Регистрируем доступные стратегии доставки
        registerStrategy(courierDeliveryStrategy);
        registerStrategy(postDeliveryStrategy);
        registerStrategy(selfPickupDeliveryStrategy);
        registerStrategy(expressDeliveryStrategy);
        
        logger.logInfo("DeliveryService инициализирован с " + strategies.size() + " стратегиями доставки");
    }
    
    /**
     * Регистрация новой стратегии доставки
     */
    public void registerStrategy(DeliveryStrategy strategy) {
        strategies.put(strategy.getDeliveryMethod(), strategy);
        logger.logInfo("Зарегистрирована стратегия доставки: " + strategy.getDeliveryMethod());
    }
    
    /**
     * Получение стратегии доставки по её идентификатору
     */
    public DeliveryStrategy getStrategy(String deliveryMethod) {
        DeliveryStrategy strategy = strategies.get(deliveryMethod);
        if (strategy == null) {
            logger.logError("Стратегия доставки не найдена: " + deliveryMethod);
        }
        return strategy;
    }
    
    /**
     * Получение стратегии доставки по ID метода доставки
     */
    public DeliveryStrategy getStrategyById(Long deliveryMethodId) {
        String code = getDeliveryMethodCodeById(deliveryMethodId);
        if (code == null) {
            logger.logError("Стратегия доставки не найдена для ID: " + deliveryMethodId);
            return null;
        }
        return getStrategy(code);
    }
    
    /**
     * Рассчитать стоимость доставки с использованием выбранной стратегии
     */
    public BigDecimal calculateDeliveryCost(String deliveryMethod, double distance, double weight) {
        DeliveryStrategy strategy = getStrategy(deliveryMethod);
        if (strategy == null) {
            logger.logError("Неизвестный метод доставки: " + deliveryMethod);
            return BigDecimal.ZERO;
        }
        
        logger.logInfo("Выбрана стратегия доставки: " + deliveryMethod);
        
        try {
            return strategy.calculateDeliveryCost(distance, weight);
        } catch (Exception e) {
            logger.logError("Ошибка при расчете стоимости доставки: " + e.getMessage());
            return BigDecimal.ZERO;
        }
    }
    
    /**
     * Рассчитать стоимость доставки с использованием ID метода доставки
     */
    public BigDecimal calculateDeliveryCostById(Long deliveryMethodId, double distance, double weight) {
        String code = getDeliveryMethodCodeById(deliveryMethodId);
        if (code == null) {
            logger.logError("Неизвестный ID метода доставки: " + deliveryMethodId);
            return BigDecimal.ZERO;
        }
        return calculateDeliveryCost(code, distance, weight);
    }
    
    /**
     * Рассчитать время доставки с использованием выбранной стратегии
     */
    public int calculateDeliveryTime(String deliveryMethod, double distance) {
        DeliveryStrategy strategy = getStrategy(deliveryMethod);
        if (strategy == null) {
            logger.logError("Неизвестный метод доставки: " + deliveryMethod);
            return -1;
        }
        
        try {
            return strategy.calculateDeliveryTime(distance);
        } catch (Exception e) {
            logger.logError("Ошибка при расчете времени доставки: " + e.getMessage());
            return -1;
        }
    }
    
    /**
     * Рассчитать время доставки с использованием ID метода доставки
     */
    public int calculateDeliveryTimeById(Long deliveryMethodId, double distance) {
        String code = getDeliveryMethodCodeById(deliveryMethodId);
        if (code == null) {
            logger.logError("Неизвестный ID метода доставки: " + deliveryMethodId);
            return -1;
        }
        return calculateDeliveryTime(code, distance);
    }
    
    /**
     * Проверить доступность доставки с использованием выбранной стратегии
     */
    public boolean isDeliveryAvailable(String deliveryMethod, double distance, double weight) {
        DeliveryStrategy strategy = getStrategy(deliveryMethod);
        if (strategy == null) {
            logger.logError("Неизвестный метод доставки: " + deliveryMethod);
            return false;
        }
        
        try {
            return strategy.isAvailable(distance, weight);
        } catch (Exception e) {
            logger.logError("Ошибка при проверке доступности доставки: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Проверить доступность доставки с использованием ID метода доставки
     */
    public boolean isDeliveryAvailableById(Long deliveryMethodId, double distance, double weight) {
        String code = getDeliveryMethodCodeById(deliveryMethodId);
        if (code == null) {
            logger.logError("Неизвестный ID метода доставки: " + deliveryMethodId);
            return false;
        }
        return isDeliveryAvailable(code, distance, weight);
    }
    
    /**
     * Получение списка доступных методов доставки
     */
    public Map<String, String> getAvailableDeliveryMethods() {
        Map<String, String> methods = new HashMap<>();
        methods.put("COURIER", "Курьерская доставка");
        methods.put("POST", "Почтовая доставка");
        methods.put("SELF_PICKUP", "Самовывоз");
        methods.put("EXPRESS", "Экспресс-доставка");
        return methods;
    }
    
    /**
     * Получение списка доступных методов доставки с их ID
     */
    public Map<Long, String> getAvailableDeliveryMethodsWithIds() {
        Map<Long, String> methods = new HashMap<>();
        methods.put(1L, "Курьерская доставка");
        methods.put(2L, "Почтовая доставка");
        methods.put(3L, "Самовывоз");
        methods.put(4L, "Экспресс-доставка");
        return methods;
    }
    
    /**
     * Проверка доступности метода доставки
     */
    public boolean isDeliveryMethodAvailable(String deliveryMethod) {
        return strategies.containsKey(deliveryMethod);
    }
    
    /**
     * Проверка доступности метода доставки по ID
     */
    public boolean isDeliveryMethodIdAvailable(Long deliveryMethodId) {
        return ID_TO_CODE_MAP.containsKey(deliveryMethodId) && 
               strategies.containsKey(ID_TO_CODE_MAP.get(deliveryMethodId));
    }
    
    /**
     * Получение списка всех зарегистрированных стратегий
     */
    public List<String> getRegisteredStrategies() {
        return strategies.keySet().stream().collect(Collectors.toList());
    }
    
    /**
     * Получение кода метода доставки по его ID
     */
    public String getDeliveryMethodCodeById(Long id) {
        return ID_TO_CODE_MAP.get(id);
    }
    
    /**
     * Получение ID метода доставки по его коду
     */
    public Long getDeliveryMethodIdByCode(String code) {
        return CODE_TO_ID_MAP.get(code);
    }
} 