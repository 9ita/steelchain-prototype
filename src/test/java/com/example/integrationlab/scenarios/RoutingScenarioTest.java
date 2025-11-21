package com.example.integrationlab.scenarios;

import com.example.integrationlab.domain.Order;
import com.example.integrationlab.domain.OrderType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.messaging.support.MessageBuilder;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class RoutingScenarioTest {

    @Autowired
    private QueueChannel routingInputChannel;

    @Autowired
    private QueueChannel electronicsChannel;

    @Autowired
    private QueueChannel booksChannel;

    @Autowired
    private QueueChannel discardChannel;

    @Test
    void routesOrdersByType() {
        routingInputChannel.send(MessageBuilder.withPayload(new Order(BigDecimal.valueOf(1500), OrderType.ELECTRONICS, false)).build());
        routingInputChannel.send(MessageBuilder.withPayload(new Order(BigDecimal.valueOf(2000), OrderType.BOOKS, true)).build());
        routingInputChannel.send(MessageBuilder.withPayload(new Order(BigDecimal.valueOf(10), OrderType.BOOKS, false)).build());

        assertThat(electronicsChannel.receive(1000)).isNotNull();
        assertThat(booksChannel.receive(1000)).isNotNull();
        assertThat(discardChannel.receive(1000)).isNotNull();
    }
}
