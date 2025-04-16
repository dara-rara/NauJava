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

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

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
        try {
            long startTimeGeneral= System.currentTimeMillis();

            Report report = getReport(reportId);

            long startTimeOneProcessing = System.currentTimeMillis();

            CompletableFuture<Long> userCountFuture = CompletableFuture.supplyAsync(() -> {
                Thread.currentThread().setName("UserCountThread");
                return usersRepository.count();
            });

            long timeOneProcessing = (System.currentTimeMillis() - startTimeOneProcessing);
            long startTimeTwoProcessing = System.currentTimeMillis();

            CompletableFuture<Iterable<Teacher>> teacherListFuture = CompletableFuture.supplyAsync(() -> {
                Thread.currentThread().setName("TeacherListThread");
                return teacherRepository.findAll();
            });

            long timeTwoProcessing = (System.currentTimeMillis() - startTimeTwoProcessing);

            CompletableFuture.allOf(userCountFuture, teacherListFuture).join();

            long userCount = userCountFuture.get();
            List<Teacher> teachers = (List<Teacher>) teacherListFuture.get();

            report.setDescription("Kоличество пользователей: " + userCount + "\n" +
                    "Cписок учителей: " + teachers.stream()
                    .map(Teacher::getUser)
                    .map(user -> user.getLastName() + " " + user.getFirstName())
                    .collect(Collectors.joining(", ")));
            report.setStatus(Status.COMPLETED);
            report.setProcessingTimeMillisOne(timeOneProcessing);
            report.setProcessingTimeMillisTwo(timeTwoProcessing);
            report.setProcessingTimeMillisGeneral(System.currentTimeMillis() - startTimeGeneral);
            reportRepository.save(report);

        } catch (InterruptedException | ExecutionException e) {
            updateReportStatus(reportId, Status.ERROR);
            Thread.currentThread().interrupt();
            throw new RuntimeException("Прерывание формирования отчета", e);
        } catch (Exception e) {
            updateReportStatus(reportId, Status.ERROR);
            throw new RuntimeException("Неожиданная ошибка при формировании отчета", e);
        }
    }

    @Override
    public void updateReportStatus(Long reportId, Status status) {
        Report report = reportRepository.findById(reportId)
                .orElseThrow(() -> new EntityNotFoundException("Отчёт не найден"));
        report.setStatus(status);
        reportRepository.save(report);
    }
}
