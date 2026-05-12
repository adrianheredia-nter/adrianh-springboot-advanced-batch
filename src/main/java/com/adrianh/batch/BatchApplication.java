package com.adrianh.batch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main application class for the Spring Batch order processing system.
 * <p>
 * This application reads customer orders from a CSV file, validates and transforms
 * them, and persists valid orders into an H2 database. It also provides a summary
 * of the batch execution including accepted and rejected order counts.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
@SpringBootApplication
@EnableScheduling
public class BatchApplication {

    /**
     * Application entry point.
     *
     * @param args command-line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(BatchApplication.class, args);
    }
}
