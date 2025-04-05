package org.home.sportshop.service;

import java.util.List;
import java.util.Optional;

import org.home.sportshop.model.Customer;
import org.home.sportshop.model.User;
import org.home.sportshop.repository.CustomerRepository;
import org.home.sportshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final UserRepository userRepository;

    @Autowired
    public CustomerService(CustomerRepository customerRepository, UserRepository userRepository) {
        this.customerRepository = customerRepository;
        this.userRepository = userRepository;
    }

    public List<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }

    public Customer addCustomer(Customer customer) {
        // Проверяем, существует ли уже клиент с данным email
        if (customer.getEmail() != null && !customer.getEmail().isEmpty()) {
            Optional<Customer> existingCustomer = customerRepository.findByEmail(customer.getEmail());
            if (existingCustomer.isPresent()) {
                // Возвращаем существующего клиента
                return existingCustomer.get();
            }
        }
        
        // Проверяем, существует ли уже клиент с данным телефоном
        if (customer.getPhone() != null && !customer.getPhone().isEmpty()) {
            Optional<Customer> existingCustomer = customerRepository.findByPhone(customer.getPhone());
            if (existingCustomer.isPresent()) {
                // Возвращаем существующего клиента
                return existingCustomer.get();
            }
        }
        
        // Пробуем получить текущего аутентифицированного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        
        if (authentication != null && authentication.isAuthenticated() && 
            !authentication.getPrincipal().equals("anonymousUser")) {
            String username = authentication.getName();
            Optional<User> userOpt = userRepository.findByUsername(username);
            
            // Если пользователь найден, устанавливаем его для клиента
            userOpt.ifPresent(customer::setUser);
        }
        
        return customerRepository.save(customer);
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        Customer customer = customerRepository.findById(id).orElseThrow();
        customer.setName(customerDetails.getName());
        customer.setPhone(customerDetails.getPhone());
        customer.setEmail(customerDetails.getEmail());
        return customerRepository.save(customer);
    }

    public void deleteCustomer(Long id) {
        customerRepository.deleteById(id);
    }
}