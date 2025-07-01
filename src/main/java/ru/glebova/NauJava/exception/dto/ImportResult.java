package ru.glebova.NauJava.exception.dto;

import lombok.Getter;
import lombok.Setter;
import ru.glebova.NauJava.exception.ImportException;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
@Setter
public class ImportResult {
    private int successCount = 0;
    private List<String> errors = new ArrayList<>();
    private List<Long> failedIds = new ArrayList<>();

    /**
     * Добавляет информацию об успешно обработанной записи
     */
    public void incrementSuccess() {
        successCount++;
    }

    /**
     * Добавляет ошибку с описанием
     * @param error Описание ошибки
     */
    public void addError(String error) {
        Objects.requireNonNull(error, "Error message cannot be null");
        errors.add(error);
    }

    /**
     * Добавляет ошибку с ID проблемной записи
     * @param error Описание ошибки
     * @param id ID проблемной записи
     */
    public void addError(String error, Long id) {
        Objects.requireNonNull(error, "Error message cannot be null");
        Objects.requireNonNull(id, "ID cannot be null");

        errors.add(error + " (ID: " + id + ")");
        failedIds.add(id);
    }

    /**
     * Добавляет ошибку из ImportException
     * @param ex Исключение при импорте
     */
    public void addError(ImportException ex) {
        Objects.requireNonNull(ex, "ImportException cannot be null");
        addError(ex.getMessage(), ex.getId());
    }

    /**
     * Проверяет наличие ошибок
     * @return true если есть ошибки
     */
    public boolean hasErrors() {
        return !errors.isEmpty();
    }

    /**
     * Возвращает общее количество обработанных записей
     * @return Сумма успешных и неудачных операций
     */
    public int getTotalProcessed() {
        return successCount + failedIds.size();
    }

    /**
     * Возвращает сводную статистику импорта
     */
    public String getSummary() {
        return String.format("Обработано: %d, Успешно: %d, Ошибки: %d",
                getTotalProcessed(), successCount, errors.size());
    }
}