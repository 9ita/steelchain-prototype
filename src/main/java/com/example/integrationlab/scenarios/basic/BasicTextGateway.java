package com.example.integrationlab.scenarios.basic;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface BasicTextGateway {
    @Gateway(requestChannel = "basicInputChannel")
    String processText(String input);
}
