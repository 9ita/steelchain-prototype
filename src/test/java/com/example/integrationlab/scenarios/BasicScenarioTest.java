package com.example.integrationlab.scenarios;

import com.example.integrationlab.scenarios.basic.BasicTextGateway;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@SpringBootTest
class BasicScenarioTest {

    @Autowired
    private BasicTextGateway gateway;

    @Test
    void transformsTextAndReturnsJson() {
        String response = gateway.processText("hello world");
        log.info("response: {}", response);
        assertThat(response).contains("HELLO WORLD");
        assertThat(response).contains("timestamp");
    }
}
