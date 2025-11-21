package com.example.integrationlab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.ExecutorChannel;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.channel.QueueChannel;

import java.util.concurrent.Executors;

@Configuration
public class ChannelConfiguration {

    @Bean
    public PublishSubscribeChannel loggingChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    public QueueChannel discardChannel() {
        return new QueueChannel();
    }

    @Bean
    public QueueChannel basicInputChannel() {
        return new QueueChannel();
    }

    @Bean
    public QueueChannel routingInputChannel() {
        return new QueueChannel();
    }

    @Bean
    public QueueChannel aggregationInputChannel() {
        return new QueueChannel();
    }

    @Bean
    public QueueChannel electronicsChannel() {
        return new QueueChannel();
    }

    @Bean
    public QueueChannel booksChannel() {
        return new QueueChannel();
    }

    @Bean
    public PublishSubscribeChannel vipNotificationChannel() {
        return new PublishSubscribeChannel();
    }

    @Bean
    public ExecutorChannel executorChannel() {
        return new ExecutorChannel(Executors.newCachedThreadPool());
    }

    @Bean
    public QueueChannel aggregatedResultsChannel() {
        return new QueueChannel();
    }

    @Bean
    public QueueChannel resiliencyInputChannel() {
        return new QueueChannel();
    }

    @Bean
    public QueueChannel controlBusInputChannel() {
        return new QueueChannel();
    }
}
