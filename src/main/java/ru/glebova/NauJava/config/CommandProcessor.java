//package ru.glebova.NauJava.config;
//
//import org.springframework.stereotype.Component;
//import ru.glebova.NauJava.domain.Pupil;
//import ru.glebova.NauJava.service.PupilService;
//
///**
// * Класс CommandProcessor отвечает за обработку команд, введённых пользователем,
// * и взаимодействие с сервисом PupilService для выполнения операций над учениками.
// * Поддерживаемые команды:
// * <ul>
// *     <li>create <id> <имя> <фамилия> - создание нового ученика</li>
// *     <li>find <id> - поиск ученика по ID</li>
// *     <li>update <id> <новое_имя> <новая_фамилия> - обновление данных ученика</li>
// *     <li>delete <id> - удаление ученика по ID</li>
// * </ul>
// *
// * @see PupilService
// */
//@Component
//public class CommandProcessor {
//
//    private final PupilService pupilService;
//
//    /**
//     * Конструктор класса CommandProcessor.
//     *
//     * @param pupilService сервис для работы с данными учеников
//     */
//    public CommandProcessor(PupilService pupilService) {
//        this.pupilService = pupilService;
//    }
//
//    /**
//     * Обрабатывает введённую пользователем команду.
//     * В зависимости от команды вызывает соответствующий метод сервиса PupilService.
//     *
//     * @param input введённая пользователем команда
//     * @throws IllegalArgumentException если команда содержит неверное кол-во аргументов
//     */
//    public void processCommand(String input) {
//        String[] cmd = input.split(" ");
//        try {
//            switch (cmd[0]) {
//                case "create" -> {
//                    if (cmd.length != 4) {
//                        System.out.println("Ошибка: неверное кол-во аргументов. Используйте: create <id> <имя> <фамилия>");
//                        return;
//                    }
//                    pupilService.createPupil(Long.valueOf(cmd[1]), cmd[2], cmd[3]);
//                    System.out.println("Ученик успешно добавлен.");
//                }
//                case "find" -> {
//                    if (cmd.length != 2) {
//                        System.out.println("Ошибка: неверное кол-во аргументов. Используйте: find <id>");
//                        return;
//                    }
//                    Pupil pupil = pupilService.findById(Long.valueOf(cmd[1]));
//                    System.out.println("Найден ученик: " + pupil.getFirstname() + " " + pupil.getLastname());
//                }
//                case "update" -> {
//                    if (cmd.length != 4) {
//                        System.out.println("Ошибка: неверное кол-во аргументов. Используйте: update <id> <новое_имя> <новая_фамилия>");
//                        return;
//                    }
//                    pupilService.updateProfile(Long.valueOf(cmd[1]), cmd[2], cmd[3]);
//                    System.out.println("Данные ученика успешно обновлены.");
//                }
//                case "delete" -> {
//                    if (cmd.length != 2) {
//                        System.out.println("Ошибка: неверное кол-во аргументов. Используйте: delete <id>");
//                        return;
//                    }
//                    pupilService.deleteById(Long.valueOf(cmd[1]));
//                    System.out.println("Ученик успешно удален.");
//                }
//                default -> System.out.println("Неизвестная команда. Доступные команды: create, find, update, delete.");
//            }
//        } catch (IllegalArgumentException e) {
//            System.err.println("Ошибка: " + e.getMessage());
//        }
//    }
//}
