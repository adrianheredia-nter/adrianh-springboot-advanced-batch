package com.adrianh.batch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a raw order record read from the CSV file.
 * <p>
 * This object holds the raw string values before they are validated
 * and transformed into an {@link Order} entity.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderCsv {

    /**
     * Raw order ID from the CSV.
     */
    private String orderId;

    /**
     * Raw customer name from the CSV.
     */
    private String customerName;

    /**
     * Raw order date string from the CSV.
     */
    private String orderDate;

    /**
     * Raw order total string from the CSV.
     */
    private String orderTotal;
}
