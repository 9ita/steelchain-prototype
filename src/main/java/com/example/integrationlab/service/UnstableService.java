package com.example.integrationlab.service;

import org.springframework.stereotype.Service;

import java.util.Random;

@Service
public class UnstableService {
    private final Random random = new Random();

    public String call(String payload) {
        if (random.nextBoolean()) {
            throw new IllegalStateException("Simulated intermittent failure");
        }
        return "OK:" + payload;
    }
}
