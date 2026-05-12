# Spring Boot Advanced Batch - Order Processing System

## Description

Spring Boot Batch application that processes customer orders from a CSV file, validates and transforms the data, and persists valid orders into an H2 in-memory database. The application provides a complete batch processing pipeline with fault tolerance, automatic scheduling, and execution summaries.

## Technologies

- **Java**: 17
- **Spring Boot**: 2.7.18
- **Spring Batch**: Batch processing framework
- **Spring Data JPA**: Data persistence layer
- **Hibernate**: JPA provider
- **H2 Database**: In-memory relational database
- **Lombok**: Boilerplate code reduction
- **Maven**: Build and dependency management
- **JUnit 5**: Testing framework

## Project Structure

```
src/main/java/com/adrianh/batch/
├── BatchApplication.java          # Main application entry point
├── config/
│   └── BatchConfig.java           # Batch job, steps, and component configuration
├── constant/
│   └── BatchConstants.java        # Application-wide constants
├── exception/
│   ├── InvalidOrderException.java # Validation error exception
│   └── OrderProcessingException.java # Processing error exception
├── model/
│   ├── Order.java                 # JPA entity for persisted orders
│   └── OrderCsv.java             # DTO for raw CSV data
├── processor/
│   └── OrderProcessor.java       # Validates and transforms order data
├── reader/
│   └── OrderCsvReader.java       # Reads orders from CSV file
├── repository/
│   └── OrderRepository.java      # Spring Data JPA repository
├── scheduler/
│   └── BatchScheduler.java       # Scheduled job execution at midnight
├── tasklet/
│   └── SummaryTasklet.java       # Prints execution summary
└── writer/
    └── OrderWriter.java          # Persists orders to H2 database

src/main/resources/
├── application.properties         # Application configuration
└── data/
    └── pedidos.csv               # Input CSV with 55 customer orders
```

## Prerequisites

- **JDK 17** or higher
- **Maven 3.6+**

## Setup and Execution

### 1. Clone the Repository

```bash
git clone https://github.com/adrianheredia-nter/adrianh-springboot-advanced-batch.git
cd adrianh-springboot-advanced-batch
```

### 2. Build the Project

```bash
mvn clean install
```

### 3. Run the Application

```bash
mvn spring-boot:run
```

The application starts and the batch job is scheduled to execute automatically at **midnight (00:00)** every day.

### 4. Run the Batch Job Manually

The job is configured with `spring.batch.job.enabled=false` to prevent execution on startup. It runs via the scheduler at midnight. To test it manually, you can change `spring.batch.job.enabled=true` in `application.properties` and restart the application.

### 5. Access H2 Console

While the application is running, access the H2 database console at:

```
http://localhost:8080/h2-console
```

- **JDBC URL**: `jdbc:h2:mem:batchdb`
- **Username**: `sa`
- **Password**: *(empty)*

## Batch Job Details

### Step 1: Read, Process, and Write

| Component | Description |
|-----------|-------------|
| **Reader** | `OrderCsvReader` — Reads `pedidos.csv` line by line using `FlatFileItemReader` |
| **Processor** | `OrderProcessor` — Validates totals (rejects ≤ 0), converts names to uppercase |
| **Writer** | `OrderWriter` — Persists valid orders to H2 via Spring Data JPA |
| **Chunk Size** | 15 items per transaction |

**Fault Tolerance:**
- Skips `FlatFileParseException` and `InvalidOrderException` (up to 10 skips)
- Retries `OrderProcessingException` (up to 3 retries)

### Step 2: Summary Tasklet

Prints an execution summary to the console:

```
========== BATCH EXECUTION SUMMARY ==========
Total orders processed: 55
Orders accepted: 47
Orders rejected: 8
==============================================
```

### Automatic Scheduling

The job is scheduled to run at **midnight every day** using the cron expression `0 0 0 * * ?`.

## CSV File Format

The input file `pedidos.csv` uses comma-separated values with the following columns:

| Column | Description | Example |
|--------|-------------|---------|
| Order ID | Unique identifier | `1` |
| Customer Name | Customer full name | `Juan Pérez` |
| Order Date | Date in yyyy-MM-dd format | `2023-08-01` |
| Order Total | Decimal amount | `150.00` |

Example:
```csv
1,Juan Pérez,2023-08-01,150.00
2,Maria García,2023-08-02,230.50
```

## Running Tests

```bash
mvn test
```

### Test Coverage

- **Unit Tests**: `OrderProcessorTest` — Tests validation rules, transformation logic, and error handling
- **Integration Tests**: `BatchJobTest` — Tests complete job execution, data persistence, and step execution
- **Context Tests**: `BatchApplicationTests` — Verifies Spring Boot context loads correctly

## Configuration Properties

| Property | Description | Default |
|----------|-------------|---------|
| `spring.datasource.url` | H2 database URL | `jdbc:h2:mem:batchdb` |
| `spring.jpa.hibernate.ddl-auto` | Schema generation strategy | `update` |
| `spring.batch.job.enabled` | Auto-run job on startup | `false` |
| `batch.input.file` | Path to input CSV file | `classpath:data/pedidos.csv` |

## Author

Adrian Heredia
