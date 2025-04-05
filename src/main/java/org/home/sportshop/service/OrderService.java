package org.home.sportshop.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.home.sportshop.model.Customer;
import org.home.sportshop.model.Order;
import org.home.sportshop.model.OrderItem;
import org.home.sportshop.model.Product;
import org.home.sportshop.model.User;
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

    @Autowired
    public OrderService(OrderRepository orderRepository, ProductRepository productRepository,
                        CustomerRepository customerRepository, UserRepository userRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public Order createOrder(Customer customer, List<OrderItem> orderItems) {
        Customer existingCustomer = customerRepository.findById(customer.getId())
            .orElseThrow(() -> new RuntimeException("Customer not found with id: " + customer.getId()));
        
        Order order = new Order();
        order.setCustomer(existingCustomer);
        
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (OrderItem item : orderItems) {
            Product product = productRepository.findById(item.getProduct().getId())
                .orElseThrow(() -> new RuntimeException("Product not found with id: " + item.getProduct().getId()));
            
            item.setPrice(product.getPrice());
            item.setProductName(product.getName());
            item.setProductDescription(product.getDescription());
            item.setProductImageUrl(product.getImageUrl());
            
            item.setOrder(order);
            totalPrice = totalPrice.add(product.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }
        order.setTotalPrice(totalPrice);
        order.setStatus("IN_WORK");
        order.setCreatedAt(LocalDateTime.now());
        order.setOrderItems(orderItems);
        return orderRepository.save(order);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    
    public List<Order> getOrdersByUsername(String username) {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found: " + username));
        
        List<Customer> userCustomers = customerRepository.findByUserId(user.getId());
        
        if (userCustomers.isEmpty()) {
            return List.of();
        }
        
        return orderRepository.findByCustomerIn(userCustomers);
    }

    public Order updateOrderStatus(Long id, String status) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        return orderRepository.save(order);
    }

    public void deleteOrder(Long id) {
        orderRepository.deleteById(id);
    }
}