package com.example.integrationlab.scenarios.routing;

import com.example.integrationlab.domain.Order;
import com.example.integrationlab.domain.OrderType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.router.HeaderValueRouter;

@Configuration
public class RoutingScenarioConfiguration {

    @Bean
    public IntegrationFlow routingFlow() {
        return IntegrationFlow.from("routingInputChannel")
                .filter((Order order) -> order.getAmount().doubleValue() >= 1000, f -> f.discardChannel("discardChannel"))
                .publishSubscribeChannel(spec -> spec
                        .subscribe(flow -> flow
                                .enrichHeaders(h -> h.headerFunction("orderType", m -> ((Order) m.getPayload()).getType()))
                                .route(orderTypeRouter()))
                        .subscribe(flow -> flow.filter(Order::isVip).channel("vipNotificationChannel")))
                .get();
    }

    @Bean
    public HeaderValueRouter orderTypeRouter() {
        HeaderValueRouter router = new HeaderValueRouter("orderType");
        router.setChannelMapping(OrderType.ELECTRONICS.name(), "electronicsChannel");
        router.setChannelMapping(OrderType.BOOKS.name(), "booksChannel");
        return router;
    }
}
