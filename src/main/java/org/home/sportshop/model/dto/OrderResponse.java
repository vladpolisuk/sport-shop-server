package org.home.sportshop.model.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.home.sportshop.delivery.DeliveryService;
import org.home.sportshop.model.Order;
import org.home.sportshop.model.OrderItem;
import org.home.sportshop.payment.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderResponse {
    private Long id;
    private Long customerId;
    private String customerName;
    private String customerEmail;
    private String customerPhone;
    private BigDecimal totalPrice;
    private String status;
    private LocalDateTime createdAt;
    private List<OrderItemDto> orderItems;
    private Long deliveryMethodId;
    private String deliveryMethod;
    private String deliveryAddress;
    private Long paymentMethodId;
    private String paymentMethod;
    
    // Static services for mapping methods
    private static DeliveryService deliveryService;
    private static PaymentService paymentService;
    
    @Autowired
    public void setServices(DeliveryService deliveryService, PaymentService paymentService) {
        OrderResponse.deliveryService = deliveryService;
        OrderResponse.paymentService = paymentService;
    }
    
    public static OrderResponse fromOrder(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerId(order.getCustomer().getId());
        response.setCustomerName(order.getCustomer().getName());
        response.setCustomerEmail(order.getCustomer().getEmail());
        response.setCustomerPhone(order.getCustomer().getPhone());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus());
        response.setCreatedAt(order.getCreatedAt());
        response.setDeliveryMethodId(order.getDeliveryMethodId());
        response.setDeliveryAddress(order.getDeliveryAddress());
        response.setPaymentMethodId(order.getPaymentMethodId());
        
        // Set string representations if services are available
        if (deliveryService != null && order.getDeliveryMethodId() != null) {
            response.setDeliveryMethod(deliveryService.getDeliveryMethodCodeById(order.getDeliveryMethodId()));
        }
        if (paymentService != null && order.getPaymentMethodId() != null) {
            response.setPaymentMethod(paymentService.getPaymentMethodCodeById(order.getPaymentMethodId()));
        }
        
        List<OrderItemDto> items = order.getOrderItems().stream()
            .map(OrderItemDto::fromOrderItem)
            .toList();
        response.setOrderItems(items);
        
        return response;
    }
    
    // Вложенный класс для представления OrderItem
    public static class OrderItemDto {
        private Long id;
        private Long productId;
        private String productName;
        private String productDescription;
        private String productImageUrl;
        private Integer quantity;
        private BigDecimal price;
        
        public static OrderItemDto fromOrderItem(OrderItem item) {
            OrderItemDto dto = new OrderItemDto();
            dto.setId(item.getId());
            dto.setProductId(item.getProduct().getId());
            
            // Используем сохраненные детали продукта, если они есть
            dto.setProductName(item.getProductName() != null ? item.getProductName() : item.getProduct().getName());
            dto.setProductDescription(item.getProductDescription() != null ? item.getProductDescription() : item.getProduct().getDescription());
            dto.setProductImageUrl(item.getProductImageUrl() != null ? item.getProductImageUrl() : item.getProduct().getImageUrl());
            
            dto.setQuantity(item.getQuantity());
            dto.setPrice(item.getPrice());
            return dto;
        }

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getProductId() {
            return productId;
        }

        public void setProductId(Long productId) {
            this.productId = productId;
        }

        public String getProductName() {
            return productName;
        }

        public void setProductName(String productName) {
            this.productName = productName;
        }
        
        public String getProductDescription() {
            return productDescription;
        }

        public void setProductDescription(String productDescription) {
            this.productDescription = productDescription;
        }
        
        public String getProductImageUrl() {
            return productImageUrl;
        }

        public void setProductImageUrl(String productImageUrl) {
            this.productImageUrl = productImageUrl;
        }

        public Integer getQuantity() {
            return quantity;
        }

        public void setQuantity(Integer quantity) {
            this.quantity = quantity;
        }

        public BigDecimal getPrice() {
            return price;
        }

        public void setPrice(BigDecimal price) {
            this.price = price;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    
    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public BigDecimal getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(BigDecimal totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<OrderItemDto> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItemDto> orderItems) {
        this.orderItems = orderItems;
    }
    
    public Long getDeliveryMethodId() {
        return deliveryMethodId;
    }

    public void setDeliveryMethodId(Long deliveryMethodId) {
        this.deliveryMethodId = deliveryMethodId;
    }
    
    public String getDeliveryMethod() {
        return deliveryMethod;
    }

    public void setDeliveryMethod(String deliveryMethod) {
        this.deliveryMethod = deliveryMethod;
    }

    public String getDeliveryAddress() {
        return deliveryAddress;
    }

    public void setDeliveryAddress(String deliveryAddress) {
        this.deliveryAddress = deliveryAddress;
    }

    public Long getPaymentMethodId() {
        return paymentMethodId;
    }

    public void setPaymentMethodId(Long paymentMethodId) {
        this.paymentMethodId = paymentMethodId;
    }
    
    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
} 