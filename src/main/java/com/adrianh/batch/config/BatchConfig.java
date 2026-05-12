package com.adrianh.batch.config;

import com.adrianh.batch.constant.BatchConstants;
import com.adrianh.batch.exception.InvalidOrderException;
import com.adrianh.batch.exception.OrderProcessingException;
import com.adrianh.batch.model.Order;
import com.adrianh.batch.model.OrderCsv;
import com.adrianh.batch.processor.OrderProcessor;
import com.adrianh.batch.reader.OrderCsvReader;
import com.adrianh.batch.repository.OrderRepository;
import com.adrianh.batch.tasklet.SummaryTasklet;
import com.adrianh.batch.writer.OrderWriter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

/**
 * Batch configuration class that defines the job, steps, and all batch components.
 * <p>
 * The job consists of two steps:
 * <ol>
 *     <li><strong>Step 1</strong>: Reads orders from a CSV file, validates and transforms them,
 *         and writes valid orders to the H2 database in chunks of 15.</li>
 *     <li><strong>Step 2</strong>: Executes a summary tasklet that logs the execution statistics.</li>
 * </ol>
 * The step is configured with fault tolerance, including skip and retry policies
 * for handling malformed data and transient errors.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
@Slf4j
public class BatchConfig {

    private final JobBuilderFactory jobBuilderFactory;
    private final StepBuilderFactory stepBuilderFactory;
    private final OrderRepository orderRepository;

    @Value("${batch.input.file}")
    private Resource inputResource;

    /**
     * Creates the CSV item reader bean.
     *
     * @return configured {@link OrderCsvReader}
     */
    @Bean
    public OrderCsvReader orderReader() {
        return new OrderCsvReader(inputResource);
    }

    /**
     * Creates the order processor bean.
     * <p>
     * Scoped to the step to ensure a fresh instance is created for each step execution,
     * preventing counter accumulation across job runs.
     * </p>
     *
     * @return configured {@link OrderProcessor}
     */
    @Bean
    @StepScope
    public OrderProcessor orderProcessor() {
        return new OrderProcessor();
    }

    /**
     * Creates the order writer bean.
     *
     * @return configured {@link OrderWriter}
     */
    @Bean
    public OrderWriter orderWriter() {
        return new OrderWriter(orderRepository);
    }

    /**
     * Creates the summary tasklet bean.
     * <p>
     * Uses Spring Batch's {@code StepExecution} metrics instead of processor instance
     * variables, ensuring accurate counts even during fault-tolerant reprocessing.
     * </p>
     *
     * @return configured {@link SummaryTasklet}
     */
    @Bean
    public SummaryTasklet summaryTasklet() {
        return new SummaryTasklet();
    }

    /**
     * Configures Step 1: read, process, and write orders in chunks of 15.
     * <p>
     * Fault tolerance is enabled with skip and retry policies:
     * <ul>
     *     <li>Skips {@link FlatFileParseException} and {@link InvalidOrderException}
     *         up to the configured skip limit.</li>
     *     <li>Retries {@link OrderProcessingException} up to the configured retry limit.</li>
     * </ul>
     * </p>
     *
     * @return configured read-process-write {@link Step}
     */
    @Bean
    public Step readProcessWriteStep() {
        return stepBuilderFactory.get(BatchConstants.STEP_READ_PROCESS_WRITE)
                .<OrderCsv, Order>chunk(BatchConstants.CHUNK_SIZE)
                .reader(orderReader())
                .processor(orderProcessor())
                .writer(orderWriter())
                .faultTolerant()
                .skipLimit(BatchConstants.SKIP_LIMIT)
                .skip(FlatFileParseException.class)
                .skip(InvalidOrderException.class)
                .retryLimit(BatchConstants.RETRY_LIMIT)
                .retry(OrderProcessingException.class)
                .build();
    }

    /**
     * Configures Step 2: executes the summary tasklet.
     *
     * @return configured summary {@link Step}
     */
    @Bean
    public Step summaryStep() {
        return stepBuilderFactory.get(BatchConstants.STEP_SUMMARY)
                .tasklet(summaryTasklet())
                .build();
    }

    /**
     * Configures the main batch job with both steps in sequence.
     * <p>
     * Uses {@link RunIdIncrementer} to allow the job to be re-executed
     * with a unique run ID each time, supporting restartability.
     * </p>
     *
     * @return configured {@link Job}
     */
    @Bean
    public Job orderProcessingJob() {
        return jobBuilderFactory.get(BatchConstants.JOB_NAME)
                .incrementer(new RunIdIncrementer())
                .start(readProcessWriteStep())
                .next(summaryStep())
                .build();
    }
}
