package com.example.integrationlab.scenarios;

import com.example.integrationlab.domain.TotalWordCountReport;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AggregationScenarioTest {

    @Autowired
    private QueueChannel aggregationInputChannel;

    @Autowired
    private QueueChannel aggregatedResultsChannel;

    @Test
    void aggregatesWordCounts() {
        aggregationInputChannel.send(MessageBuilder.withPayload(List.of("first sentence", "another one")).build());

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            var message = aggregatedResultsChannel.receive(0);
            assertThat(message).isNotNull();
            TotalWordCountReport report = (TotalWordCountReport) message.getPayload();
            assertThat(report.totalSentences()).isEqualTo(2);
            assertThat(report.totalWords()).isGreaterThan(0);
        });
    }
}
