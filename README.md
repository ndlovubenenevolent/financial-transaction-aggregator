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
- **MapStruct**, **Lombok**, **OpenAPI/Swagger**, **JUnit 5**, **Mockito**, **Embedded Kafka** (producer integration tests)

## Prerequisites

- Docker & Docker Compose (to run the full stack)
- Java 21 and Maven 3.9+ (for local builds and tests — use `mvn`, not a wrapper)

## Quick Start

Clone the repo and start the full stack — no `.env` file required:

```bash
docker compose up --build
```

| Service | Port | Description |
|---------|------|-------------|
| Aggregator Service | 8080 | REST API + Kafka consumer |
| Bank Service | 8081 | Transaction simulator |
| Credit Card Service | 8082 | Transaction simulator |
| Investment Service | 8083 | Transaction simulator |
| PostgreSQL | 5432 | `aggregator_db` |
| Kafka | 9092 | Event broker |

Wait ~2 minutes for the stack to start and simulators to publish their first transactions, then query the API.

## Environment Variables

Configuration is optional. `docker-compose.yml` ships with sensible local defaults, so the project runs out of the box after cloning.

To override settings (e.g. custom DB credentials or simulator intervals), create a `.env` file in the project root:

```env
# PostgreSQL
POSTGRES_DB=aggregator_db
POSTGRES_USER=aggregator
POSTGRES_PASSWORD=aggregator
POSTGRES_PORT=5432

# Kafka
KAFKA_BOOTSTRAP_SERVERS=kafka:9092
KAFKA_PORT=9092

# Transaction simulator intervals (milliseconds)
BANK_SIMULATOR_INTERVAL_MS=60000
CARD_SIMULATOR_INTERVAL_MS=60000
INVESTMENT_SIMULATOR_INTERVAL_MS=60000

# Random customer pool size per simulator (IDs: CUST-001 .. CUST-00N)
BANK_SIMULATOR_CUSTOMER_POOL_SIZE=100
CARD_SIMULATOR_CUSTOMER_POOL_SIZE=100
INVESTMENT_SIMULATOR_CUSTOMER_POOL_SIZE=100
```

The `.env` file is gitignored and never committed. Use it only for local overrides.

| Variable | Default | Description |
|----------|---------|-------------|
| `POSTGRES_DB` | `aggregator_db` | Database name |
| `POSTGRES_USER` | `aggregator` | Database user |
| `POSTGRES_PASSWORD` | `aggregator` | Database password |
| `POSTGRES_PORT` | `5432` | Host port for PostgreSQL (DBeaver, etc.) |
| `KAFKA_BOOTSTRAP_SERVERS` | `kafka:9092` | Kafka address inside Docker network |
| `KAFKA_PORT` | `9092` | Host port for Kafka |
| `BANK_SIMULATOR_INTERVAL_MS` | `60000` | Bank publish interval |
| `BANK_SIMULATOR_CUSTOMER_POOL_SIZE` | `100` | Random bank customer IDs (`CUST-001`–`CUST-100`) |
| `CARD_SIMULATOR_INTERVAL_MS` | `60000` | Credit card publish interval |
| `CARD_SIMULATOR_CUSTOMER_POOL_SIZE` | `100` | Random card customer IDs (`CUST-001`–`CUST-100`) |
| `INVESTMENT_SIMULATOR_INTERVAL_MS` | `60000` | Investment publish interval |
| `INVESTMENT_SIMULATOR_CUSTOMER_POOL_SIZE` | `100` | Random investment customer IDs (`CUST-001`–`CUST-100`) |
| `RATE_LIMIT_ENABLED` | `true` | Enable per-client API rate limiting |
| `RATE_LIMIT_REQUESTS_PER_MINUTE` | `100` | Max requests per client per minute |

## API Authentication

Machine-to-machine clients authenticate with an API key via the `X-API-Key` header. Keys are stored as **BCrypt hashes** in the PostgreSQL `api_keys` table (never plain text).

A demo key is seeded by Flyway migration:

| Plain key (demo) | Client name | Notes |
|------------------|-------------|-------|
| `demo-api-key` | `demo-client` | Seeded in `V2__create_api_keys.sql` |

Generate a new key and BCrypt hash for production (uses the same encoder as the running service):

```bash
# Generate key + SQL (save the printed plain key — it is shown once)
./scripts/generate-api-key.sh my-client-name

# Optional: insert directly when Docker stack is running
./scripts/generate-api-key.sh my-client-name --apply
```

The script prints a ready-to-run `INSERT` statement. With `--apply`, it inserts into PostgreSQL and evicts the API key cache immediately via `POST /api/v1/admin/api-keys/cache/evict` (requires an existing valid API key, default `demo-api-key`).

Unauthorized requests return:

```json
{
  "error": "UNAUTHORIZED",
  "message": "Invalid API Key"
}
```

