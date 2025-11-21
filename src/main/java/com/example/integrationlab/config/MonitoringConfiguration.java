package com.example.integrationlab.config;

import com.example.integrationlab.domain.ProcessedText;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;

@Configuration
public class MonitoringConfiguration {

    /**
     * 1. 단순 로깅용 구독자
     * - ChannelConfiguration에 정의된 "loggingChannel"에서 메시지를 받아옵니다.
     * - PublishSubscribeChannel이므로 다른 구독자와 상관없이 메시지를 받습니다.
     */
    @Bean
    public IntegrationFlow consoleLoggingFlow() {
        return IntegrationFlow.from("loggingChannel") // 이미 정의된 Bean 이름으로 연결
                .handle(message -> {
                    System.out.println("[시스템 로그] 헤더: " + message.getHeaders());
                    System.out.println("[시스템 로그] 내용: " + message.getPayload());
                })
                .get();
    }

    /**
     * 2. 통계 집계용 구독자 (예시)
     * - 똑같이 "loggingChannel"을 바라봅니다.
     * - 로깅과 동시에 별도로 실행됩니다.
     */
    @Bean
    public IntegrationFlow statisticsFlow() {
        return IntegrationFlow.from("loggingChannel")
                .filter(p -> p instanceof ProcessedText)
                .handle(message -> {
                    ProcessedText pt = (ProcessedText) message.getPayload();
                    // 실제로는 DB에 저장하거나 MetricRegistry를 호출하겠죠?
                    System.out.println("[통계 수집] 텍스트 길이 집계: " + pt.original().length());
                })
                .get();
    }
}