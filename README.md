# Sport Shop - Backend API

Серверная часть приложения для интернет-магазина спортивного питания. Реализует полный набор API для работы с товарами, заказами и клиентами с JWT-аутентификацией и ролевым управлением доступом.

## Технологии

- Java 17+
- Spring Boot
- Spring Security + JWT
- Spring Data JPA
- PostgreSQL
- Maven

## Функциональность

- Управление товарами (просмотр, добавление, редактирование, удаление)
- Управление клиентами и заказами
- Система аутентификации и авторизации с ролями (USER, ADMIN)
- Валидация данных и обработка ошибок

## Требования

- Java 17 или выше
- PostgreSQL
- Maven

## Установка и запуск

1. **Клонирование репозитория**

   ```bash
   git clone <repository-url>
   cd sport-shop
   ```

2. **Настройка базы данных**
   - Создайте базу данных PostgreSQL
   - Обновите параметры подключения в `src/main/resources/application.properties`

3. **Сборка проекта**

   ```bash
   mvn clean install
   ```

4. **Запуск приложения**

   ```bash
   mvn spring-boot:run
   ```

   или

   ```bash
   java -jar target/sport-shop-0.0.1-SNAPSHOT.jar
   ```

5. **Проверка работоспособности**
   - API будет доступно по адресу: <http://localhost:8080>

## API Reference

### Товары (Products)

| Метод | Путь | Описание | Роли |
|-------|------|----------|------|
| GET | /products | Получение всех товаров | Публичный |
| GET | /products/{id} | Получение товара по ID | Публичный |
| POST | /products | Добавление нового товара | ADMIN |
| PUT | /products/{id} | Обновление товара | ADMIN |
| DELETE | /products/{id} | Удаление товара | ADMIN |

### Заказы (Orders)

| Метод | Путь | Описание | Роли |
|-------|------|----------|------|
| GET | /orders | Получение всех заказов | USER, ADMIN |
| GET | /orders/my | Получение заказов текущего пользователя | USER, ADMIN |
| POST | /orders | Создание нового заказа | USER, ADMIN |
| PUT | /orders/{id} | Обновление статуса заказа | ADMIN |
| DELETE | /orders/{id} | Удаление заказа | ADMIN |

### Клиенты (Customers)

| Метод | Путь | Описание | Роли |
|-------|------|----------|------|
| GET | /customers | Получение всех клиентов | Публичный |
| POST | /customers | Добавление нового клиента | USER, ADMIN |
| PUT | /customers/{id} | Обновление клиента | USER, ADMIN |
| DELETE | /customers/{id} | Удаление клиента | ADMIN |

### Аутентификация (Auth)

| Метод | Путь | Описание | Роли |
|-------|------|----------|------|
| POST | /auth/register | Регистрация нового пользователя | Публичный |
| POST | /auth/login | Вход в систему | Публичный |
| GET | /auth/check | Проверка токена аутентификации | Публичный |

## Авторизация

API использует JWT-токены для авторизации. При успешной аутентификации генерируется токен, который должен быть включен в заголовок:

```
Authorization: Bearer <token>
```

## Структура базы данных

- **products**: товары магазина
- **customers**: данные клиентов
- **orders**: заказы
- **order_items**: позиции заказов
- **users**: пользователи системы
- **roles**: роли пользователей

## Примеры запросов

### Регистрация пользователя

```bash
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password123","email":"user@example.com"}'
```

### Авторизация

```bash
curl -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user1","password":"password123"}'
```

### Получение списка товаров

```bash
curl -X GET http://localhost:8080/products
```

### Создание заказа

```bash
curl -X POST http://localhost:8080/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"customer":{"id":1},"orderItems":[{"product":{"id":1},"quantity":2}]}'
```

## Разработка

### Добавление новых эндпоинтов

1. Создайте модель в пакете `org.home.sportshop.model`
2. Создайте репозиторий в пакете `org.home.sportshop.repository`
3. Реализуйте сервис в пакете `org.home.sportshop.service`
4. Добавьте контроллер в пакете `org.home.sportshop.controller`
5. Обновите настройки безопасности в `SecurityConfig.java` при необходимости
