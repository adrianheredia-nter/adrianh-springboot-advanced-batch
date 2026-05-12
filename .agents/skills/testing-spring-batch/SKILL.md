---
name: testing-spring-batch
description: Test the Spring Batch order processing application end-to-end. Use when verifying batch job execution, CSV processing, database persistence, or summary output.
---

# Testing Spring Batch Order Processing

## Prerequisites

- JDK 17
- Maven 3.6+ (install with `sudo apt-get install -y maven` if missing)

## Build & Unit Tests

```bash
cd /home/ubuntu/adrianh-springboot-advanced-batch
mvn clean test
```

Expected: `Tests run: 15, Failures: 0, Errors: 0, Skipped: 0 — BUILD SUCCESS`

## Running the Batch Job Manually

The job is disabled on startup by default (`spring.batch.job.enabled=false`). To trigger it:

```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.batch.job.enabled=true"
```

Do NOT modify `application.properties` to enable it — use the command-line override instead.

## Key Verification Points

### 1. Summary Counts

After the job runs, look for the summary in console output:

```
========== BATCH EXECUTION SUMMARY ==========
Total orders processed: <readCount>
Orders accepted: <writeCount>
Orders rejected: <filterCount>
==============================================
```

With the default `pedidos.csv` (55 records, 8 invalid with total ≤ 0):
- Total: 55
- Accepted: 47
- Rejected: 8

Invalid order IDs: 11, 15, 25, 33, 38, 43, 45, 55 (total = 0.00 or negative)

### 2. Database Contents

The app uses H2 in-memory database. While the app is running, access the H2 console at:
`http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:batchdb`
- Username: `sa`
- Password: (empty)

Verify:
- Only valid orders are persisted (no orders with total ≤ 0)
- All customer names are UPPERCASE
- Row count matches accepted count

### 3. Rejection Logs

Grep for rejection warnings:
```bash
grep "Order rejected" <output-file>
```

Each rejected order logs: `Order rejected - total is less than or equal to 0: <orderId>`

## Architecture Notes

- **Summary counts** use `StepExecution.writeCount` and `StepExecution.filterCount` (not manual counters). This ensures accuracy during fault-tolerant chunk reprocessing.
- **OrderProcessor** is `@StepScope` — a new instance per step execution.
- **SummaryTasklet** reads metrics from the `JobExecution`'s step executions by matching step name `readProcessWriteStep`.
- **Chunk size**: 15 items per transaction.
- **Fault tolerance**: Skips `FlatFileParseException` and `InvalidOrderException` (limit 10), retries `OrderProcessingException` (limit 3).

## Test CSV

The test CSV at `src/test/resources/data/pedidos-test.csv` has 5 records (3 valid, 2 invalid) for faster integration tests.

## Scheduled Execution

The `BatchScheduler` runs the job at midnight daily (`0 0 0 * * ?`). This cannot be easily tested in a short session — verify by inspecting the `@Scheduled` annotation in `BatchScheduler.java`.

## Devin Secrets Needed

None — the application uses H2 in-memory database with no external dependencies.
