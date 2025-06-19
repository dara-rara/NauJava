package ru.glebova.NauJava;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.adapter.repository.*;
import ru.glebova.NauJava.service.impl.ReportServiceImpl;

import java.util.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceImplTest extends BaseTest{

    @Mock
    private ReportRepository reportRepository;

    @Mock
    private UsersRepository usersRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @InjectMocks
    private ReportServiceImpl reportService;

    // Тест на успешное получение отчета по ID
    @Test
    void getReportWhenExistsTest() {
        Long reportId = 1L;
        Report expectedReport = new Report();
        expectedReport.setId(reportId);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(expectedReport));

        Report actualReport = reportService.getReport(reportId);

        assertEquals(expectedReport, actualReport);
        verify(reportRepository).findById(reportId);
    }

    // Тест на обработку отсутствия отчета
    @Test
    void getReportWhenReportNotFoundTest() {
        Long reportId = 99L;
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> reportService.getReport(reportId)
        );

        assertEquals("Отчёта с id - 99 не существует", exception.getMessage());
        verify(reportRepository).findById(reportId);
    }

    // Тест на получение последнего отчета (когда он существует)
    @Test
    void getLatestReportTest() {
        Report expectedReport = new Report();
        expectedReport.setId(1L);

        when(reportRepository.findTopByOrderByCreatedDateDesc())
                .thenReturn(Optional.of(expectedReport));

        Report actualReport = reportService.getLatestReport();

        assertEquals(expectedReport, actualReport);
        verify(reportRepository).findTopByOrderByCreatedDateDesc();
    }

    // Тест на получение последнего отчета (когда нет отчетов)
    @Test
    void getLatestReportWhenNoReportsTest() {
        when(reportRepository.findTopByOrderByCreatedDateDesc())
                .thenReturn(Optional.empty());

        Report actualReport = reportService.getLatestReport();

        assertNull(actualReport);
        verify(reportRepository).findTopByOrderByCreatedDateDesc();
    }

    // Тест на успешное создание отчета
    @Test
    void createReportTest() {
        Report newReport = new Report();
        newReport.setStatus(Status.CREATED);
        newReport.setId(1L);

        when(reportRepository.save(any(Report.class))).thenReturn(newReport);

        Long reportId = reportService.createReport();

        assertEquals(1L, reportId);
        verify(reportRepository).save(any(Report.class));
    }

    // Тест на асинхронную генерацию отчета (успешный сценарий)
    @Test
    void generateReportAsyncSuccessfullyTest() throws Exception {
        Long reportId = 1L;
        Report report = new Report();
        report.setId(reportId);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(usersRepository.count()).thenReturn(10L);

        Teacher teacher = new Teacher();
        Users user = new Users();
        user.setFirstName("Иван");
        user.setLastName("Иванов");
        teacher.setUser(user);

        when(teacherRepository.findAll()).thenReturn(Arrays.asList(teacher));

        CompletableFuture<Void> future = reportService.generateReportAsync(reportId);
        future.get(); // Ждем завершения

        assertEquals(Status.COMPLETED, report.getStatus());
        assertTrue(report.getDescription().contains("Количество пользователей: 10"));
        assertTrue(report.getDescription().contains("Иванов Иван"));
        verify(reportRepository, atLeastOnce()).save(report);
    }

    // Тест на обработку ошибки при асинхронной генерации
    @Test
    void generateReportAsyncErrorTest() throws Exception {
        Long reportId = 1L;
        Report report = new Report();
        report.setId(reportId);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));
        when(usersRepository.count()).thenThrow(new RuntimeException("Error"));

        CompletableFuture<Void> future = reportService.generateReportAsync(reportId);

        ExecutionException exception = assertThrows(
                ExecutionException.class,
                future::get
        );

        assertEquals("Ошибка генерации отчёта", exception.getCause().getMessage());
        assertEquals(Status.ERROR, report.getStatus());
        verify(reportRepository, times(4)).save(any(Report.class));
    }

    // Тест на обновление статуса отчета
    @Test
    void updateReportStatusUpdateStatusTest() {
        Long reportId = 1L;
        Report report = new Report();
        report.setId(reportId);

        when(reportRepository.findById(reportId)).thenReturn(Optional.of(report));

        reportService.updateReportStatus(reportId, Status.ERROR);

        assertEquals(Status.ERROR, report.getStatus());
        verify(reportRepository).save(report);
    }

    // Тест на обработку ошибки при обновлении несуществующего отчета
    @Test
    void updateReportStatusWhenReportNotFoundTest() {
        Long reportId = 99L;
        when(reportRepository.findById(reportId)).thenReturn(Optional.empty());

        EntityNotFoundException exception = assertThrows(
                EntityNotFoundException.class,
                () -> reportService.updateReportStatus(reportId, Status.ERROR)
        );

        assertEquals("Отчёт не найден", exception.getMessage());
        verify(reportRepository, never()).save(any());
    }
}