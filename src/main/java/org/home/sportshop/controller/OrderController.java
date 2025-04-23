package org.home.sportshop.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.home.sportshop.delivery.DeliveryService;
import org.home.sportshop.logging.LoggingService;
import org.home.sportshop.model.Customer;
import org.home.sportshop.model.Order;
import org.home.sportshop.model.OrderItem;
import org.home.sportshop.model.Product;
import org.home.sportshop.model.User;
import org.home.sportshop.model.dto.CreateOrderRequest;
import org.home.sportshop.model.dto.OrderResponse;
import org.home.sportshop.notifications.OrderNotificationService;
import org.home.sportshop.payment.PaymentService;
import org.home.sportshop.service.CustomerService;
import org.home.sportshop.service.OrderService;
import org.home.sportshop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "https://vladpolisuk-sport-shop.vercel.app"}, allowCredentials = "true")
public class OrderController {
    private final OrderService orderService;
    private final OrderNotificationService notificationService;
    private final CustomerService customerService;
    private final UserService userService;
    private final PaymentService paymentService;
    private final DeliveryService deliveryService;
    private final LoggingService logger = LoggingService.getInstance();

    @Autowired
    public OrderController(OrderService orderService, OrderNotificationService notificationService,
                           CustomerService customerService, UserService userService,
                           PaymentService paymentService, DeliveryService deliveryService) {
        this.orderService = orderService;
        this.notificationService = notificationService;
        this.customerService = customerService;
        this.userService = userService;
        this.paymentService = paymentService;
        this.deliveryService = deliveryService;
        logger.logInfo("OrderController инициализирован с " + notificationService.getObserversCount() + " наблюдателями");
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody CreateOrderRequest createOrderRequest) {
        logger.logInfo("Запрос на создание нового заказа");
        try {
            // Получаем ID клиента из запроса
            Long customerId = createOrderRequest.getCustomerId();
            
            // Получаем текущего аутентифицированного пользователя
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = null;
            
            if (authentication != null && authentication.isAuthenticated() && 
                    !authentication.getName().equals("anonymousUser")) {
                currentUser = userService.getUserByUsername(authentication.getName());
                logger.logInfo("Текущий пользователь: " + currentUser.getUsername());
            }
            
            // Находим клиента по ID
            Customer customer = customerService.getCustomerById(customerId);
            logger.logInfo("Использование клиента с ID: " + customer.getId() + " для создания заказа");
            
            // Преобразуем OrderItemRequest в OrderItem
            List<OrderItem> orderItems = createOrderRequest.getItems().stream()
                .map(itemRequest -> {
                    OrderItem item = new OrderItem();
                    Product product = new Product();
                    product.setId(itemRequest.getProductId());
                    item.setProduct(product);
                    item.setQuantity(itemRequest.getQuantity());
                    return item;
                })
                .collect(Collectors.toList());
            
            // Получаем данные о доставке и оплате из запроса
            Long deliveryMethodId = createOrderRequest.getDeliveryMethodId();
            String deliveryMethod = createOrderRequest.getDeliveryMethod();
            String deliveryAddress = createOrderRequest.getDeliveryAddress();
            Long paymentMethodId = createOrderRequest.getPaymentMethodId();
            String paymentMethod = createOrderRequest.getPaymentMethod();
            
            // Преобразуем строковые коды в ID, если они были предоставлены
            if (deliveryMethodId == null && deliveryMethod != null) {
                deliveryMethodId = deliveryService.getDeliveryMethodIdByCode(deliveryMethod);
                logger.logInfo("Преобразован код метода доставки '" + deliveryMethod + "' в ID: " + deliveryMethodId);
            }
            
            if (paymentMethodId == null && paymentMethod != null) {
                paymentMethodId = paymentService.getPaymentMethodIdByCode(paymentMethod);
                logger.logInfo("Преобразован код метода оплаты '" + paymentMethod + "' в ID: " + paymentMethodId);
            }
            
            // Создаем заказ с данными о доставке и оплате
            Order order = orderService.createOrder(customer, orderItems, deliveryMethodId, deliveryAddress, paymentMethodId);
            
            // Уведомляем наблюдателей о создании нового заказа
            notificationService.notifyOrderStatusChanged(order, null, order.getStatus());
            
            logger.logInfo("Заказ успешно создан с ID: " + order.getId());
            return OrderResponse.fromOrder(order);
        } catch (Exception e) {
            logger.logError("Ошибка при создании заказа: " + e.getMessage());
            throw e;
        }
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        logger.logInfo("Запрос на получение всех заказов");
        try {
            List<OrderResponse> orders = orderService.getAllOrders().stream()
                    .map(OrderResponse::fromOrder)
                    .collect(Collectors.toList());
            logger.logInfo("Возвращено заказов: " + orders.size());
            return orders;
        } catch (Exception e) {
            logger.logError("Ошибка при получении заказов: " + e.getMessage());
            throw e;
        }
    }
    
    @GetMapping("/my")
    public List<OrderResponse> getCurrentUserOrders() {
        // Получаем текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        logger.logInfo("Запрос на получение заказов пользователя: " + username);
        try {
            // Получаем заказы текущего пользователя
            List<Order> userOrders = orderService.getOrdersByUsername(username);
            
            // Преобразуем в DTO и возвращаем
            List<OrderResponse> response = userOrders.stream()
                    .map(OrderResponse::fromOrder)
                    .collect(Collectors.toList());
            
            logger.logInfo("Возвращено заказов для пользователя " + username + ": " + response.size());
            return response;
        } catch (Exception e) {
            logger.logError("Ошибка при получении заказов пользователя " + username + ": " + e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    public OrderResponse updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> updateData) {
        String status = updateData.get("status");
        logger.logInfo("Запрос на обновление статуса заказа с ID: " + id + " на статус: " + status);
        
        try {
            Order order = orderService.updateOrderStatus(id, status);
            logger.logInfo("Статус заказа с ID: " + id + " успешно обновлен на: " + status);
            return OrderResponse.fromOrder(order);
        } catch (Exception e) {
            logger.logError("Ошибка при обновлении статуса заказа с ID: " + id + ": " + e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        logger.logWarning("Запрос на удаление заказа с ID: " + id);
        try {
            orderService.deleteOrder(id);
            logger.logInfo("Заказ с ID: " + id + " успешно удален");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.logError("Ошибка при удалении заказа с ID: " + id + ": " + e.getMessage());
            throw e;
        }
    }
}