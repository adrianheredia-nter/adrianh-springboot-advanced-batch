package com.adrianh.batch.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Entity representing a customer order.
 * <p>
 * Maps to the {@code orders} table in the H2 database. Each order contains
 * an identifier, customer name, order date, and total amount.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    /**
     * Unique identifier for the order.
     */
    @Id
    @Column(name = "order_id")
    private Long orderId;

    /**
     * Name of the customer who placed the order.
     */
    @NotBlank
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    /**
     * Date when the order was placed.
     */
    @NotNull
    @Column(name = "order_date", nullable = false)
    private LocalDate orderDate;

    /**
     * Total monetary amount of the order.
     */
    @NotNull
    @Positive
    @Column(name = "order_total", nullable = false)
    private BigDecimal orderTotal;
}
