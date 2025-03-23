package ru.glebova.NauJava.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Scanner;


@Component
public class CommandLineWeb implements CommandLineRunner {

    private final CommandProcessor commandProcessor;

    public CommandLineWeb(CommandProcessor commandProcessor) {
        this.commandProcessor = commandProcessor;
    }

    @Override
    public void run(String... args) {
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
    }
}