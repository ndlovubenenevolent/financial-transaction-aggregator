# Financial Transaction Aggregator

Event-driven platform that aggregates customer financial transactions from multiple institutions (Bank, Credit Card, Investment), normalizes and categorizes them, stores them in PostgreSQL, and exposes REST APIs for aggregated insights.

## Architecture

```
Bank Service ──► bank-transactions ──┐
Credit Card Service ──► card-transactions ──┼──► Aggregator Service ──► PostgreSQL
Investment Service ──► investment-transactions ──┘         │
                                                           REST API
```

- **Java 21**, **Spring Boot 3**, **Maven**
- **Apache Kafka** (KRaft — no Zookeeper)
- **PostgreSQL** with Flyway migrations
- **MapStruct**, **Lombok**, **OpenAPI/Swagger**, **JUnit 5**, **Mockito**, **Testcontainers**

## Prerequisites

- Docker & Docker Compose (to run the full stack)
- Java 21 and Maven 3.9+ (for local builds and tests — use `mvn`, not a wrapper)

## Quick Start

```bash
docker-compose up --build
```

| Service | Port | Description |
|---------|------|-------------|
| Aggregator Service | 8080 | REST API + Kafka consumer |
| Bank Service | 8081 | Transaction simulator |
| Credit Card Service | 8082 | Transaction simulator |
| Investment Service | 8083 | Transaction simulator |
| PostgreSQL | 5432 | `aggregator_db` |
| Kafka | 9092 | Event broker |

Wait ~60 seconds for producers to generate transactions, then query the API.

## API Documentation

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

### Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/transactions` | All transactions (paginated, sortable) |
| GET | `/api/v1/customers/{customerId}/transactions` | Customer transactions |
| GET | `/api/v1/transactions/category/{category}` | Filter by category |
| GET | `/api/v1/transactions/source/{source}` | Filter by source |
| GET | `/api/v1/customers/{customerId}/monthly-summary` | Monthly income/expenses |
| GET | `/api/v1/customers/{customerId}/dashboard` | Balance, breakdown, recent txns |

### Example Requests

```bash
# All transactions
curl "http://localhost:8080/api/v1/transactions?page=0&size=10&sort=transactionDate,desc"

# Customer transactions
curl "http://localhost:8080/api/v1/customers/CUST-001/transactions"

# Filter by category
curl "http://localhost:8080/api/v1/transactions/category/GROCERIES"

# Filter by source
curl "http://localhost:8080/api/v1/transactions/source/BANK"

# Monthly summary
curl "http://localhost:8080/api/v1/customers/CUST-001/monthly-summary?year=2025&month=6"

# Dashboard
curl "http://localhost:8080/api/v1/customers/CUST-001/dashboard"
```

## Amount Sign Convention

| Type | Sign |
|------|------|
| Income (salary, dividends) | Positive |
| Expenses (purchases, withdrawals) | Negative |
| Investments (share/ETF buys) | Negative |

## Categorization Rules

| Keywords | Category |
|----------|----------|
| salary, dividend payment | INCOME |
| uber, bolt | TRANSPORT |
| netflix, spotify | ENTERTAINMENT |
| checkers, pick n pay, woolworths | GROCERIES |
| easyequities, etf purchase, share purchase | INVESTMENTS |
| eft transfer, transfer | TRANSFERS |
| (unknown) | OTHER |

New rules: implement `CategorizationRule` as a `@Component` — no changes to `CategorizationService` required.

## Kafka Topics

| Topic | Producer | Consumer |
|-------|----------|----------|
| `bank-transactions` | Bank Service | Aggregator |
| `card-transactions` | Credit Card Service | Aggregator |
| `investment-transactions` | Investment Service | Aggregator |
| `*.DLT` | — | Dead letter (after 3 retries) |

Consumer group: `aggregator-group`

## Build & Test

Requires Maven on your `PATH`. If builds fail with `wrong name: com/fintech/...`, run `mvn clean` first — iCloud-synced `Documents` folders can duplicate `target/` directories (e.g. `com 2/`).

```bash
# Build all modules
mvn clean package

# Run tests with coverage (aggregator enforces 80% line coverage)
mvn verify
```

## Project Structure

```
├── common-events/          # Shared Kafka event DTOs and enums
├── bank-service/           # Bank transaction simulator + producer
├── credit-card-service/    # Card transaction simulator + producer
├── investment-service/     # Investment transaction simulator + producer
├── aggregator-service/     # Consumer, normalization, categorization, REST API
├── docker-compose.yml
└── pom.xml
```

## Health Checks

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
```
