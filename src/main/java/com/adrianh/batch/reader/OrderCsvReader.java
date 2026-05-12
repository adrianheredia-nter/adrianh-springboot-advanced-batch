package com.adrianh.batch.reader;

import com.adrianh.batch.constant.BatchConstants;
import com.adrianh.batch.model.OrderCsv;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.core.io.Resource;

/**
 * Custom CSV reader for order records.
 * <p>
 * Reads the {@code pedidos.csv} file line by line, tokenizing each line into
 * the four expected columns: orderId, customerName, orderDate, and orderTotal.
 * </p>
 *
 * @author adrianh
 * @version 1.0.0
 */
@Slf4j
public class OrderCsvReader extends FlatFileItemReader<OrderCsv> {

    /**
     * Constructs an OrderCsvReader configured to read the given CSV resource.
     *
     * @param resource the CSV file resource to read
     */
    public OrderCsvReader(Resource resource) {
        setName("orderCsvReader");
        setResource(resource);
        setLineMapper(createLineMapper());
        setSaveState(true);
        log.info("OrderCsvReader initialized with resource: {}", resource);
    }

    /**
     * Creates the line mapper that tokenizes CSV lines and maps them to {@link OrderCsv} objects.
     *
     * @return configured {@link DefaultLineMapper} instance
     */
    private DefaultLineMapper<OrderCsv> createLineMapper() {
        DefaultLineMapper<OrderCsv> lineMapper = new DefaultLineMapper<>();

        DelimitedLineTokenizer tokenizer = new DelimitedLineTokenizer();
        tokenizer.setDelimiter(DelimitedLineTokenizer.DELIMITER_COMMA);
        tokenizer.setNames(
                BatchConstants.FIELD_ORDER_ID,
                BatchConstants.FIELD_CUSTOMER_NAME,
                BatchConstants.FIELD_ORDER_DATE,
                BatchConstants.FIELD_ORDER_TOTAL
        );
        tokenizer.setStrict(true);

        BeanWrapperFieldSetMapper<OrderCsv> fieldSetMapper = new BeanWrapperFieldSetMapper<>();
        fieldSetMapper.setTargetType(OrderCsv.class);

        lineMapper.setLineTokenizer(tokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }
}
