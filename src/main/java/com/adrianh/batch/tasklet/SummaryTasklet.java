package com.adrianh.batch.tasklet;

import com.adrianh.batch.constant.BatchConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.Collection;

/**
 * Tasklet that prints a summary of the batch execution to the console.
 * <p>
 * Retrieves execution statistics from Spring Batch's {@link StepExecution} metrics
 * for the read-process-write step. This approach is accurate even during fault-tolerant
 * chunk reprocessing and across multiple job runs, as the metrics are managed by the
 * framework's transaction-aware infrastructure.
 * </p>
 * <ul>
 *     <li><strong>Accepted</strong>: {@code writeCount} from the read-process-write step.</li>
 *     <li><strong>Rejected</strong>: {@code filterCount} from the read-process-write step
 *         (items for which the processor returned {@code null}).</li>
 *     <li><strong>Total processed</strong>: accepted + rejected.</li>
 * </ul>
 *
 * @author adrianh
 * @version 1.0.0
 */
@Slf4j
public class SummaryTasklet implements Tasklet {

    /**
     * Executes the summary tasklet, printing batch execution statistics.
     * <p>
     * Reads the {@code writeCount} and {@code filterCount} from the previous
     * step's {@link StepExecution} to determine accepted and rejected orders.
     * </p>
     *
     * @param contribution the step contribution
     * @param chunkContext the chunk context
     * @return {@link RepeatStatus#FINISHED} to indicate the tasklet is complete
     */
    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) {
        JobExecution jobExecution = chunkContext.getStepContext()
                .getStepExecution()
                .getJobExecution();

        Collection<StepExecution> stepExecutions = jobExecution.getStepExecutions();

        int accepted = 0;
        int rejected = 0;

        for (StepExecution stepExecution : stepExecutions) {
            if (BatchConstants.STEP_READ_PROCESS_WRITE.equals(stepExecution.getStepName())) {
                accepted = stepExecution.getWriteCount();
                rejected = stepExecution.getFilterCount();
                break;
            }
        }

        int total = accepted + rejected;

        log.info(BatchConstants.LOG_SUMMARY_HEADER);
        log.info(BatchConstants.LOG_TOTAL_PROCESSED, total);
        log.info(BatchConstants.LOG_ACCEPTED, accepted);
        log.info(BatchConstants.LOG_REJECTED, rejected);
        log.info(BatchConstants.LOG_SUMMARY_FOOTER);

        return RepeatStatus.FINISHED;
    }
}
