package org.home.sportshop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.home.sportshop.logging.LoggingService;
import org.home.sportshop.model.Customer;
import org.home.sportshop.model.Order;
import org.home.sportshop.model.OrderItem;
import org.home.sportshop.model.Product;
import org.home.sportshop.model.User;
import org.home.sportshop.notifications.OrderNotificationService;
import org.home.sportshop.repository.CustomerRepository;
import org.home.sportshop.repository.OrderRepository;
import org.home.sportshop.repository.ProductRepository;
import org.home.sportshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrderService {
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;
    private final OrderNotificationService notificationService;
    private final LoggingService logger = LoggingService.getInstance();

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        CustomerRepository customerRepository, UserRepository userRepository,
                        OrderNotificationService notificationService) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
        logger.logInfo("OrderService инициализирован");
    }

    @Transactional
    public Order createOrder(Customer customer, List<OrderItem> orderItems) {
        return createOrder(customer, orderItems, null, null, null);
    }

    @Transactional
    public Order createOrder(Customer customer, List<OrderItem> orderItems, 
                             Long deliveryMethodId, String deliveryAddress, Long paymentMethodId) {
        if (customer == null) {
            logger.logError("Ошибка создания заказа: Клиент не может быть null.");
            throw new IllegalArgumentException("Клиент не указан для заказа.");
        }
        if (orderItems == null || orderItems.isEmpty()) {
            logger.logError("Ошибка создания заказа: Список товаров пуст или не передан.");
            throw new IllegalArgumentException("Невозможно создать заказ без товаров.");
        }

        logger.logInfo("Создание нового заказа для клиента с ID: " + customer.getId());
        Customer existingCustomer = customerRepository.findById(customer.getId())
            .orElseThrow(() -> {
                String errorMessage = "Customer not found with id: " + customer.getId();
                logger.logError(errorMessage);
                return new RuntimeException(errorMessage);
            });
        
        Order order = new Order();
        order.setCustomer(existingCustomer);
        order.setDeliveryMethodId(deliveryMethodId);
        order.setDeliveryAddress(deliveryAddress);
        order.setPaymentMethodId(paymentMethodId);
        
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            if (item.getProduct() == null || item.getProduct().getId() == null) {
                logger.logError("Ошибка в товаре заказа: отсутствует информация о продукте.");
                throw new IllegalArgumentException("Ошибка в товаре заказа: отсутствует информация о продукте.");
            }
            if (item.getQuantity() <= 0) {
                logger.logError("Ошибка в товаре заказа: количество должно быть положительным.");
                throw new IllegalArgumentException("Ошибка в товаре заказа: количество должно быть положительным.");
            }

            Product product = productRepository.findById(item.getProduct().getId())
                .orElseThrow(() -> {
                    String errorMessage = "Product not found with id: " + item.getProduct().getId();
                    logger.logError(errorMessage);
                    return new RuntimeException(errorMessage);
                });
            
            logger.logInfo("Добавление товара в заказ: " + product.getName() + ", количество: " + item.getQuantity());
            item.setPrice(product.getPrice());
            item.setProductName(product.getName());
            item.setProductDescription(product.getDescription());
            item.setProductImageUrl(product.getImageUrl());
            
            item.setOrder(order);
            totalPrice = totalPrice.add(item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setTotalPrice(totalPrice);
        order.setStatus("CREATED");
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderItems(orderItems);
        
        Order savedOrder = orderRepository.save(order);
        logger.logInfo("Заказ успешно создан с ID: " + savedOrder.getId() + ", общая сумма: " + totalPrice);
        return savedOrder;
    }

    public List<Order> getAllOrders() {
        logger.logInfo("Запрос на получение всех заказов");
        List<Order> orders = orderRepository.findAll();
        logger.logInfo("Получено заказов: " + orders.size());
        return orders;
    }
    
    public List<Order> getOrdersByUsername(String username) {
        logger.logInfo("Запрос на получение заказов пользователя: " + username);
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> {
                String errorMessage = "User not found: " + username;
                logger.logError(errorMessage);
                return new RuntimeException(errorMessage);
            });
        
        List<Customer> userCustomers = customerRepository.findByUserId(user.getId());
        
        if (userCustomers.isEmpty()) {
            logger.logWarning("У пользователя " + username + " нет связанных клиентов");
            return List.of();
        }
        
        List<Order> orders = orderRepository.findByCustomerIn(userCustomers);
        logger.logInfo("Получено " + orders.size() + " заказов для пользователя " + username);
        return orders;
    }

    public Order updateOrderStatus(Long id, String status) {
        logger.logInfo("Обновление статуса заказа с ID: " + id + " на: " + status);
        Order order = orderRepository.findById(id).orElseThrow(() -> {
            String errorMessage = "Order not found with id: " + id;
            logger.logError(errorMessage);
            return new RuntimeException(errorMessage);
        });
        
        String oldStatus = order.getStatus();
        order.setStatus(status);
        Order updatedOrder = orderRepository.save(order);
        
        logger.logInfo("Статус заказа с ID: " + id + " успешно обновлен с " + oldStatus + " на " + status);
        notificationService.notifyOrderStatusChanged(updatedOrder, oldStatus, status);
        
        return updatedOrder;
    }

    public void deleteOrder(Long id) {
        logger.logWarning("Удаление заказа с ID: " + id);
        try {
            orderRepository.deleteById(id);
            logger.logInfo("Заказ с ID: " + id + " успешно удален");
        } catch (Exception e) {
            logger.logError("Ошибка при удалении заказа с ID: " + id + ": " + e.getMessage());
            throw e;
        }
    }
}