package com.adrianh.batch.writer;

import com.adrianh.batch.exception.OrderProcessingException;
import com.adrianh.batch.model.Order;
import com.adrianh.batch.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemWriter;

import java.util.List;

/**
 * Writer that persists validated {@link Order} entities to the H2 database.
 * <p>
 * Uses the {@link OrderRepository} (Spring Data JPA) for persistence operations.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class OrderWriter implements ItemWriter<Order> {

    private final OrderRepository orderRepository;

    /**
     * Writes a list of validated orders to the database.
     *
     * @param orders the list of orders to persist
     * @throws OrderProcessingException if an error occurs during persistence
     */
    @Override
    public void write(List<? extends Order> orders) {
        try {
            orderRepository.saveAll(orders);
            log.info("Successfully wrote {} orders to database", orders.size());
        } catch (Exception e) {
            log.error("Error writing orders to database: {}", e.getMessage());
            throw new OrderProcessingException("Failed to write orders to database", e);
        }
    }
}
