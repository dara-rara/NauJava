package ru.glebova.NauJava.service;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;
import ru.glebova.NauJava.adapter.controller.dto.RegisterDTO;
import ru.glebova.NauJava.adapter.controller.dto.UserDTO;
import ru.glebova.NauJava.domain.Role;
import ru.glebova.NauJava.domain.Users;
import ru.glebova.NauJava.exception.ImportException;
import ru.glebova.NauJava.exception.dto.ImportResult;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

/**
 * Сервис для работы с пользователями системы.
 * Предоставляет методы для регистрации, управления учетными записями,
 * импорта/экспорта пользователей и работы с их данными.
 */
public interface UsersService {

    /**
     * Регистрирует нового пользователя в системе.
     *
     * @param registerDTO DTO с данными для регистрации
     * @throws EntityExistsException если пользователь с таким логином уже существует
     */
    void registerNewUser(RegisterDTO registerDTO);

    /**
     * Проверяет существование пользователя по логину.
     *
     * @param username логин пользователя
     * @return true если пользователь существует, false в противном случае
     */
    boolean userExist(String username);

    /**
     * Получает список всех пользователей в формате DTO.
     *
     * @return список UserDTO
     */
    List<UserDTO> getAllUsers();

    /**
     * Получает пользователя по ID в формате DTO.
     *
     * @param id идентификатор пользователя
     * @return UserDTO
     * @throws EntityNotFoundException если пользователь не найден
     */
    UserDTO getUserById(Long id);

    /**
     * Создает нового пользователя.
     *
     * @param userDTO DTO с данными пользователя
     * @return созданный пользователь
     * @throws EntityExistsException если пользователь с таким логином уже существует
     */
    Users createUser(UserDTO userDTO);

    /**
     * Обновляет данные пользователя.
     *
     * @param id идентификатор пользователя
     * @param userDTO DTO с обновленными данными
     * @return обновленный пользователь
     * @throws EntityNotFoundException если пользователь не найден
     * @throws EntityExistsException если новый логин уже занят
     */
    Users updateUser(Long id, UserDTO userDTO);

    /**
     * Удаляет пользователя по ID.
     *
     * @param id идентификатор пользователя
     * @throws EntityNotFoundException если пользователь не найден
     */
    void deleteUser(Long id);

    /**
     * Импортирует пользователей из файла.
     *
     * @param file файл с данными пользователей
     * @param role роль импортируемых пользователей
     * @return результат импорта
     * @throws IOException при ошибках чтения файла
     * @throws ImportException при ошибках формата данных
     */
    ImportResult importUsersFromFile(MultipartFile file, Role role) throws IOException;

    /**
     * Генерирует шаблон файла для импорта пользователей.
     *
     * @param outputStream поток для записи шаблона
     * @throws IOException при ошибках записи
     */
    void generateUserTemplate(OutputStream outputStream) throws IOException;

    /**
     * Конвертирует сущность Users в DTO.
     *
     * @param user сущность пользователя
     * @return UserDTO
     */
    UserDTO convertToDTO(Users user);

    /**
     * Получает пользователя по логину в формате DTO.
     *
     * @param username логин пользователя
     * @return UserDTO
     * @throws EntityNotFoundException если пользователь не найден
     */
    UserDTO getUserByUsername(String username);

    /**
     * Обновляет данные пользователя по оригинальному логину.
     *
     * @param originalUsername текущий логин пользователя
     * @param userDTO DTO с обновленными данными
     * @throws EntityNotFoundException если пользователь не найден
     * @throws EntityExistsException если новый логин уже занят
     */
    void updateUser(String originalUsername, UserDTO userDTO);

    /**
     * Удаляет пользователя по логину.
     *
     * @param username логин пользователя
     * @throws EntityNotFoundException если пользователь не найден
     */
    void deleteUserByUsername(String username);

    /**
     * Находит пользователей по роли.
     *
     * @param role роль для поиска
     * @return список пользователей
     */
    List<Users> findUsersByRole(Role role);

    /**
     * Загружает пользователя по ID (для Spring Security).
     *
     * @param id идентификатор пользователя
     * @return сущность пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    Users loadUserById(Long id) throws UsernameNotFoundException;

    /**
     * Загружает пользователя по логину (для Spring Security).
     *
     * @param username логин пользователя
     * @return сущность пользователя
     * @throws UsernameNotFoundException если пользователь не найден
     */
    Users loadUserByName(String username);
}