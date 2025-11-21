package com.example.integrationlab.scenarios.basic;

import com.example.integrationlab.domain.ProcessedText;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.Transformers;

import java.time.Instant;

@Configuration
public class BasicScenarioConfiguration {

    @Bean
    public IntegrationFlow basicFlow() {
        return IntegrationFlow.from("basicInputChannel")
                .transform((String payload) -> new ProcessedText(payload, payload.toUpperCase(), Instant.now()))
                .wireTap("loggingChannel")
                .transform(Transformers.toJson())
                .wireTap("loggingChannel")
                .handle(String.class, (json, headers) -> json)
                .wireTap("loggingChannel")
                .get();


    }
}
