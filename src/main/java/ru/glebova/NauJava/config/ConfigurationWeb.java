package ru.glebova.NauJava.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigurationWeb {

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @PostConstruct
    public void printAppInfo() {
        System.out.println("Имя приложения: " + appName);
        System.out.println("Версия приложения: " + appVersion);
    }
}
