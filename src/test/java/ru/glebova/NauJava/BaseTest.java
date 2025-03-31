package ru.glebova.NauJava;

import io.github.cdimascio.dotenv.Dotenv;
import org.junit.jupiter.api.BeforeAll;

public abstract class BaseTest {
    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
    }
}
