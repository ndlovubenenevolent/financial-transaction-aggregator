# Design Document

Architecture and key decisions for the Financial Transaction Aggregator. Setup and usage: [README](../README.md).

## Architecture

```
Bank / Card / Investment Services ──► Kafka ──► Aggregator ──► PostgreSQL
                                                      │
                                                   REST API
```

- **Source services** — simulate institution feeds, publish typed events
- **Aggregator** — consume, normalize, categorize, persist, expose APIs
- **Shared modules** — `common-events` (DTOs, topics) and `common-kafka` (producer/consumer infra) avoid duplication across four services

## Ingestion

```
Kafka → SourceTransactionHandler → Normalizer → CategoryEnricher → saveIfNotExists
```

- Each source has its own event shape and normalizer; all map to a unified `Transaction` entity
- **Amounts:** income positive, expenses/investments negative
- **Categorization:** ordered `CategorizationRule` beans match keywords in descriptions; add new rules without changing `CategorizationService`
- **Idempotency:** unique `(source, external_transaction_id)` — safe for Kafka replays

## API & Data Model

- No `Customer` entity — `customerId` is a field on transactions; `/customers/{id}/...` routes are read-model analytics views
- **Controllers:** `TransactionController` (list/filter), `CustomerAnalyticsController` (dashboard, summary)
- Fixed filters use dedicated repository methods — JPA Specifications would be overkill here

## Kafka & Resilience

| Topic | `bank-transactions`, `card-transactions`, `investment-transactions` + `*.DLT` |
| Producers | `acks=all`, idempotent, 3 retries |
| Consumers | 3 retries (1s backoff) for transient errors; poison pills → DLT |

## Security

API-key auth on the aggregator only (`X-API-Key`, BCrypt hashes in DB). Per-client rate limiting (in-memory, 100 req/min). Source services have no public HTTP API.

## Quality & Deployment

- Tests: unit, WebMvc, JPA, embedded Kafka; aggregator enforces 80% line coverage (`mvn verify`)
- CI: GitHub Actions on `main` / `dev`
- Multi-stage Dockerfiles (non-root user), full stack via `docker compose up --build`

## Trade-offs

| Choice | Why | Limit |
|--------|-----|-------|
| Kafka | Decouples sources | Broker ops overhead |
| Simulators | No external deps | Not real bank APIs |
| In-memory rate limit | Simple | Needs Redis for multi-replica |
| Keyword categorization | Easy to extend | Less accurate than ML |
