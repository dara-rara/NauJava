package ru.glebova.NauJava;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpStatus;
import ru.glebova.NauJava.adapter.controller.dto.GradeDTO;
import ru.glebova.NauJava.adapter.controller.dto.ScheduleDTO;
import ru.glebova.NauJava.exception.EmptyNameException;
import ru.glebova.NauJava.exception.PupilNotFoundException;
import ru.glebova.NauJava.service.GradeService;
import ru.glebova.NauJava.service.ScheduleService;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(MockitoExtension.class)
class CustomControllerTest extends BaseTest{

    @LocalServerPort
    private int port;

    @MockBean
    private GradeService gradeService;

    @MockBean
    private ScheduleService scheduleService;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    // Тесты для /custom/grades
    @Test
    void getGradesByPupilAndSubjectShouldReturn200Test() {
        GradeDTO mockGrade = new GradeDTO("Иван Петров", "Математика", 5, "2025-04-25");
        when(gradeService.getGradesPupilAndSubject(1L, 1L))
                .thenReturn(List.of(mockGrade));

        given()
                .queryParam("pupilId", 1)
                .queryParam("subjectId", 1)
                .accept(ContentType.JSON)
                .when()
                .get("/custom/grades")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("list", hasSize(1))
                .body("list[0].name", equalTo("Иван Петров"))
                .body("list[0].subject", equalTo("Математика"))
                .body("list[0].grade", equalTo(5))
                .body("list[0].date", equalTo("2025-04-25"));
    }

    @Test
    void getGradesWhenPupilNotFoundTest() {
        when(gradeService.getGradesPupilAndSubject(999L, 1L))
                .thenThrow(new PupilNotFoundException(999L));

        given()
                .queryParam("pupilId", 999)
                .queryParam("subjectId", 1)
                .accept(ContentType.JSON)
                .when()
                .get("/custom/grades")
                .then()
                .statusCode(HttpStatus.NOT_FOUND.value())
                .contentType(ContentType.JSON)
                .body("error", containsString("Pupil with id 999 not found"));
    }

    @Test
    void getGradesWhenNoGradesTest() {
        when(gradeService.getGradesPupilAndSubject(2L, 2L))
                .thenReturn(List.of());

        given()
                .queryParam("pupilId", 2L)
                .queryParam("subjectId", 2L)
                .accept(ContentType.JSON)
                .when()
                .get("/custom/grades")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("list", empty());
    }

    // Тесты для /custom/schedule
    @Test
    void getScheduleShouldReturn200Test() {
        ScheduleDTO mockSchedule = new ScheduleDTO("ПН", "09:00");

        when(scheduleService.getScheduleClassAndSubject("10A", "Математика"))
                .thenReturn(List.of(mockSchedule));

        given()
                .queryParam("className", "10A")
                .queryParam("subjectName", "Математика")
                .accept(ContentType.JSON)
                .when()
                .get("/custom/schedule")
                .then()
                .statusCode(HttpStatus.OK.value())
                .contentType(ContentType.JSON)
                .body("$", hasSize(1))
                .body("[0].dayOfWeek", equalTo("ПН"))
                .body("[0].time", equalTo("09:00"));
    }

    @Test
    void getScheduleWhenInvalidParamsTest() {
        when(scheduleService.getScheduleClassAndSubject("", ""))
                .thenThrow(new EmptyNameException());

        given()
                .queryParam("className", "")
                .queryParam("subjectName", "")
                .accept(ContentType.JSON)
                .when()
                .get("/custom/schedule")
                .then()
                .statusCode(HttpStatus.BAD_REQUEST.value())
                .contentType(ContentType.JSON)
                .body("error", containsString("Название предмета не может быть пустым"));
    }
}