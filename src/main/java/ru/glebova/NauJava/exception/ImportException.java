package ru.glebova.NauJava.exception;


/**
 * Исключение, возникающее при ошибках импорта данных
 */
public class ImportException extends RuntimeException {
    private final Long id;
    private final Integer lineNumber;
    private final String errorCode;

    /**
     * Конструктор с ID записи
     */
    public ImportException(Long id) {
        super("Ошибка импорта для элемента с ID: " + id);
        this.id = id;
        this.lineNumber = null;
        this.errorCode = "IMPORT_ERROR";
    }

    /**
     * Конструктор с ID и сообщением
     */
    public ImportException(Long id, String message) {
        super(message);
        this.id = id;
        this.lineNumber = null;
        this.errorCode = "IMPORT_ERROR";
    }

    /**
     * Конструктор с ID, сообщением и кодом ошибки
     */
    public ImportException(Long id, String message, String errorCode) {
        super(message);
        this.id = id;
        this.lineNumber = null;
        this.errorCode = errorCode;
    }

    /**
     * Конструктор для ошибок в файле (с номером строки)
     */
    public ImportException(String message, Integer lineNumber) {
        super(message);
        this.id = null;
        this.lineNumber = lineNumber;
        this.errorCode = "FILE_IMPORT_ERROR";
    }

    /**
     * Конструктор для ошибок в файле с кодом ошибки
     */
    public ImportException(String message, Integer lineNumber, String errorCode) {
        super(message);
        this.id = null;
        this.lineNumber = lineNumber;
        this.errorCode = errorCode;
    }

    /**
     * Конструктор с причиной исключения
     */
    public ImportException(Long id, String message, Throwable cause) {
        super(message, cause);
        this.id = id;
        this.lineNumber = null;
        this.errorCode = "IMPORT_ERROR";
    }

    // Геттеры
    public Long getId() {
        return id;
    }

    public Integer getLineNumber() {
        return lineNumber;
    }

    public String getErrorCode() {
        return errorCode;
    }

    /**
     * Форматированное сообщение об ошибке
     */
    @Override
    public String getMessage() {
        if (lineNumber != null) {
            return String.format("[Строка %d] %s", lineNumber, super.getMessage());
        }
        if (id != null) {
            return String.format("[ID %d] %s", id, super.getMessage());
        }
        return super.getMessage();
    }
}