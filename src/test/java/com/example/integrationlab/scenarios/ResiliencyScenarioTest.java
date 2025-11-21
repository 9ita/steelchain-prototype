package com.example.integrationlab.scenarios;

import com.example.integrationlab.service.UnstableService;
import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.PollableChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@SpringBootTest
class ResiliencyScenarioTest {

    @MockBean
    private UnstableService unstableService;

    @Autowired
    private MessageChannel resiliencyInputChannel;

    @Autowired
    private PollableChannel parkingLotChannel;

    @Test
    void sendsToParkingLotAfterRetries() {
        when(unstableService.call(Mockito.anyString())).thenThrow(new IllegalStateException("always fail"));

        resiliencyInputChannel.send(MessageBuilder.withPayload("payload").build());

        Awaitility.await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            assertThat(parkingLotChannel.receive(0)).isNotNull();
        });
    }
}
