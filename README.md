# Menu Service

A RESTful microservice for managing restaurant menu items, categories, and ingredients.

Part of: https://github.com/topics/korjick-java-microservice-restaurant

## Tech Stack

- **Language & Framework:** Java 25, Spring Boot 4
- **Persistence:** Spring Data JPA, Hibernate, Hypersistence Utils (JSONB)
- **Database & Migrations:** PostgreSQL, Flyway
- **Mapping & Utilities:** MapStruct, Lombok
- **Documentation:** SpringDoc OpenAPI (Swagger UI)
- **Testing:** JUnit 5, Testcontainers (`@ServiceConnection`), AssertJ, WebTestClient
- **Containerization:** Cloud Native Buildpacks

## API Endpoints

- `POST /v1/menu-items` - Create a new menu item.
- `GET /v1/menu-items/{id}` - Retrieve a menu item by ID.
- `GET /v1/menu-items?category={category}&sort={sort}` - Retrieve items filtered by category and sorted (`AZ`, `ZA`, `PRICE_ASC`, `PRICE_DESC`, `DATE_ASC`, `DATE_DESC`).
- `POST /v1/menu-items/menu-info` - Retrieve menu items info and prices for an order by item names.
- `PATCH /v1/menu-items/{id}` - Partially update menu item details.
- `DELETE /v1/menu-items/{id}` - Delete a menu item by ID.

## Build and Run

```bash
# Run tests
./gradlew test

# Run application
./gradlew bootRun

# Build and run containerized application
./gradlew bootBuildImage
cd ./docker
docker compose up -d
```
