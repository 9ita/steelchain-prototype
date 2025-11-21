package com.example.integrationlab.scenarios.resiliency;

import com.example.integrationlab.service.UnstableService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.handler.advice.RequestHandlerRetryAdvice;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Configuration
public class ResiliencyScenarioConfiguration {

    @Bean
    public IntegrationFlow resiliencyFlow(UnstableService unstableService) {
        return IntegrationFlow.from("resiliencyInputChannel")
                .handle(String.class, (payload, headers) -> unstableService.call(payload),
                        e -> e.advice(retryAdvice(parkingLotChannel())))
                .get();
    }

    @Bean
    public RequestHandlerRetryAdvice retryAdvice(MessageChannel parkingLotChannel) {
        RequestHandlerRetryAdvice advice = new RequestHandlerRetryAdvice();
        RetryTemplate template = new RetryTemplate();
        SimpleRetryPolicy policy = new SimpleRetryPolicy(3);
        FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
        backOffPolicy.setBackOffPeriod(200);
        template.setRetryPolicy(policy);
        template.setBackOffPolicy(backOffPolicy);
        advice.setRetryTemplate(template);
        advice.setRecoveryCallback(context -> {
            ErrorMessage errorMessage = new ErrorMessage(context.getLastThrowable());
            parkingLotChannel.send(errorMessage);
            return errorMessage;
        });
        return advice;
    }

    @Bean
    public MessageChannel parkingLotChannel() {
        return new QueueChannel();
    }

    @Bean
    public IntegrationFlow controlBusFlow() {
        return IntegrationFlow.from("controlBusInputChannel")
                .controlBus()
                .get();
    }
}
