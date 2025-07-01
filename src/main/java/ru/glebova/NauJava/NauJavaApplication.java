package ru.glebova.NauJava;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.PrintStream;
import java.io.UnsupportedEncodingException;

import static java.lang.System.out;

@SpringBootApplication
public class NauJavaApplication {

    public static void main(String[] args) {
        try {
            System.setOut(new PrintStream(out, true, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        Dotenv dotenv = Dotenv.configure().load();
        dotenv.entries().forEach(entry -> {
            System.setProperty(entry.getKey(), entry.getValue());
        });
        SpringApplication.run(NauJavaApplication.class, args);
    }
}
