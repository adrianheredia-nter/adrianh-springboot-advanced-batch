package com.adrianh.batch.config;

import com.adrianh.batch.model.Order;
import com.adrianh.batch.repository.OrderRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the batch job configuration.
 * <p>
 * Tests the complete job execution using a test CSV file with known data,
 * verifying that orders are correctly processed, persisted, and invalid
 * orders are properly rejected.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
@SpringBatchTest
@SpringBootTest
class BatchJobTest {

    @Autowired
    private JobLauncherTestUtils jobLauncherTestUtils;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private Job orderProcessingJob;

    @Test
    @DisplayName("Should execute complete job successfully")
    void shouldExecuteCompleteJobSuccessfully() throws Exception {
        jobLauncherTestUtils.setJob(orderProcessingJob);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        JobExecution jobExecution = jobLauncherTestUtils.launchJob(jobParameters);

        assertEquals(BatchStatus.COMPLETED, jobExecution.getStatus());
    }

    @Test
    @DisplayName("Should persist only valid orders in database")
    void shouldPersistOnlyValidOrders() throws Exception {
        orderRepository.deleteAll();
        jobLauncherTestUtils.setJob(orderProcessingJob);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncherTestUtils.launchJob(jobParameters);

        List<Order> orders = orderRepository.findAll();
        // Test CSV has 5 records: 3 valid (IDs 1,2,5), 2 invalid (ID 3 total=0, ID 4 total=-25)
        assertEquals(3, orders.size());
    }

    @Test
    @DisplayName("Should convert customer names to uppercase")
    void shouldConvertCustomerNamesToUppercase() throws Exception {
        orderRepository.deleteAll();
        jobLauncherTestUtils.setJob(orderProcessingJob);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncherTestUtils.launchJob(jobParameters);

        List<Order> orders = orderRepository.findAll();
        assertTrue(orders.stream()
                .allMatch(order -> order.getCustomerName().equals(order.getCustomerName().toUpperCase())));
    }

    @Test
    @DisplayName("Should reject orders with non-positive totals")
    void shouldRejectOrdersWithNonPositiveTotals() throws Exception {
        orderRepository.deleteAll();
        jobLauncherTestUtils.setJob(orderProcessingJob);

        JobParameters jobParameters = new JobParametersBuilder()
                .addLong("timestamp", System.currentTimeMillis())
                .toJobParameters();

        jobLauncherTestUtils.launchJob(jobParameters);

        List<Order> orders = orderRepository.findAll();
        assertTrue(orders.stream()
                .noneMatch(order -> order.getOrderTotal().doubleValue() <= 0));
    }

    @Test
    @DisplayName("Should execute read-process-write step successfully")
    void shouldExecuteReadProcessWriteStepSuccessfully() throws Exception {
        jobLauncherTestUtils.setJob(orderProcessingJob);

        JobExecution stepExecution = jobLauncherTestUtils.launchStep("readProcessWriteStep");

        assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());
    }

    @Test
    @DisplayName("Should execute summary step successfully")
    void shouldExecuteSummaryStepSuccessfully() throws Exception {
        jobLauncherTestUtils.setJob(orderProcessingJob);

        JobExecution stepExecution = jobLauncherTestUtils.launchStep("summaryStep");

        assertEquals(BatchStatus.COMPLETED, stepExecution.getStatus());
    }
}
