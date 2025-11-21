package com.example.integrationlab.domain;

import java.time.Instant;

public record ProcessedText(String original, String uppercased, Instant timestamp) {
}
