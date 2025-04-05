package org.home.sportshop.model.dto;

import java.util.List;

import org.home.sportshop.model.Customer;
import org.home.sportshop.model.OrderItem;

public class OrderRequest {
    private Customer customer;
    private List<OrderItem> orderItems;

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
} 