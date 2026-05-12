package com.adrianh.batch.processor;

import com.adrianh.batch.exception.InvalidOrderException;
import com.adrianh.batch.model.Order;
import com.adrianh.batch.model.OrderCsv;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit tests for {@link OrderProcessor}.
 * <p>
 * Tests cover validation rules, transformation logic, and error handling.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
class OrderProcessorTest {

    private OrderProcessor processor;

    @BeforeEach
    void setUp() {
        processor = new OrderProcessor();
    }

    @Test
    @DisplayName("Should process valid order successfully")
    void shouldProcessValidOrder() {
        OrderCsv orderCsv = OrderCsv.builder()
                .orderId("1")
                .customerName("Juan Pérez")
                .orderDate("2023-08-01")
                .orderTotal("150.00")
                .build();

        Order result = processor.process(orderCsv);

        assertNotNull(result);
        assertEquals(1L, result.getOrderId());
        assertEquals("JUAN PÉREZ", result.getCustomerName());
        assertEquals("150.00", result.getOrderTotal().toPlainString());
    }

    @Test
    @DisplayName("Should convert customer name to uppercase")
    void shouldConvertCustomerNameToUpperCase() {
        OrderCsv orderCsv = OrderCsv.builder()
                .orderId("2")
                .customerName("maria garcía")
                .orderDate("2023-08-02")
                .orderTotal("230.50")
                .build();

        Order result = processor.process(orderCsv);

        assertNotNull(result);
        assertEquals("MARIA GARCÍA", result.getCustomerName());
    }

    @Test
    @DisplayName("Should reject order with total equal to zero")
    void shouldRejectOrderWithTotalZero() {
        OrderCsv orderCsv = OrderCsv.builder()
                .orderId("3")
                .customerName("Carlos López")
                .orderDate("2023-08-03")
                .orderTotal("0.00")
                .build();

        Order result = processor.process(orderCsv);

        assertNull(result);
    }

    @Test
    @DisplayName("Should reject order with negative total")
    void shouldRejectOrderWithNegativeTotal() {
        OrderCsv orderCsv = OrderCsv.builder()
                .orderId("4")
                .customerName("Ana Martínez")
                .orderDate("2023-08-04")
                .orderTotal("-25.00")
                .build();

        Order result = processor.process(orderCsv);

        assertNull(result);
    }

    @Test
    @DisplayName("Should throw InvalidOrderException for invalid order ID")
    void shouldThrowExceptionForInvalidOrderId() {
        OrderCsv orderCsv = OrderCsv.builder()
                .orderId("abc")
                .customerName("Test User")
                .orderDate("2023-08-01")
                .orderTotal("100.00")
                .build();

        assertThrows(InvalidOrderException.class, () -> processor.process(orderCsv));
    }

    @Test
    @DisplayName("Should throw InvalidOrderException for invalid order total")
    void shouldThrowExceptionForInvalidOrderTotal() {
        OrderCsv orderCsv = OrderCsv.builder()
                .orderId("5")
                .customerName("Test User")
                .orderDate("2023-08-01")
                .orderTotal("not_a_number")
                .build();

        assertThrows(InvalidOrderException.class, () -> processor.process(orderCsv));
    }

    @Test
    @DisplayName("Should throw InvalidOrderException for invalid date format")
    void shouldThrowExceptionForInvalidDate() {
        OrderCsv orderCsv = OrderCsv.builder()
                .orderId("6")
                .customerName("Test User")
                .orderDate("01-08-2023")
                .orderTotal("100.00")
                .build();

        assertThrows(InvalidOrderException.class, () -> processor.process(orderCsv));
    }

    @Test
    @DisplayName("Should correctly process mix of valid and invalid orders")
    void shouldCorrectlyProcessMixOfOrders() {
        OrderCsv validOrder = OrderCsv.builder()
                .orderId("1")
                .customerName("Valid Customer")
                .orderDate("2023-08-01")
                .orderTotal("100.00")
                .build();

        OrderCsv invalidOrder = OrderCsv.builder()
                .orderId("2")
                .customerName("Invalid Customer")
                .orderDate("2023-08-02")
                .orderTotal("0.00")
                .build();

        Order validResult = processor.process(validOrder);
        Order invalidResult = processor.process(invalidOrder);
        Order validResult2 = processor.process(validOrder);

        assertNotNull(validResult);
        assertNull(invalidResult);
        assertNotNull(validResult2);
    }
}
