package com.example.integrationlab.scenarios.aggregation;

import com.example.integrationlab.domain.TotalWordCountReport;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aggregator.DefaultAggregatingMessageGroupProcessor;
import org.springframework.integration.dsl.AggregatorSpec;
import org.springframework.integration.dsl.IntegrationFlow;

import java.util.List;

@Configuration
public class AggregationScenarioConfiguration {

    @Bean
    public IntegrationFlow aggregationFlow() {
        return IntegrationFlow.from("aggregationInputChannel")
                .split()
                .channel("executorChannel")
                .handle(String.class, (payload, headers) -> payload.split("\\s+").length)
                .aggregate(this::configureAggregator)
                .channel("aggregatedResultsChannel")
                .get();
    }

    private void configureAggregator(AggregatorSpec spec) {
        spec.outputProcessor(new DefaultAggregatingMessageGroupProcessor() {
            private Object aggregatePayloads(List<Object> payloads, java.util.Map<String, Object> headers) {
                int totalWords = payloads.stream().mapToInt(p -> (Integer) p).sum();
                return new TotalWordCountReport(payloads.size(), totalWords);
            }
        }).releaseStrategy(group -> group.size() == group.getSequenceSize())
                .expireGroupsUponCompletion(true)
                .groupTimeout(5000);
    }
}
