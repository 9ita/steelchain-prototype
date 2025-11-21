package com.example.integrationlab.scenarios;

import com.example.integrationlab.scenarios.basic.BasicTextGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BasicScenarioTest {

    @Autowired
    private BasicTextGateway gateway;

    @Test
    void transformsTextAndReturnsJson() {
        String response = gateway.processText("hello world");
        assertThat(response).contains("HELLO WORLD");
        assertThat(response).contains("timestamp");
    }
}