## Rate Limiting

`/api/**` endpoints are rate-limited **per authenticated client** (100 requests/minute by default). Limits are tracked in-memory (Caffeine) per `clientName`.

When exceeded, the API returns **429** with a `Retry-After: 60` header:

```json
{
  "error": "TOO_MANY_REQUESTS",
  "message": "Rate limit exceeded"
}
```

Configure via environment variables:

```bash
RATE_LIMIT_ENABLED=true
RATE_LIMIT_REQUESTS_PER_MINUTE=100
```

For multiple aggregator replicas in production, replace the in-memory limiter with a shared store (e.g. Redis).

## Error Handling & Failed Processes

### REST API (`aggregator-service`)

| Status | When | Response shape |
|--------|------|----------------|
| **400** | Invalid params, bad sort field, validation errors | `{ timestamp, status, message, path }` |
| **401** | Missing/invalid API key | `{ error, message }` |
| **403** | Authenticated but not authorized | `{ error, message }` |
| **404** | Resource not found | `{ timestamp, status, message, path }` |
| **429** | Rate limit exceeded | `{ error, message }` + `Retry-After` header |
| **500** | Unhandled server error (logged with stack trace) | `{ timestamp, status, message, path }` |

All 4xx/5xx API errors are logged at WARN/ERROR with method, URI, and message.

### Kafka producers (bank / card / investment)

| Failure | Behaviour |
|---------|-----------|
| Publish failure | Logged at **ERROR** with `customerId` + `eventId`; Kafka producer retries 3× (`acks=all`, idempotent) |
| Simulator tick | Continues on next schedule — no crash |

### Kafka consumer (aggregator)

| Failure | Behaviour |
|---------|-----------|
| Transient error (DB timeout, etc.) | **3 retries** with 1s fixed backoff between attempts |
| Poison pill (bad JSON, invalid argument) | No retry → sent straight to **DLT** |
| After retries exhausted | Message published to `<topic>.DLT` and logged at **ERROR** |

DLT topics: `bank-transactions.DLT`, `card-transactions.DLT`, `investment-transactions.DLT`

Inspect failed messages:

```bash
docker compose exec kafka /opt/kafka/bin/kafka-console-consumer.sh \
  --bootstrap-server localhost:9092 \
  --topic bank-transactions.DLT \
  --from-beginning
```

### Idempotency

Duplicate transactions (same `source` + `externalTransactionId`) are skipped at persistence — safe to replay messages without double-counting.

## API Documentation

Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

Use the **Authorize** button and enter the API key.

### Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/transactions` | All transactions (paginated, sortable) |
| GET | `/api/v1/customers/{customerId}/transactions` | Customer transactions |
| GET | `/api/v1/transactions/category/{category}` | Filter by category |
| GET | `/api/v1/transactions/source/{source}` | Filter by source |
| GET | `/api/v1/customers/{customerId}/monthly-summary` | Monthly income/expenses |
| GET | `/api/v1/customers/{customerId}/dashboard` | Balance, breakdown, recent txns |
| POST | `/api/v1/admin/api-keys/cache/evict` | Evict cached API keys (authenticated) |

### Example Requests

```bash
# All transactions
curl -H "X-API-Key: demo-api-key" \
  "http://localhost:8080/api/v1/transactions?page=0&size=10&sort=transactionDate,desc"

# Customer transactions
curl -H "X-API-Key: demo-api-key" \
  "http://localhost:8080/api/v1/customers/CUST-001/transactions"

# Filter by category
curl -H "X-API-Key: demo-api-key" \
  "http://localhost:8080/api/v1/transactions/category/GROCERIES"

# Filter by source
curl -H "X-API-Key: demo-api-key" \
  "http://localhost:8080/api/v1/transactions/source/BANK"

# Monthly summary
curl -H "X-API-Key: demo-api-key" \
  "http://localhost:8080/api/v1/customers/CUST-001/monthly-summary?year=2025&month=6"

# Dashboard
curl -H "X-API-Key: demo-api-key" \
  "http://localhost:8080/api/v1/customers/CUST-001/dashboard"
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
| eskom, municipality, water bill, electricity | UTILITIES |
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
├── common-events/          # Shared Kafka event DTOs, topics, enums
├── common-kafka/           # Shared Kafka producer/consumer infrastructure
├── bank-service/           # Bank transaction simulator + producer
├── credit-card-service/    # Card transaction simulator + producer
├── investment-service/     # Investment transaction simulator + producer
├── aggregator-service/     # Consumer, normalization, categorization, REST API
├── scripts/                # API key generation and operational helpers
├── docker-compose.yml
└── pom.xml
```

## Health Checks

```bash
curl http://localhost:8080/actuator/health
curl http://localhost:8081/actuator/health
curl http://localhost:8082/actuator/health
curl http://localhost:8083/actuator/health
```
