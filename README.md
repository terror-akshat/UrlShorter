# URL Shortener Service

## Overview
This project is a Spring Boot based URL shortener service that allows a user to register, create shortened URLs, and resolve a short code back to the original URL through HTTP redirection.

The application uses:
- Spring Boot for the application framework
- Spring Web MVC for REST APIs
- Spring Data JPA for MySQL persistence
- Redis for caching URL lookups
- Spring Boot Actuator and Prometheus for metrics

## Features
- Create a user
- Generate a unique short URL for a given long URL
- Store URL mappings in MySQL
- Resolve short URLs using Redis cache first, then MySQL as fallback
- Return an HTTP `302 Found` redirect to the original URL
- Expose actuator and Prometheus endpoints for monitoring

## Tech Stack
- Java 17
- Spring Boot 4.0.4
- MySQL
- Redis
- Maven
- H2 database for tests

## Project Structure
```text
src/main/java/com/akshat/urlShortner
|-- config
|   `-- RedisConfig.java
|-- controller
|   |-- UrlController.java
|   `-- UserController.java
|-- entity
|   |-- Url.java
|   `-- User.java
|-- repository
|   |-- UrlRepository.java
|   `-- UserRepository.java
`-- service
    |-- RedisService.java
    `-- UrlService.java
```

## Data Model
### User
Represents the owner of one or more shortened URLs.

Fields:
- `id`
- `name`
- `urls`

### Url
Represents a shortened URL entry.

Fields:
- `shortUrl` as the primary key
- `longUrl`
- `createdTime`
- `expirationTime`
- `user`

Note:
- `expirationTime` exists in the entity but is not currently used in the service logic.

## API Endpoints
### 1. Create User
**Endpoint**
```http
POST /user
```

**Request Body**
```json
{
  "name": "Akshat"
}
```

**Response**
```json
{
  "id": 1,
  "name": "Akshat"
}
```

### 2. Create Short URL
**Endpoint**
```http
POST /url/user/{id}
```

`{id}` is the user id.

**Request Body**
```json
{
  "longUrl": "https://example.com/some/very/long/path"
}
```

**Response**
```json
{
  "shortUrl": "a1b2c3d4",
  "longUrl": "https://example.com/some/very/long/path",
  "createdTime": "...",
  "expirationTime": null,
  "user": {
    "id": 1,
    "name": "Akshat"
  }
}
```

### 3. Redirect to Original URL
**Endpoint**
```http
GET /url/{shortUrl}
```

**Behavior**
- Looks for the short code in Redis first
- If not found in Redis, fetches from MySQL
- Stores the MySQL result in Redis with a TTL of `3000` seconds
- Returns HTTP `302 Found` with the original URL in the `Location` header

## Application Flow
### Flow 1: User creation
1. Client sends a `POST /user` request with user details.
2. `UserController` receives the request.
3. `UrlService.createUser()` saves the user through `UserRepository`.
4. MySQL stores the user record.
5. The saved user is returned in the response.

### Flow 2: Short URL creation
1. Client sends a `POST /url/user/{id}` request with the original long URL.
2. `UrlController` calls `UrlService.createShortUrl()`.
3. The service generates an 8 character short code using a UUID substring.
4. The service checks MySQL to avoid short code collisions.
5. The user is loaded from MySQL using the provided user id.
6. A new `Url` entity is created with:
   - generated short code
   - original long URL
   - current creation time
   - user mapping
7. The URL record is saved in MySQL.
8. The saved entity is returned to the client.

### Flow 3: Redirect and cache lookup
1. Client calls `GET /url/{shortUrl}`.
2. `UrlController` calls `UrlService.getOriginalUrl()`.
3. The service checks Redis using the short code as the key.
4. If the value exists in Redis, the cached long URL is returned immediately.
5. If Redis does not contain the key, the service fetches the URL from MySQL.
6. The service stores the database result in Redis with a `3000` second TTL.
7. The controller returns an HTTP `302 Found` response.
8. The browser or client follows the `Location` header and opens the original URL.

## Redis Caching Strategy
Redis is used only for read optimization during redirection.

Behavior:
- Cache key: `shortUrl`
- Cache value: `longUrl`
- TTL: `3000` seconds

Why it helps:
- Reduces repeated database reads for frequently accessed short URLs
- Improves redirect response time
- Keeps the database as the source of truth

## Monitoring
The project includes Spring Boot Actuator and Prometheus registry.

Configured properties:
```properties
management.endpoints.web.exposure.include=*
management.endpoint.prometheus.enabled=true
```

Useful endpoints:
- `/actuator`
- `/actuator/health`
- `/actuator/prometheus`

## Configuration
Current application properties:
```properties
spring.application.name=urlShortner
spring.datasource.url=jdbc:mysql://localhost:3306/urlShortner
spring.datasource.username=root
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

management.endpoints.web.exposure.include=*
management.endpoint.prometheus.enabled=true

spring.redis.host=localhost
spring.redis.port=6379
```

## Prerequisites
Before running the application, make sure the following are available locally:
- Java 17
- Maven or the provided Maven Wrapper
- MySQL running on `localhost:3306`
- Redis running on `localhost:6379`

You should also create a MySQL database named `urlShortner`.

Example:
```sql
CREATE DATABASE urlShortner;
```

## How to Run
### 1. Clone the project
```bash
git clone <your-repository-url>
cd urlShortner
```

### 2. Update database credentials if needed
Edit `src/main/resources/application.properties`.

### 3. Start MySQL and Redis
Make sure both services are running before starting the application.

### 4. Run the application
Using Maven Wrapper on Windows:
```powershell
.\mvnw.cmd spring-boot:run
```

Using Maven Wrapper on Linux or macOS:
```bash
./mvnw spring-boot:run
```

## How to Test
Run tests with:
```powershell
.\mvnw.cmd test
```

The test configuration uses an in-memory H2 database.

## Example API Usage
### Create a user
```bash
curl -X POST http://localhost:8080/user \
  -H "Content-Type: application/json" \
  -d '{"name":"Akshat"}'
```

### Create a short URL for that user
```bash
curl -X POST http://localhost:8080/url/user/1 \
  -H "Content-Type: application/json" \
  -d '{"longUrl":"https://example.com/article/123"}'
```

### Resolve the short URL
```bash
curl -i http://localhost:8080/url/a1b2c3d4
```

Expected redirect response:
```http
HTTP/1.1 302 Found
Location: https://example.com/article/123
```

## Internal Component Responsibilities
### `UserController`
Handles user creation requests.

### `UrlController`
Handles short URL creation and redirect requests.

### `UrlService`
Contains the main business logic for:
- creating users
- generating unique short codes
- saving URL mappings
- resolving long URLs
- coordinating database and Redis access

### `RedisService`
Wraps simple Redis `get` and `set` operations.

### `RedisConfig`
Configures `RedisTemplate<String, String>` with string serializers.

## Current Limitations
- No custom alias support for short URLs
- No URL validation before saving
- No authentication or authorization
- No click analytics yet
- No expiration handling even though the entity contains `expirationTime`
- Exception handling currently throws generic runtime exceptions

## Future Improvements
- Add DTOs for request and response handling
- Add global exception handling with meaningful error responses
- Validate input URLs before saving
- Support custom short codes
- Implement expiration logic for links
- Add click count and analytics
- Add unit and integration tests for controllers and services
- Move sensitive configuration to environment variables

## Summary
This project implements a simple and practical URL shortener service with a layered Spring Boot architecture. A user is created first, then a short code is generated and stored in MySQL, and redirect requests are accelerated using Redis caching before returning a `302 Found` response.
