package org.home.sportshop.service;

import java.util.Optional;

import org.home.sportshop.logging.LoggingService;
import org.home.sportshop.model.User;
import org.home.sportshop.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final LoggingService logger = LoggingService.getInstance();

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        logger.logInfo("UserService инициализирован");
    }

    /**
     * Получение пользователя по имени пользователя
     * @param username Имя пользователя
     * @return Пользователь
     */
    public User getUserByUsername(String username) {
        logger.logInfo("Запрос на получение пользователя по username: " + username);
        return userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    String errorMessage = "Пользователь с username: " + username + " не найден";
                    logger.logError(errorMessage);
                    return new RuntimeException(errorMessage);
                });
    }

    /**
     * Получение пользователя по ID
     * @param id ID пользователя
     * @return Пользователь
     */
    public User getUserById(Long id) {
        logger.logInfo("Запрос на получение пользователя по ID: " + id);
        return userRepository.findById(id)
                .orElseThrow(() -> {
                    String errorMessage = "Пользователь с ID: " + id + " не найден";
                    logger.logError(errorMessage);
                    return new RuntimeException(errorMessage);
                });
    }

    /**
     * Проверка существования пользователя по имени пользователя
     * @param username Имя пользователя
     * @return true, если пользователь существует
     */
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Проверка существования пользователя по email
     * @param email Email пользователя
     * @return true, если пользователь существует
     */
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Безопасное получение пользователя по имени пользователя
     * @param username Имя пользователя
     * @return Optional с пользователем или пустой Optional
     */
    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
} 