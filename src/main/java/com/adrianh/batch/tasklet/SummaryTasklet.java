package com.adrianh.batch.tasklet;

import com.adrianh.batch.constant.BatchConstants;
import com.adrianh.batch.processor.OrderProcessor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

/**
 * Tasklet that prints a summary of the batch execution to the console.
 * <p>
 * Displays the total number of orders processed, the count of accepted orders,
 * and the count of rejected orders.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
@Slf4j
@RequiredArgsConstructor
public class SummaryTasklet implements Tasklet {

    private final OrderProcessor orderProcessor;

    /**
     * Executes the summary tasklet, printing batch execution statistics.
     *
     * @param contribution the step contribution
     * @param chunkContext the chunk context
     * @return {@link RepeatStatus#FINISHED} to indicate the tasklet is complete
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        int accepted = orderProcessor.getAcceptedCount();
        int rejected = orderProcessor.getRejectedCount();
        int total = accepted + rejected;

        log.info(BatchConstants.LOG_SUMMARY_HEADER);
        log.info(BatchConstants.LOG_TOTAL_PROCESSED, total);
        log.info(BatchConstants.LOG_ACCEPTED, accepted);
        log.info(BatchConstants.LOG_REJECTED, rejected);
        log.info(BatchConstants.LOG_SUMMARY_FOOTER);

        return RepeatStatus.FINISHED;
    }
}
