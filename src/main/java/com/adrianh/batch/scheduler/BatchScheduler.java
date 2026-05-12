package com.adrianh.batch.scheduler;

import com.adrianh.batch.constant.BatchConstants;
import com.adrianh.batch.exception.OrderProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * Scheduler that triggers the order processing batch job automatically at midnight.
 * <p>
 * Uses Spring's {@code @Scheduled} annotation with a cron expression to execute
 * the job daily at 00:00. Each execution receives a unique timestamp parameter
 * to allow restartability.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class BatchScheduler {

    private final JobLauncher jobLauncher;
    private final Job orderProcessingJob;

    /**
     * Executes the order processing job at midnight every day.
     * <p>
     * Each execution is parameterized with the current timestamp to ensure
     * unique job instances for restartability.
     * </p>
     */
    @Scheduled(cron = BatchConstants.CRON_MIDNIGHT)
    public void executeJob() {
        try {
            JobParameters jobParameters = new JobParametersBuilder()
                    .addLong("timestamp", System.currentTimeMillis())
                    .toJobParameters();

            log.info(BatchConstants.LOG_JOB_STARTED);
            jobLauncher.run(orderProcessingJob, jobParameters);
            log.info(BatchConstants.LOG_JOB_COMPLETED);
        } catch (Exception e) {
            log.error("Error executing scheduled batch job: {}", e.getMessage(), e);
            throw new OrderProcessingException("Scheduled job execution failed", e);
        }
    }
}
