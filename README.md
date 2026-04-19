# Prudente

Prudente is a Spring Boot personal finance management API for managing accounts, categorized transactions, budgets, analytics, and report exports. The application currently runs as an in-memory backend prototype focused on demonstrating modern Java features in a realistic finance domain.

## Summary

The API supports:

- account creation and retrieval
- debit, credit, and transfer transactions
- category-aware spending transactions
- budget creation and expense tracking by category
- transaction analytics and category spending summaries
- monthly financial reports and batch report generation
- CSV and text export

Application state is stored in memory, so data is reset whenever the application restarts.

## Tech Stack

- Java 21
- Spring Boot 3.2.3
- Spring Web
- Spring Boot Actuator
- Maven

## Project Structure

```text
src/main/java/com/pfm
|- analytics/   Analytics services, DTOs, and mappers
|- api/         Interface contracts
|- concurrency/ Batch processing services
|- domain/      Domain objects
|- dto/         Request DTOs
|- i18n/        Localisation services
|- io/          File export services
|- model/       Core models and enums
|- report/      Report services and DTOs
|- service/     Core business logic
|- util/        Mappers and formatters
`- web/         REST controllers
```

## Prerequisites

You need:

- JDK 21
- Maven on your `PATH`
- Git

Verify:

```bash
java -version
mvn -version
git --version
```

## Clone

```bash
git clone https://github.com/Feawos/Prudente
cd Prudente
```

## Run

Start the API:

```bash
mvn spring-boot:run
```

Default base URL:

```text
http://localhost:8080
```

Build and run the jar:

```bash
mvn clean package
java -jar target/Prudente-1.0-SNAPSHOT.jar
```

## Core Endpoints

### Accounts

- `GET /api/accounts`
- `POST /api/accounts`
- `GET /api/accounts/{id}`

Example request:

```json
{
  "id": "acc-001",
  "name": "Main Account",
  "type": "CHECKING",
  "balance": 1000.00,
  "currency": "EUR"
}
```

### Transactions

- `GET /api/transactions`
- `POST /api/transactions/debit`
- `POST /api/transactions/credit`
- `POST /api/transactions/transfer`
- `GET /api/transactions/export`
- `GET /export/transactions`

Debit or credit request:

```json
{
  "type": "DEBIT",
  "amount": 45.50,
  "currency": "EUR",
  "date": "2026-04-19",
  "accountId": "acc-001",
  "category": "FOOD"
}
```

Transfer request:

```json
{
  "amount": 150.00,
  "currency": "EUR",
  "fromAccount": "acc-001",
  "toAccount": "acc-002"
}
```

Notes:

- debit and credit transactions store a category
- if category is omitted for debit or credit, the controller defaults it to `OTHER`
- transfer transactions do not carry a category

### Budgets

- `GET /api/budgets`
- `POST /api/budgets`
- `POST /api/budgets/{category}/expense?amount=25.00`
- `GET /api/budgets/{category}/status`

Example request:

```json
{
  "category": "FOOD",
  "limitAmount": 300.00,
  "spentAmount": 0.00,
  "currency": "EUR",
  "startDate": "2026-04-01",
  "endDate": "2026-04-30"
}
```

## Analytics Endpoints

### Transaction Analytics

- `GET /api/analytics/transactions`
- `GET /api/analytics/transactions/stats`
- `GET /api/analytics/transactions/daily-totals`
- `GET /api/analytics/transactions/category-spending`
- `GET /api/analytics/transactions/category-spending/top`

Supported filters on `GET /api/analytics/transactions`:

- `type=DEBIT|CREDIT|TRANSFER`
- `currency=EUR|USD|GBP|NGN`
- `category=FOOD|RENT|ENTERTAINMENT|UTILITIES|TRANSPORT|OTHER`
- `fromDate=yyyy-MM-dd`
- `toDate=yyyy-MM-dd`
- `limit=<number>`
- `sortBy=DATE|AMOUNT`
- `direction=ASC|DESC`

Example:

```text
/api/analytics/transactions?type=DEBIT&category=FOOD&sortBy=AMOUNT&direction=DESC
```

### Account Analytics

- `GET /api/analytics/accounts`
- `GET /api/analytics/accounts/highest-balance`

`GET /api/analytics/accounts?sort=balanceDesc` returns balances sorted descending.

### Budget Analytics

- `GET /api/analytics/budgets/status`
- `GET /api/analytics/budgets/{category}/status`
- `GET /api/analytics/budgets/exceeded`

## Reporting Endpoints

- `GET /api/reports/monthly?year=2026&month=4`
- `GET /api/reports/monthly/export?year=2026&month=4`
- `POST /api/reports/monthly/batch`

Batch request body:

```json
["2026-01", "2026-02", "2026-03"]
```

The monthly report currently includes:

- total credits
- total debits
- net cash flow
- transaction count
- category spending summary
- account balances
- budget status snapshot

## Supported Values

### Currencies

- `EUR`
- `USD`
- `GBP`
- `NGN`

### Categories

- `FOOD`
- `RENT`
- `ENTERTAINMENT`
- `UTILITIES`
- `TRANSPORT`
- `OTHER`

## Current Implementation Notes

- data is stored in memory inside service-layer collections
- there is no database integration yet
- transaction categories are stored for debit and credit flows only
- monthly batch reports are generated with `ExecutorService`
- report file export uses Java NIO2 and writes to `exports/`
- localisation infrastructure exists via message bundles and `LocalizationService`
- exception handling for invalid input and export/report failures is centralized with `@ControllerAdvice`

## Known Gaps

- no persistent storage
- no automated test suite in the repository yet
- no OpenAPI/Swagger documentation
- localisation is implemented as infrastructure but not yet deeply applied to every response/export

