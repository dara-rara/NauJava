package ru.glebova.NauJava.service.impl;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.glebova.NauJava.adapter.repository.PupilRepository;
import ru.glebova.NauJava.adapter.repository.ReportRepository;
import ru.glebova.NauJava.adapter.repository.TeacherRepository;
import ru.glebova.NauJava.adapter.repository.UsersRepository;
import ru.glebova.NauJava.domain.Report;
import ru.glebova.NauJava.domain.Status;
import ru.glebova.NauJava.domain.Teacher;
import ru.glebova.NauJava.domain.Users;
import ru.glebova.NauJava.service.ReportService;
import ru.glebova.NauJava.service.dto.TaskResult;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ReportServiceImpl implements ReportService {

    private final ReportRepository reportRepository;
    private final UsersRepository usersRepository;
    private final PupilRepository pupilRepository;
    private final TeacherRepository teacherRepository;

    public ReportServiceImpl(ReportRepository reportRepository, UsersRepository usersRepository,
                             PupilRepository pupilRepository, TeacherRepository teacherRepository) {
        this.reportRepository = reportRepository;
        this.usersRepository = usersRepository;
        this.pupilRepository = pupilRepository;
        this.teacherRepository = teacherRepository;
    }

    @Override
    public Report getReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Отчёта с id - " + reportId + " не существует"));
    }

    @Override
    public Report getLatestReport() {
        Optional<Report> report = reportRepository.findTopByOrderByCreatedDateDesc();
        return report.isEmpty() ? null : report.get();
    }

    @Transactional
    @Override
    public Long createReport() {
        Report report = new Report();
        report.setStatus(Status.CREATED);
        report.setDateTime(LocalDateTime.now());
        report = reportRepository.save(report);
        return report.getId();
    }

    @Override
    public CompletableFuture<Void> generateReportAsync(Long reportId) {
        return CompletableFuture.runAsync(() -> {
            try {
                generateReportPupilAndTeacher(reportId);
            } catch (Exception e) {
                updateReportStatus(reportId, Status.ERROR);
                throw new RuntimeException("Ошибка генерации отчёта", e);
            }
        });
    }

    private void generateReportPupilAndTeacher(Long reportId) {
        Report report = getReport(reportId);
        long startTimeGeneral = System.currentTimeMillis();

        try {
            CompletableFuture<TaskResult<Long>> userCountFuture = CompletableFuture.supplyAsync(() -> {
                long startTime = System.currentTimeMillis();
                Long count = usersRepository.count();
                return new TaskResult<>(count, System.currentTimeMillis() - startTime);
            });

            CompletableFuture<TaskResult<Iterable<Teacher>>> teacherListFuture = CompletableFuture.supplyAsync(() -> {
                long startTime = System.currentTimeMillis();
                Iterable<Teacher> teachers = teacherRepository.findAll();
                return new TaskResult<>(teachers, System.currentTimeMillis() - startTime);
            });

            CompletableFuture<Void> combinedFuture = CompletableFuture.allOf(userCountFuture, teacherListFuture)
                    .exceptionally(ex -> {
                        updateReportStatus(reportId, Status.ERROR);
                        throw new CompletionException("Ошибка при выполнении асинхронных задач", ex);
                    });
            combinedFuture.join();

            TaskResult<Long> userCountResult = userCountFuture.get();
            TaskResult<Iterable<Teacher>> teacherListResult = teacherListFuture.get();

            report.setDescription(buildReportDescription(userCountResult.getResult(), teacherListResult.getResult()));
            report.setStatus(Status.COMPLETED);
            report.setProcessingTimeMillisOne(userCountResult.getExecutionTime());
            report.setProcessingTimeMillisTwo(teacherListResult.getExecutionTime());
            report.setProcessingTimeMillisGeneral(System.currentTimeMillis() - startTimeGeneral);

        } catch (ExecutionException e) {
            throw new RuntimeException("Ошибка при формировании отчета", e.getCause());
        } catch (InterruptedException e) {
            updateReportStatus(reportId, Status.ERROR);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Прерывание формирования отчета", e);
        } catch (Exception e) {
            updateReportStatus(reportId, Status.ERROR);
            throw new RuntimeException("Неожиданная ошибка при формировании отчета", e);
        } finally {
            reportRepository.save(report);
        }
    }

    private String buildReportDescription(Long userCount, Iterable<Teacher> teachers) {
        return "Количество пользователей: " + userCount + "\n" +
                "Список учителей: " + StreamSupport.stream(teachers.spliterator(), false)
                .map(Teacher::getUser)
                .map(user -> user.getLastName() + " " + user.getFirstName())
                .collect(Collectors.joining(", "));
    }

    @Override
    public void updateReportStatus(Long reportId, Status status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Отчёт не найден"));
        report.setStatus(status);
        reportRepository.save(report);
    }
}
