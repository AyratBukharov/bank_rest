# Bank Cards REST API

Технологии: `Java 17`, `Spring Boot`, `Spring Security`, `JWT`, `PostgreSQL`, `Liquibase`, `Docker`, `Swagger/OpenAPI`

REST API для управления банковскими картами с поддержкой ролей `ADMIN` и `USER`.

## Функционал:
## 1. Аутентификация и авторизация

- Регистрация нового пользователя (`USER`)

- Логин и получение `JWT-токена`

- Ролевой доступ:

`ADMIN` — полный доступ ко всем картам и пользователям

`USER` — доступ только к своим картам и переводам

## 2. Управление картами

Карты имеют следующие атрибуты:

- Номер карты (зашифрован и маскируется: **** **** **** 1234)

- Владелец

- Срок действия

- Статус: `ACTIVE`, `BLOCKED`, `EXPIRED`, `PENDING_BLOCK`

- Баланс

## ADMIN может:

- Создавать карты

- Изменять статус карты (`Активна`/`Заблокирована`/`Истек срок`/`Ожидает блокировки`)

- Удалять карты

- Просматривать все карты (с фильтрами по владельцу и статусу)

- Просматривать карты ожидающие запрос на блокировку

## USER может:

- Просматривать свои карты (поиск + пагинация)

- Смотреть баланс карты

- Переводить деньги между своими картами

- Запрашивать блокировку карты


## Настройка и запуск:
## 1. С помощью Docker (рекомендуется)

- Клонируйте проект:
```bash
git clone https://github.com/AyratBukharov/bank_rest.git
```

- Запуск через Docker Compose:

```bash
docker-compose up --build
```
Запуск приложения: `mvn spring-boot:run`

- Страница авторизации Login: http://localhost:8080/login.html
- Swagger UI: http://localhost:8080/swagger-ui/index.html

## Docker автоматически поднимает:

- PostgreSQL (порт 5432, БД bankdb)

- Spring Boot приложение (порт 8080)

## 2. Локальный запуск без Docker:

- Установите PostgreSQL и создайте БД:
```
CREATE DATABASE bankdb;
CREATE USER bank WITH PASSWORD 'bank';
GRANT ALL PRIVILEGES ON DATABASE bankdb TO bank;
```

- Настройте ```src/main/resources/application.yaml``` (при необходимости).

## Сборка проекта через Maven:

`mvn clean package`

Запуск приложения:
```bash
java -jar target/bank-cards-rest-1.1.0.jar
```

Swagger UI: http://localhost:8080/swagger-ui.html

## Примеры использования API:
## Регистрация (создание пользователя с ролью `USER`)

- POST `/auth/register`
```
{
"email": "user@example.com",
"password": "123456",
"fullName": "Иван Иванов"
}
```

## Авторизация

- POST `/auth/login`

```{
"email": "user@example.com",
"password": "123456"
}
```

- Ответ: `JWT-токен`.

## Админ:

- GET `/api/admin/cards` — получить все карты

- POST `/api/admin/cards` — создать карту

- GET `/api/admin/cards/pending-block` - получить все карты, ожидающие блокировки

- PATCH `/api/admin/cards/{id}/status` — изменить статус карты

- DELETE `/api/admin/cards/{id}` — удалить карту

## Пользователь:

- GET `/api/users/{userId}/cards` — список своих карт

- GET `/api/users/{userId}/cards/{cardId}` — конкретная карта пользователя

- GET `/api/users/{userId}/cards/{cardId}/balance` — баланс карты

- POST `/api/users/{userId}/transfers` — перевод между своими картами

- POST `/api/users/{userId}/cards/{cardId}/request-block` - запросить блокировку карты

## Безопасность

- `JWT` для аутентификации

- Ролевой доступ (`ADMIN` / `USER`)

- Маскирование номеров карт

- Шифрование паролей (`BCrypt`)

## Тестирование

- Юнит-тесты для сервисов (`CardService`, `TransferService`, `UserService`)

- Swagger UI для ручного тестирования API

## База данных

- PostgreSQL

- Миграции через Liquibase `src/main/resources/db/migration/db.changelog.yaml`

## Документация

- Swagger/OpenAPI: `http://localhost:8080/swagger-ui.html`
- OpenAPI YAML: `src/main/resources/static/openapi.yaml`