package ru.glebova.NauJava;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
@TestConfiguration
class ConfigurationTest {

    @Bean
    public TestValue testValue() {
        return new TestValue();
    }
}
