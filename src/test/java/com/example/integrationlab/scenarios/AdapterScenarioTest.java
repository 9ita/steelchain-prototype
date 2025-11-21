package com.example.integrationlab.scenarios;

import org.awaitility.Awaitility;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AdapterScenarioTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void processesFileIntoDatabase() throws IOException {
        File inputDir = new File("input/files");
        inputDir.mkdirs();
        File file = new File(inputDir, "sample.txt");
        Files.writeString(file.toPath(), "hello integration");

        Awaitility.await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            Integer count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM AUDIT_LOG", Integer.class);
            assertThat(count).isNotNull();
            assertThat(count).isGreaterThan(0);
        });
    }
}
