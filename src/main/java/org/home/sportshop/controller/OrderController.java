package org.home.sportshop.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.home.sportshop.model.Order;
import org.home.sportshop.model.dto.OrderRequest;
import org.home.sportshop.model.dto.OrderResponse;
import org.home.sportshop.service.OrderService;
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

    @Autowired
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse createOrder(@RequestBody OrderRequest orderRequest) {
        Order order = orderService.createOrder(orderRequest.getCustomer(), orderRequest.getOrderItems());
        return OrderResponse.fromOrder(order);
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders().stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
    }
    
    @GetMapping("/my")
    public List<OrderResponse> getCurrentUserOrders() {
        // Получаем текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        // Получаем заказы текущего пользователя
        List<Order> userOrders = orderService.getOrdersByUsername(username);
        
        // Преобразуем в DTO и возвращаем
        return userOrders.stream()
                .map(OrderResponse::fromOrder)
                .collect(Collectors.toList());
    }

    @PutMapping("/{id}")
    public OrderResponse updateOrderStatus(@PathVariable Long id, @RequestBody Map<String, String> updateData) {
        String status = updateData.get("status");
        System.out.println("Updating order status to: " + status);
        Order order = orderService.updateOrderStatus(id, status);
        return OrderResponse.fromOrder(order);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(id);
        return ResponseEntity.noContent().build();
    }
}