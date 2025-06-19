package ru.glebova.NauJava.service.dto;

public class TaskResult<T> {
    private final T result;
    private final long executionTimeMs;

    public TaskResult(T result, long executionTimeMs) {
        this.result = result;
        this.executionTimeMs = executionTimeMs;
    }

    public T getResult() {
        return result;
    }

    public long getExecutionTime() {
        return executionTimeMs;
    }
}
