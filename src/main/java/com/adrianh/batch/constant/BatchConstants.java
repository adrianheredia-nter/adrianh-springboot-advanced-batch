package com.adrianh.batch.constant;

/**
 * Constants used throughout the batch processing application.
 * <p>
 * Centralizes all literal values to improve maintainability and avoid magic numbers/strings.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
public final class BatchConstants {

    private BatchConstants() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    // Job and Step names
    public static final String JOB_NAME = "orderProcessingJob";
    public static final String STEP_READ_PROCESS_WRITE = "readProcessWriteStep";
    public static final String STEP_SUMMARY = "summaryStep";

    // CSV field names
    public static final String FIELD_ORDER_ID = "orderId";
    public static final String FIELD_CUSTOMER_NAME = "customerName";
    public static final String FIELD_ORDER_DATE = "orderDate";
    public static final String FIELD_ORDER_TOTAL = "orderTotal";

    // CSV column indices
    public static final int CSV_COLUMN_COUNT = 4;

    // Chunk size
    public static final int CHUNK_SIZE = 15;

    // Skip and retry limits
    public static final int SKIP_LIMIT = 10;
    public static final int RETRY_LIMIT = 3;

    // Date format
    public static final String DATE_FORMAT = "yyyy-MM-dd";

    // Log messages
    public static final String LOG_JOB_STARTED = "Order processing job started";
    public static final String LOG_JOB_COMPLETED = "Order processing job completed";
    public static final String LOG_ORDER_REJECTED = "Order rejected - total is less than or equal to 0: {}";
    public static final String LOG_ORDER_PROCESSED = "Order processed successfully: {}";
    public static final String LOG_SUMMARY_HEADER = "========== BATCH EXECUTION SUMMARY ==========";
    public static final String LOG_SUMMARY_FOOTER = "==============================================";
    public static final String LOG_TOTAL_PROCESSED = "Total orders processed: {}";
    public static final String LOG_ACCEPTED = "Orders accepted: {}";
    public static final String LOG_REJECTED = "Orders rejected: {}";

    // Cron expression for midnight execution
    public static final String CRON_MIDNIGHT = "0 0 0 * * ?";
}
