package ru.glebova.NauJava.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.glebova.NauJava.domain.Pupil;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

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

    @Bean("pupilContainer")
    public Map<Long, Pupil> pupilContainer() {
        return new HashMap();
    }
}
