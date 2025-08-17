# LivoApp - Hotel Booking Platform(Backend) 🏨

LivoApp is a **hotel booking platform** built with **Java, Spring Boot, and PostgreSQL**. It allows users to browse hotels, check availability, make bookings, and enables hotel owners to manage their inventory seamlessly.

## 🚀 Features

- 🔐 **User Authentication** with JWT (JSON Web Token)
- 🔍 **Hotel Search** by city and date
- 🏨 **Hotel Details** with available rooms and pricing
- 💰 **Dynamic Pricing Strategies** (base pricing, holiday pricing, etc.)
- 📅 **Booking Management** (create, update, cancel bookings)
- 👥 **Guest Management** linked with bookings
- 🛠️ **Hotel & Room Management** for owners
- 📦 **Inventory Management** per room and date

## 🛠️ Tech Stack

- **Java 17+**
- **Spring Boot 3**
- **Spring Data JPA (Hibernate)**
- **PostgreSQL**
- **Maven**
- **Lombok**
- **ModelMapper**

## ⚙️ Getting Started

### ✅ Prerequisites

- Java **17** or higher
- **Maven** installed
- **PostgreSQL** database running

### 🔧 Setup Instructions

1. **Configure the database**

   Update your `application.properties` or `application.yml` with PostgreSQL credentials:

   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/livoapp
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   spring.jpa.hibernate.ddl-auto=update
   spring.jpa.show-sql=true
   ```

2. **Build the project**

   ```sh
   mvn clean install
   ```

3. **Run the application**

   ```sh
   mvn spring-boot:run
   ```

## 📌 API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/api/auth/register` | POST | Register a new user |
| `/api/auth/login` | POST | User login & JWT token |
| `/api/hotels` | GET | Fetch all hotels |
| `/api/hotels/{id}` | GET | Get hotel details by ID |
| `/api/hotels/search` | GET | Search hotels by city/date |
| `/api/bookings` | POST | Create a new booking |
| `/api/bookings/{id}` | GET | Get booking details |
| `/api/rooms/inventory` | PUT | Update room inventory |

## 🧪 Testing

Run unit tests with:

```sh
mvn test
```

## 📂 Project Structure

```bash
livoApp
 ┣ src/main/java/com/example/livoApp
 ┃ ┣ config/         # Security & Configurations
 ┃ ┣ controller/     # REST Controllers
 ┃ ┣ dto/            # Data Transfer Objects
 ┃ ┣ entity/         # JPA Entities
 ┃ ┣ repository/     # Spring Data JPA Repositories
 ┃ ┣ security/       # JWT Security Components
 ┃ ┣ service/        # Service Layer
 ┃ ┗ LivoApp.java    # Main Application
 ┣ src/main/resources
 ┃ ┣ application.properties
 ┣ pom.xml
```

## 🔐 Security Flow

- User registers/login → Receives JWT Token
- JWT is passed in Authorization Header for protected endpoints
- Custom JwtAuthenticationEntryPoint handles unauthorized requests

## 🤝 Contributing

1. Fork the project
2. Create a feature branch (`feature/awesome-feature`)
3. Commit changes (`git commit -m 'Add new feature'`)
4. Push to branch (`git push origin feature/awesome-feature`)
5. Create a Pull Request
