package org.home.sportshop.repository;

import java.util.List;

import org.home.sportshop.model.Customer;
import org.home.sportshop.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomerIn(List<Customer> customers);
}