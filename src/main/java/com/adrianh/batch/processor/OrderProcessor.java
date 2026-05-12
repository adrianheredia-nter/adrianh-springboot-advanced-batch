package com.adrianh.batch.processor;

import com.adrianh.batch.constant.BatchConstants;
import com.adrianh.batch.exception.InvalidOrderException;
import com.adrianh.batch.model.Order;
import com.adrianh.batch.model.OrderCsv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Processor that validates and transforms raw CSV order data into {@link Order} entities.
 * <p>
 * Validation rules:
 * <ul>
 *     <li>Orders with a total less than or equal to 0 are rejected (returns {@code null}).</li>
 *     <li>Customer names are converted to uppercase for uniformity.</li>
 *     <li>Date strings are parsed and validated.</li>
 *     <li>Numeric fields are validated for correct format.</li>
 * </ul>
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
@Slf4j
public class OrderProcessor implements ItemProcessor<OrderCsv, Order> {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern(BatchConstants.DATE_FORMAT);

    private int acceptedCount = 0;
    private int rejectedCount = 0;

    /**
     * Processes a single CSV order record.
     * <p>
     * Returns {@code null} if the order should be discarded (total &lt;= 0),
     * otherwise returns a validated and transformed {@link Order} entity.
     * </p>
     *
     * @param orderCsv the raw order data from CSV
     * @return the processed {@link Order}, or {@code null} if rejected
     * @throws InvalidOrderException if the order data is malformed
     */
    @Override
    public Order process(OrderCsv orderCsv) {
        log.debug("Processing order: {}", orderCsv);

        Long orderId = parseOrderId(orderCsv.getOrderId());
        BigDecimal orderTotal = parseOrderTotal(orderCsv.getOrderTotal());

        if (orderTotal.compareTo(BigDecimal.ZERO) <= 0) {
            rejectedCount++;
            log.warn(BatchConstants.LOG_ORDER_REJECTED, orderId);
            return null;
        }

        LocalDate orderDate = parseOrderDate(orderCsv.getOrderDate());
        String customerName = orderCsv.getCustomerName().trim().toUpperCase();

        Order order = Order.builder()
                .orderId(orderId)
                .customerName(customerName)
                .orderDate(orderDate)
                .orderTotal(orderTotal)
                .build();

        acceptedCount++;
        log.debug(BatchConstants.LOG_ORDER_PROCESSED, order);

        return order;
    }

    /**
     * Returns the count of accepted orders.
     *
     * @return number of accepted orders
     */
    public int getAcceptedCount() {
        return acceptedCount;
    }

    /**
     * Returns the count of rejected orders.
     *
     * @return number of rejected orders
     */
    public int getRejectedCount() {
        return rejectedCount;
    }

    /**
     * Parses the order ID from a string value.
     *
     * @param value the raw order ID string
     * @return the parsed order ID
     * @throws InvalidOrderException if the value is not a valid number
     */
    private Long parseOrderId(String value) {
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidOrderException("Invalid order ID: " + value, e);
        }
    }

    /**
     * Parses the order total from a string value.
     *
     * @param value the raw order total string
     * @return the parsed order total
     * @throws InvalidOrderException if the value is not a valid decimal number
     */
    private BigDecimal parseOrderTotal(String value) {
        try {
            return new BigDecimal(value.trim());
        } catch (NumberFormatException e) {
            throw new InvalidOrderException("Invalid order total: " + value, e);
        }
    }

    /**
     * Parses the order date from a string value.
     *
     * @param value the raw order date string
     * @return the parsed order date
     * @throws InvalidOrderException if the value is not a valid date
     */
    private LocalDate parseOrderDate(String value) {
        try {
            return LocalDate.parse(value.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            throw new InvalidOrderException("Invalid order date: " + value, e);
        }
    }
}
