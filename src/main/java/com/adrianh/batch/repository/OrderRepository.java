package com.adrianh.batch.repository;

import com.adrianh.batch.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for {@link Order} entities.
 * <p>
 * Provides CRUD operations and custom queries for the {@code orders} table.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
}
