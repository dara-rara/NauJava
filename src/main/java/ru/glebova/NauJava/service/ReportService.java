package ru.glebova.NauJava.service;

import jakarta.persistence.EntityNotFoundException;
import ru.glebova.NauJava.domain.Report;
import ru.glebova.NauJava.domain.Status;

import java.util.concurrent.CompletableFuture;

/**
 * Сервис для работы с отчетами.
 * Предоставляет методы для создания, получения и асинхронного формирования отчетов.
 */
public interface ReportService {

    /**
     * Получает содержимое отчета по его идентификатору.
     *
     * @param reportId идентификатор отчета
     * @return объект отчета
     * @throws EntityNotFoundException если отчет с указанным ID не найден
     */
    Report getReport(Long reportId) throws EntityNotFoundException;

    /**
     * Создает новый отчет в системе.
     * Устанавливает статус отчета "CREATED" по умолчанию.
     *
     * @return идентификатор созданного отчета
     */
    Long createReport();

    /**
     * Асинхронно запускает процесс формирования отчета.
     * Выполняет подсчет пользователей и получение списка объектов в отдельных потоках.
     * По завершении обновляет статус отчета ("COMPLETED" или "ERROR").
     *
     * @param reportId идентификатор отчета для формирования
     * @return CompletableFuture без результата, завершающийся при окончании обработки
     * @throws EntityNotFoundException если отчет с указанным ID не найден
     */
    CompletableFuture<Void> generateReportAsync(Long reportId) throws EntityNotFoundException;

    /**
     * Обновляет статус отчета.
     *
     * @param reportId идентификатор отчета
     * @param status новый статус отчета
     * @throws EntityNotFoundException если отчет с указанным ID не найден
     */
    void updateReportStatus(Long reportId, Status status) throws EntityNotFoundException;

    /**
     * Поиск последнего отчёта
     */
    Report getLatestReport();
}
