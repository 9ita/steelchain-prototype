package com.example.integrationlab.scenarios.adapter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.file.dsl.Files;
import org.springframework.integration.jdbc.JdbcMessageHandler;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.messaging.MessageHandler;

import java.io.File;

@Configuration
public class AdapterScenarioConfiguration {

    @Bean
    public IntegrationFlow fileToJdbcFlow(JdbcTemplate jdbcTemplate,
                                          @Value("${integrationlab.file-input:input/files}") String inputDir) {
        File directory = new File(inputDir);
        directory.mkdirs();

        return IntegrationFlow.from(Files.inboundAdapter(directory)
                                .patternFilter("*.txt"),
                        e -> e.poller(p -> p.fixedDelay(1000)))
                .transform(Files.toStringTransformer())

                // CHANGE HERE: Use wireTap to side-effect to DB
                .wireTap(flow -> flow.handle(jdbcMessageHandler(jdbcTemplate)))

                // The main flow continues here to move the file
                .handle(Files.outboundAdapter(new File("processed"))
                        .autoCreateDirectory(true)
                        .deleteSourceFiles(true))
                .get();
    }

    @Bean
    public MessageHandler jdbcMessageHandler(JdbcTemplate jdbcTemplate) {
        // Note: Ensure the :payload corresponds to the String created by toStringTransformer
        String sql = "INSERT INTO AUDIT_LOG (FILENAME, CONTENTS) VALUES (:headers[file_name], :payload)";
        return new JdbcMessageHandler(jdbcTemplate, sql);
    }
}