package org.home.sportshop.service;

import java.util.List;
import java.util.Optional;

import org.home.sportshop.logging.LoggingService;
import org.home.sportshop.model.Customer;
import org.home.sportshop.model.User;
import org.home.sportshop.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final LoggingService logger = LoggingService.getInstance();

    @Autowired
    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
        logger.logInfo("CustomerService инициализирован");
    }

    public List<Customer> getAllCustomers() {
        logger.logInfo("Запрос на получение всех клиентов");
        List<Customer> customers = customerRepository.findAll();
        logger.logInfo("Получено клиентов: " + customers.size());
        return customers;
    }

    public Customer getCustomerById(Long id) {
        logger.logInfo("Запрос на получение клиента с ID: " + id);
        try {
            Customer customer = customerRepository.findById(id).orElseThrow(() -> {
                String errorMessage = "Клиент с ID: " + id + " не найден";
                logger.logError(errorMessage);
                return new RuntimeException(errorMessage);
            });
            logger.logInfo("Клиент с ID: " + id + " успешно найден");
            return customer;
        } catch (Exception e) {
            logger.logError("Ошибка при получении клиента с ID: " + id + ": " + e.getMessage());
            throw e;
        }
    }

    public Customer addCustomer(Customer customer) {
        logger.logInfo("Добавление нового клиента: " + customer.getName());
        
        // Проверка на дубликаты по email или телефону
        if (customer.getEmail() != null && customerRepository.findByEmail(customer.getEmail()).isPresent()) {
            String errorMessage = "Клиент с email " + customer.getEmail() + " уже существует";
            logger.logError(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        
        if (customer.getPhone() != null && customerRepository.findByPhone(customer.getPhone()).isPresent()) {
            String errorMessage = "Клиент с телефоном " + customer.getPhone() + " уже существует";
            logger.logError(errorMessage);
            throw new IllegalArgumentException(errorMessage);
        }
        
        Customer savedCustomer = customerRepository.save(customer);
        logger.logInfo("Клиент успешно добавлен с ID: " + savedCustomer.getId());
        return savedCustomer;
    }

    public Customer updateCustomer(Long id, Customer customerDetails) {
        logger.logInfo("Обновление клиента с ID: " + id);
        Customer customer = getCustomerById(id);
        
        customer.setName(customerDetails.getName());
        
        // Проверка на дубликаты по email, если он изменился
        if (customerDetails.getEmail() != null && !customerDetails.getEmail().equals(customer.getEmail())) {
            customerRepository.findByEmail(customerDetails.getEmail()).ifPresent(existingCustomer -> {
                if (!existingCustomer.getId().equals(id)) {
                    String errorMessage = "Клиент с email " + customerDetails.getEmail() + " уже существует";
                    logger.logError(errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                }
            });
            customer.setEmail(customerDetails.getEmail());
        }
        
        // Проверка на дубликаты по телефону, если он изменился
        if (customerDetails.getPhone() != null && !customerDetails.getPhone().equals(customer.getPhone())) {
            customerRepository.findByPhone(customerDetails.getPhone()).ifPresent(existingCustomer -> {
                if (!existingCustomer.getId().equals(id)) {
                    String errorMessage = "Клиент с телефоном " + customerDetails.getPhone() + " уже существует";
                    logger.logError(errorMessage);
                    throw new IllegalArgumentException(errorMessage);
                }
            });
            customer.setPhone(customerDetails.getPhone());
        }
        
        // Обновление других полей
        if (customerDetails.getUser() != null) {
            customer.setUser(customerDetails.getUser());
        }
        
        Customer updatedCustomer = customerRepository.save(customer);
        logger.logInfo("Клиент с ID: " + id + " успешно обновлен");
        return updatedCustomer;
    }

    public void deleteCustomer(Long id) {
        logger.logWarning("Удаление клиента с ID: " + id);
        Customer customer = getCustomerById(id);
        
        try {
            customerRepository.delete(customer);
            logger.logInfo("Клиент с ID: " + id + " успешно удален");
        } catch (Exception e) {
            logger.logError("Ошибка при удалении клиента с ID: " + id + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * Получение клиента по email
     */
    public Optional<Customer> getCustomerByEmail(String email) {
        logger.logInfo("Запрос на получение клиента по email: " + email);
        return customerRepository.findByEmail(email);
    }

    /**
     * Получение клиента по ID пользователя
     */
    public Optional<Customer> getCustomerByUserId(Long userId) {
        logger.logInfo("Запрос на получение клиента по ID пользователя: " + userId);
        List<Customer> customers = customerRepository.findByUserId(userId);
        if (customers.isEmpty()) {
            logger.logInfo("Клиент с ID пользователя " + userId + " не найден");
            return Optional.empty();
        }
        logger.logInfo("Клиент с ID пользователя " + userId + " успешно найден");
        return Optional.of(customers.get(0));
    }

    /**
     * Поиск или создание клиента на основе переданных данных.
     * Приоритет отдается поиску по ID авторизованного пользователя.
     * Если не найден по ID пользователя или пользователь анонимный,
     * ищет по email/телефону из входных данных.
     * Если не найден, создает нового клиента на основе входных данных.
     *
     * @param customerInput Данные клиента из запроса (может быть null).
     * @param currentUser Текущий авторизованный пользователь (может быть null).
     * @return Существующий или новый клиент.
     * @throws IllegalArgumentException если невозможно определить или создать клиента.
     */
    public Customer findOrCreateCustomer(Customer customerInput, User currentUser) {
        // Безопасное логирование имени клиента из запроса
        String customerNameLog = (customerInput != null) ? customerInput.getName() : "[Нет данных клиента в запросе]";
        logger.logInfo("Поиск или создание клиента. Имя из запроса: " + customerNameLog);

        // 1. Приоритет: Если пользователь авторизован, ищем его клиента по userId
        if (currentUser != null) {
            Optional<Customer> existingCustomerByUserId = getCustomerByUserId(currentUser.getId());
            if (existingCustomerByUserId.isPresent()) {
                Customer existing = existingCustomerByUserId.get();
                logger.logInfo("Найден существующий клиент по userId: " + existing.getId() + " для пользователя " + currentUser.getUsername());
                // TODO: Опционально - обновить данные существующего клиента (телефон, имя?) из customerInput, если они переданы
                // if (customerInput != null && customerInput.getPhone() != null) { existing.setPhone(customerInput.getPhone()); /* ... */ customerRepository.save(existing); }
                return existing;
            }
            // Если клиент по userId не найден, продолжаем поиск/создание ниже,
            // но будем использовать currentUser для привязки нового клиента.
            logger.logInfo("Клиент для пользователя " + currentUser.getUsername() + " не найден по userId, продолжаем поиск/создание.");
        } else {
             logger.logInfo("Пользователь не авторизован, поиск/создание клиента по данным из запроса.");
        }

        // 2. Если клиент не найден по userId (или пользователь анонимный), используем данные из запроса (customerInput)
        if (customerInput != null) {
            // Поиск по email из запроса
            if (customerInput.getEmail() != null && !customerInput.getEmail().isEmpty()) {
                Optional<Customer> existingCustomerByEmail = getCustomerByEmail(customerInput.getEmail());
                if (existingCustomerByEmail.isPresent()) {
                    Customer existing = existingCustomerByEmail.get();
                    logger.logInfo("Найден существующий клиент по email из запроса: " + existing.getId());
                    // Если пользователь авторизован и найденный клиент не связан, связываем
                    if (currentUser != null && existing.getUser() == null) {
                        existing.setUser(currentUser);
                        customerRepository.save(existing);
                        logger.logInfo("Существующий клиент (по email) " + existing.getId() + " связан с пользователем " + currentUser.getId());
                    }
                     // TODO: Опционально - обновить данные существующего клиента (телефон, имя?)
                    return existing;
                }
            }

            // Поиск по телефону из запроса (если по email не найден)
            if (customerInput.getPhone() != null && !customerInput.getPhone().isEmpty()) {
                 Optional<Customer> existingCustomerByPhone = customerRepository.findByPhone(customerInput.getPhone());
                 if (existingCustomerByPhone.isPresent()) {
                     Customer existing = existingCustomerByPhone.get();
                      logger.logInfo("Найден существующий клиент по телефону из запроса: " + existing.getId());
                      // Если пользователь авторизован и найденный клиент не связан, связываем
                     if (currentUser != null && existing.getUser() == null) {
                         existing.setUser(currentUser);
                         customerRepository.save(existing);
                         logger.logInfo("Существующий клиент (по телефону) " + existing.getId() + " связан с пользователем " + currentUser.getId());
                     }
                      // TODO: Опционально - обновить данные существующего клиента (email, имя?)
                     return existing;
                 }
            }

            // 3. Если не найден ни по userId, ни по email/телефону из запроса - создаем нового клиента
            logger.logInfo("Существующий клиент не найден. Создание нового клиента на основе данных из запроса.");

             // Проверка наличия обязательных полей для создания
             if (customerInput.getName() == null || customerInput.getName().isEmpty() ||
                 customerInput.getPhone() == null || customerInput.getPhone().isEmpty() ||
                 customerInput.getEmail() == null || customerInput.getEmail().isEmpty()) {
                 logger.logError("Недостаточно данных для создания нового клиента: имя=" + customerInput.getName() + ", телефон=" + customerInput.getPhone() + ", email=" + customerInput.getEmail());
                 throw new IllegalArgumentException("Недостаточно данных для создания нового клиента (требуется имя, телефон, email).");
             }

            // Связываем с текущим пользователем, если он авторизован
            if (currentUser != null) {
                customerInput.setUser(currentUser);
                 logger.logInfo("Новый клиент будет связан с пользователем " + currentUser.getUsername());
            }

            // Сохраняем нового клиента
            Customer savedCustomer = customerRepository.save(customerInput);
            logger.logInfo("Новый клиент успешно создан с ID: " + savedCustomer.getId());
            return savedCustomer;

        } else {
            // Сюда мы попадаем, если:
            // 1. Пользователь анонимный ИЛИ авторизованный пользователь без связанного клиента
            // 2. И при этом фронтенд НЕ ПЕРЕДАЛ customerInput в запросе
            logger.logError("Критическая ошибка: Невозможно определить или создать клиента. Нет данных в запросе и/или не найден клиент для пользователя.");
            throw new IllegalArgumentException("Невозможно определить или создать клиента: отсутствуют необходимые данные.");
        }
    }
}