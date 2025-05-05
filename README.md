# Golden Eggs - Backend

Golden Eggs is the backend of a web-based ERP system for a company that specializes in the distribution of eggs. The system provides role-based access for administrators, employees, and clients, and handles all core business logic and data management.

## 🔧 Technologies Used

- Java 17
- Spring Boot
- Spring Security (with JWT authentication)
- Spring Data JPA
- Lombok
- MySQL (connected, but not included in this repository)
- RESTful API architecture

## 📁 Project Structure

This project follows a modular structure. Each main entity has its own directory containing:

```
└── entity-name/
    ├── model/
    ├── repository/
    ├── service/
    ├── serviceImpl/
    └── controller/
```

This ensures scalability and clean separation of concerns.

## 🚀 How to Run the Project

1. Make sure you have **Java 17** and **MySQL** installed and running.
2. Clone the repository:
   ```bash
   git clone https://github.com/estebanp22/back-golden-eggs
   ```
3. Configure your MySQL database connection in `application.properties`:
   ```properties
   spring.datasource.url=jdbc:mysql://localhost:3306/golden_eggs_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   ```
4. Run the project:
   - From your IDE (e.g., IntelliJ or Eclipse)
   - Or using the command line:
     ```bash
     ./mvnw spring-boot:run
     ```
5. The backend will be available at:  
   `http://localhost:8008`

## 🔐 Roles & Security

The application uses JWT for stateless authentication. The roles are:

- **ADMIN**: Full access to all endpoints.
- **EMPLOYEE**: Restricted access for operational tasks.
- **CLIENT**: Can place orders and view products.

Some endpoints (like public product listings and placing orders) are accessible without authentication.

## 📦 API Overview

- `/api/v1/eggs/**`
- `/api/v1/orders/**`
- `/api/v1/payments/**`
- `/api/v1/users/**`
- `/api/v1/reports/**`
- `/api/v1/inventories/**`
- `/api/v1/egg-types/**`
- `/api/v1/suppliers/**`
- `/api/v1/bills/**`
- `/api/v1/visit/**`
- `/api/auth/**`

You can test all endpoints using Postman or any API testing tool.

## 🌐 Frontend

This project works together with the frontend repo built in Angular:

- **Frontend repo**: [front-golden-eggs](https://github.com/estebanp22/front-golden-eggs)

---

Developed with ❤️.
