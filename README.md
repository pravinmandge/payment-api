# Payment API

This project is a RESTful API for processing payments. It provides endpoints for creating users, making payments, and retrieving transaction history.  It is built using Spring Boot, Spring Data JPA, and an H2 database.

## Features

* User registration and login.
* Payment processing with various payment methods (e.g., credit card, PayPal).
* Transaction history retrieval.
* Error handling and logging.
* Unit and integration tests.

## Getting Started

### Prerequisites

* Java 17
* Maven
* Your favorite IDE (e.g., IntelliJ IDEA, Eclipse)

### Build and Run

1. Clone the repository:

   ```bash
   git clone https://github.com/pravinmandge/payment-api.git
   ```

2. Build the project:

   ```bash
   mvn clean install
   ```

3. Run the application:

   ```bash
   mvn spring-boot:run
   ```

The application will start on port 8080.


## API Endpoints

* **User Management:**
    * `POST /users`: Register a new user.  *(Open API)*
    * `POST /login`: User login (generates a JWT).

* **Payment Transactions:**
    * `POST /api/payments`: Make a payment. *(Requires authentication)*
    * `GET /api/payments/history`: Get transaction history for the authenticated user. *(Requires authentication)*
    * `GET /api/payments/{transactionId}/status`: Get the status of a specific transaction. *(Requires authentication)*
    * `POST /api/payments/{transactionId}/refund`: Refund a specific transaction. *(Requires authentication)*


## Database

The project uses an in-memory H2 database. You can configure a different database by modifying the `application.properties` file.

## Testing

Unit and integration tests are planned but not yet implemented.  They will be located in the `src/test/java` directory.  Run tests using Maven:
   ```bash
   mvn test
   ```
