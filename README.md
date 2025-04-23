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
- Выбор и расчет стоимости/времени доставки
- Обработка платежей различными методами
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
   - Примените SQL-скрипт для создания таблиц (например, `create_entire_db_2.sql`)

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
| POST | /orders | Создание нового заказа (с указанием клиента, товаров, доставки и оплаты) | USER, ADMIN |
| PUT | /orders/{id} | Обновление статуса заказа | ADMIN |
| DELETE | /orders/{id} | Удаление заказа | ADMIN |

### Клиенты (Customers)

| Метод | Путь | Описание | Роли |
|-------|------|----------|------|
| GET | /customers | Получение всех клиентов | Публичный |
| POST | /customers | Добавление нового клиента | USER, ADMIN |
| PUT | /customers/{id} | Обновление клиента | USER, ADMIN |
| DELETE | /customers/{id} | Удаление клиента | ADMIN |

### Доставка (Delivery)

| Метод | Путь | Описание | Роли |
|-------|------|----------|------|
| GET | /delivery/methods | Получение доступных методов доставки (коды) | Публичный |
| GET | /delivery/methods/ids | Получение доступных методов доставки (ID) | Публичный |
| GET | /delivery/cost | Расчет стоимости доставки (по коду) | Публичный |
| GET | /delivery/cost/by-id | Расчет стоимости доставки (по ID) | Публичный |
| GET | /delivery/time | Расчет времени доставки (по коду) | Публичный |
| GET | /delivery/time/by-id | Расчет времени доставки (по ID) | Публичный |
| GET | /delivery/available | Проверка доступности доставки (по коду) | Публичный |
| GET | /delivery/available/by-id | Проверка доступности доставки (по ID) | Публичный |

### Оплата (Payment)

| Метод | Путь | Описание | Роли |
|-------|------|----------|------|
| GET | /payments/methods | Получение доступных методов оплаты (коды) | Публичный |
| GET | /payments/methods/ids | Получение доступных методов оплаты (ID) | Публичный |
| POST | /payments/process | Обработка платежа (по коду) | USER, ADMIN |
| POST | /payments/process/by-id | Обработка платежа (по ID) | USER, ADMIN |

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
- **payment_methods**: методы оплаты
- **delivery_methods**: методы доставки

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
  -d '{
    "customerId": 1,
    "items": [
      { "productId": 1, "quantity": 2 },
      { "productId": 5, "quantity": 1 }
    ],
    "deliveryMethod": "COURIER",
    "deliveryAddress": "г. Москва, ул. Ленина, д.10, кв.5",
    "paymentMethod": "CREDIT_CARD"
  }'
```

*Примечание: Вместо `deliveryMethod` и `paymentMethod` можно передавать `deliveryMethodId` и `paymentMethodId`.*

## Разработка

### Добавление новых эндпоинтов

1. Создайте модель в пакете `org.home.sportshop.model`
2. Создайте репозиторий в пакете `org.home.sportshop.repository`
3. Реализуйте сервис в пакете `org.home.sportshop.service`
4. Добавьте контроллер в пакете `org.home.sportshop.controller`
5. Обновите настройки безопасности в `SecurityConfig.java` при необходимости
