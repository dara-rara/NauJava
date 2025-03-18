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

    @Autowired
    private CommandProcessor commandProcessor;

    @Value("${app.name}")
    private String appName;

    @Value("${app.version}")
    private String appVersion;

    @PostConstruct
    public void printAppInfo() {
        System.out.println("Имя приложения: " + appName);
        System.out.println("Версия приложения: " + appVersion);
    }

    @Bean
    public CommandLineRunner commandScanner() {
        return args -> {
            try (Scanner scanner = new Scanner(System.in)) {
                System.out.println("Доступные команды:");
                System.out.println("  create <id> <имя> <фамилия> - создать нового ученика");
                System.out.println("  find <id> - найти ученика по ID");
                System.out.println("  update <id> <новое_имя> <новая_фамилия> - обновить данные ученика");
                System.out.println("  delete <id> - удалить ученика по ID");
                System.out.println("  exit - выйти из программы");
                System.out.println("Введите команду:");
                while (true) {
                    String input = scanner.nextLine();

                    if ("exit".equalsIgnoreCase(input.trim())) {
                        System.out.println("Выход из программы...");
                        break;
                    }

                    commandProcessor.processCommand(input);
                }
            }
        };
    }

    @Bean
    @Scope(value = BeanDefinition.SCOPE_SINGLETON)
    public Map<Long, Pupil> pupilContainer() { return new HashMap(); }
}
