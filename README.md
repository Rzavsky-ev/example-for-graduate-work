# README: Платформа объявлений

## Описание проекта

Веб-приложение для размещения и управления объявлениями с возможностью комментирования. RESTful API сервис с
аутентификацией пользователей и разделением ролей (USER и ADMIN).

### Основные возможности:

- ✅ Регистрация и аутентификация пользователей
- ✅ CRUD операции с объявлениями
- ✅ Загрузка изображений для объявлений
- ✅ Система комментариев
- ✅ Управление профилем пользователя
- ✅ Ролевая модель (USER/ADMIN)

## Технологический стек

### Backend

- **Java 17**
- **Spring Boot 3.x**
- **Spring Security**
- **Spring Data JPA**
- **Hibernate**
- **Lombok**
- **Swagger/OpenAPI 3.0** (документация API)
- **BCrypt** (хеширование паролей)

### База данных

- **PostgreSQL 15** (основная СУБД)
- **Flyway** (управление миграциями)

### Инфраструктура

- **Maven** (сборка)
- **Docker** (контейнеризация)
- **Git** (версионный контроль)

## Установка и запуск

### Требования:

- Java 17+
- PostgreSQL 15+
- Maven 3.8+

### Шаги установки:

1. Клонировать репозиторий:

```bash
git clone https://github.com/your-repository/ad-platform.git
cd ad-platform
```

2. Настроить базу данных:

```bash
createdb ads_db
```

3. Собрать проект:

```bash
mvn clean install
```

4. Запустить приложение:

```bash
java -jar target/ad-platform-0.0.1-SNAPSHOT.jar
```

Приложение будет доступно по адресу: http://localhost:8080

## Документация API

После запуска доступны:

Swagger UI: http://localhost:8080/swagger-ui.html

OpenAPI спецификация: http://localhost:8080/v3/api-docs

## Модели данных

### Сущности:

#### Пользователь (User):

- id - уникальный идентификатор

- username - email (логин)

- password - хэшированный пароль

- firstName/lastName - имя и фамилия

- phone - контактный телефон

- role - USER или ADMIN

#### Объявление (Ad):

- id - уникальный идентификатор

- title - заголовок

- description - описание

- price - цена

- imagePath - путь к изображению

- author - связь ManyToOne с User

- comments - связь OneToMany с Comment

#### Комментарий (Comment):

- id - уникальный идентификатор

- text - содержимое

- createdAt - timestamp создания

- author - связь ManyToOne с User

- ad - связь ManyToOne с Ad

## API Endpoints

### Объявления

| Метод  | Путь        | Описание                  |
|--------|-------------|---------------------------|
| GET    | `/ads`      | Получить все объявления   |
| POST   | `/ads`      | Создать новое объявление  |
| GET    | `/ads/{id}` | Получить объявление по ID |
| PATCH  | `/ads/{id}` | Обновить объявление       |
| DELETE | `/ads/{id}` | Удалить объявление        |
| GET    | `/ads/me`   | Получить свои объявления  |

### Комментарии

| Метод  | Путь                             | Описание             |
|--------|----------------------------------|----------------------|
| POST   | `/ads/{id}/comments`             | Добавить комментарий |
| GET    | `/ads/{id}/comments`             | Получить комментарии |
| DELETE | `/ads/{id}/comments/{commentId}` | Удалить комментарий  |

### Пользователи

| Метод | Путь                  | Описание         |
|-------|-----------------------|------------------|
| POST  | `/register`           | Регистрация      |
| POST  | `/login`              | Аутентификация   |
| GET   | `/users/me`           | Получить профиль |
| PATCH | `/users/me`           | Обновить профиль |
| POST  | `/users/set_password` | Сменить пароль   |

## Конфигурация

## Основные настройки (application.properties):

### properties

#### Server

server.port=8080

#### PostgreSQL

spring.datasource.url=jdbc:postgresql://localhost:5432/ads_db
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.datasource.driver-class-name=org.postgresql.Driver

#### JPA/Hibernate

spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

#### File storage

image.upload.directory=uploads
image.max-size=5MB

## Разработчик

👨‍💻 Ржавский Эдуард Владимирович

- Backend разработчик

- Архитектор решения