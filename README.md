# ShopFlow

ShopFlow is a backend REST API for an online store built with Java and Spring Boot.

The project includes authentication, product catalog management, shopping cart functionality, order processing, role-based security, stock validation, transactions, pessimistic locking, API documentation and automated tests.

## Technologies

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Data JPA
- Spring Security
- JWT
- PostgreSQL
- H2
- Hibernate
- Maven
- Swagger / OpenAPI
- JUnit
- Mockito

## Features

- User registration
- User login
- JWT authentication
- Access token
- Refresh token
- BCrypt password hashing
- CUSTOMER and ADMIN roles
- Product CRUD
- Category CRUD
- Product pagination
- Product search
- Product filtering
- Personal shopping cart
- Stock quantity validation
- Order checkout
- Transactional order processing
- Pessimistic locking
- Order status management
- Swagger documentation
- Unit and Spring Boot tests

## Roles

### CUSTOMER

CUSTOMER can:

- View products
- View categories
- Search products
- Filter products
- Manage own cart
- Create orders
- View own orders

### ADMIN

ADMIN can:

- Create products
- Update products
- Delete products
- Create categories
- Update categories
- Delete categories
- View all orders
- View individual orders
- Change order statuses

## Authentication

Main authentication endpoints:

```text
POST /api/auth/register
POST /api/auth/login
POST /api/auth/refresh
GET  /api/users/me
```

Protected endpoints require:

```text
Authorization: Bearer <ACCESS_TOKEN>
```

Access tokens are used for normal API authorization.

Refresh tokens are used only for:

```text
POST /api/auth/refresh
```

Access and refresh tokens are separated by token type.

## Products

Product endpoints:

```text
GET    /api/products
GET    /api/products/{id}
GET    /api/products/search
GET    /api/products/filter
POST   /api/products
PUT    /api/products/{id}
DELETE /api/products/{id}
```

GET operations are public.

POST, PUT and DELETE require ADMIN role.

Products support:

- Pagination
- Search by name
- Filtering by active status
- Stock quantity
- Category relation

## Categories

Category endpoints:

```text
GET    /api/categories
GET    /api/categories/{id}
POST   /api/categories
PUT    /api/categories/{id}
DELETE /api/categories/{id}
```

GET operations are public.

POST, PUT and DELETE require ADMIN role.

## Cart

Each authenticated user has a personal shopping cart.

Cart endpoints:

```text
GET    /api/cart
POST   /api/cart/add
PUT    /api/cart/items/{itemId}
DELETE /api/cart/items/{itemId}
DELETE /api/cart/clear
```

Cart functionality includes:

- Add product
- Update quantity
- Remove item
- Clear cart
- Validate product stock
- Reject inactive products
- Prevent access to another user's cart

The current user is determined from the JWT token.

## Orders

Order endpoints:

```text
POST /api/orders/checkout
GET  /api/orders/my
GET  /api/orders
GET  /api/orders/{id}
PUT  /api/orders/{id}/status
```

CUSTOMER can:

```text
POST /api/orders/checkout
GET  /api/orders/my
```

ADMIN can:

```text
GET /api/orders
GET /api/orders/{id}
PUT /api/orders/{id}/status
```

Customers can access only their own orders.

## Order Statuses

Supported order statuses:

```text
PENDING
CONFIRMED
PROCESSING
SHIPPED
DELIVERED
```

## Checkout

Order checkout is performed inside a database transaction.

The checkout method uses:

```java
@Transactional
```

During checkout:

1. The user cart is loaded
2. Cart items are validated
3. Product availability is checked
4. Product stock is locked
5. Stock quantity is reduced
6. Product price is saved in `priceAtPurchase`
7. Order items are created
8. Total order amount is calculated
9. Order is saved
10. Cart is cleared

## Pessimistic Locking

To prevent race conditions during stock updates, ShopFlow uses:

```java
@Lock(LockModeType.PESSIMISTIC_WRITE)
```

This prevents multiple concurrent orders from incorrectly purchasing the same remaining stock.

## Price At Purchase

Each order item stores:

```text
priceAtPurchase
```

This means an old order keeps the original purchase price even if the product price changes later.

## Database

Main database:

```text
PostgreSQL
```

Database name:

```text
shopflow_db
```

Main tables:

```text
users
categories
products
carts
cart_items
orders
order_items
```

## Test Database

Tests use:

```text
H2 in-memory database
```

Test profile:

```text
test
```

Test database URL:

```text
jdbc:h2:mem:shopflow_test
```

## Swagger

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Swagger provides interactive documentation for the REST API.

## Environment Variables

The JWT secret is not stored directly in the main configuration file.

Before running the application, set:

```bash
export JWT_SECRET=your_base64_secret
```

The application reads it using:

```properties
jwt.secret=${JWT_SECRET}
```

## Running the Project

Make sure PostgreSQL is running and the database exists.

Create the database if necessary:

```bash
createdb shopflow_db
```

Set the JWT secret:

```bash
export JWT_SECRET=your_base64_secret
```

Run the application:

```bash
./mvnw spring-boot:run
```

The application will start at:

```text
http://localhost:8080
```

## Running Tests

Run all tests:

```bash
./mvnw test
```

Tests cover:

- Product creation
- Missing category validation
- Successful checkout
- Empty cart validation
- Stock validation
- Spring Boot context startup
- H2 test database configuration

## Project Structure

```text
src
├── main
│   ├── java
│   │   └── com.shopflow
│   │       ├── config
│   │       ├── controller
│   │       ├── dto
│   │       ├── model
│   │       ├── repository
│   │       ├── security
│   │       └── service
│   └── resources
│       └── application.properties
│
└── test
    ├── java
    │   └── com.shopflow
    │       ├── service
    │       └── ShopflowApplicationTests
    └── resources
        └── application-test.properties
```

## Security

ShopFlow uses Spring Security with JWT authentication.

Security features include:

- BCrypt password hashing
- Stateless authentication
- JWT access tokens
- JWT refresh tokens
- Role-based authorization
- `@PreAuthorize`
- ADMIN-only management operations
- User-specific cart access
- User-specific order access

## Author

ShopFlow Java Spring Boot project.# shopflow
