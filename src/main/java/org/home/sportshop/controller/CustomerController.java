package org.home.sportshop.controller;

import java.util.List;
import java.util.Optional;

import org.home.sportshop.logging.LoggingService;
import org.home.sportshop.model.Customer;
import org.home.sportshop.model.User;
import org.home.sportshop.service.CustomerService;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/customers")
@CrossOrigin(origins = {"http://localhost:5500", "http://127.0.0.1:5500", "https://vladpolisuk-sport-shop.vercel.app"}, allowCredentials = "true")
public class CustomerController {
    private final CustomerService customerService;
    private final UserService userService;
    private final LoggingService logger = LoggingService.getInstance();

    @Autowired
    public CustomerController(CustomerService customerService, UserService userService) {
        this.customerService = customerService;
        this.userService = userService;
        logger.logInfo("CustomerController инициализирован");
    }

    @GetMapping
    public List<Customer> getAllCustomers() {
        logger.logInfo("Запрос на получение всех клиентов");
        List<Customer> customers = customerService.getAllCustomers();
        logger.logInfo("Возвращено клиентов: " + customers.size());
        return customers;
    }

    @PostMapping
    public Customer addCustomer(@RequestBody Customer customer) {
        logger.logInfo("Запрос на добавление нового клиента: " + customer.getName());
        try {
            // Получаем текущего авторизованного пользователя
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = null;
            
            if (authentication != null && authentication.isAuthenticated() && 
                    !authentication.getName().equals("anonymousUser")) {
                currentUser = userService.getUserByUsername(authentication.getName());
                logger.logInfo("Текущий пользователь: " + currentUser.getUsername());
            }
            
            // Используем метод для поиска или создания клиента
            Customer savedCustomer = customerService.findOrCreateCustomer(customer, currentUser);
            logger.logInfo("Клиент получен/создан с ID: " + savedCustomer.getId());
            return savedCustomer;
        } catch (Exception e) {
            logger.logError("Ошибка при добавлении клиента: " + e.getMessage());
            throw e;
        }
    }

    @PutMapping("/{id}")
    public Customer updateCustomer(@PathVariable Long id, @RequestBody Customer customer) {
        logger.logInfo("Запрос на обновление клиента с ID: " + id);
        try {
            Customer updatedCustomer = customerService.updateCustomer(id, customer);
            logger.logInfo("Клиент с ID: " + id + " успешно обновлен");
            return updatedCustomer;
        } catch (Exception e) {
            logger.logError("Ошибка при обновлении клиента с ID: " + id + ": " + e.getMessage());
            throw e;
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        logger.logWarning("Запрос на удаление клиента с ID: " + id);
        try {
            customerService.deleteCustomer(id);
            logger.logInfo("Клиент с ID: " + id + " успешно удален");
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            logger.logError("Ошибка при удалении клиента с ID: " + id + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Проверка существования клиента по email
     * Если клиент существует, возвращает его данные
     * Если пользователь авторизован и связан с клиентом, возвращает данные клиента
     * Если пользователь не авторизован и клиент не существует, возвращает 404
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkCustomer(@RequestParam(required = false) String email) {
        logger.logInfo("Запрос на проверку клиента по email: " + email);
        
        // Получаем текущего авторизованного пользователя
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = null;
        
        if (authentication != null && authentication.isAuthenticated() && 
                !authentication.getName().equals("anonymousUser")) {
            currentUser = userService.getUserByUsername(authentication.getName());
            logger.logInfo("Текущий пользователь: " + currentUser.getUsername());
        }

        // Если email передан, пытаемся найти клиента по email
        if (email != null && !email.isEmpty()) {
            Optional<Customer> existingCustomer = customerService.getCustomerByEmail(email);
            if (existingCustomer.isPresent()) {
                logger.logInfo("Клиент с email " + email + " найден, ID: " + existingCustomer.get().getId());
                return ResponseEntity.ok(existingCustomer.get());
            }
        }
        
        // Если пользователь авторизован, пытаемся найти клиента по userId
        if (currentUser != null) {
            Optional<Customer> existingCustomer = customerService.getCustomerByUserId(currentUser.getId());
            if (existingCustomer.isPresent()) {
                logger.logInfo("Клиент с userId " + currentUser.getId() + " найден, ID: " + existingCustomer.get().getId());
                return ResponseEntity.ok(existingCustomer.get());
            }
        }
        
        // Клиент не найден
        logger.logInfo("Клиент не найден");
        return ResponseEntity.notFound().build();
    }
}