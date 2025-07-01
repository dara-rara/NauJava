package ru.glebova.NauJava.adapter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.glebova.NauJava.adapter.controller.dto.GradeSaveRequest;
import ru.glebova.NauJava.adapter.repository.PeriodLessonRepository;
import ru.glebova.NauJava.domain.Subject;
import ru.glebova.NauJava.domain.Teacher;
import ru.glebova.NauJava.service.GradeService;
import ru.glebova.NauJava.service.SubjectService;
import ru.glebova.NauJava.service.TeacherService;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/teacher")
@RequiredArgsConstructor
public class TeacherApiController {
    private final PeriodLessonRepository periodLessonRepository;
    private final TeacherService teacherService;
    private final GradeService gradeService;
    private final SubjectService subjectService;

    @GetMapping("/subjects-for-class")
    public ResponseEntity<List<SubjectDto>> getSubjectsForClass(
            @RequestParam Long teacherId,
            @RequestParam Long classId) {

        List<Subject> subjects = subjectService.getSubjectsForClass(teacherId, classId);

        List<SubjectDto> dtos = subjects.stream()
                .map(s -> new SubjectDto(s.getId(), s.getName()))
                .collect(Collectors.toList());

        return ResponseEntity.ok(dtos);
    }

    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    @PostMapping("/grades/save")
    public ResponseEntity<?> saveGrades(
            @RequestBody GradeSaveRequest request,
            Principal principal) {

        if (request.getGrades() == null || request.getClassId() == null) {
            return ResponseEntity.badRequest().body("Необходимые данные отсутствуют");
        }

        try {
            Teacher teacher = teacherService.findByTeacher(principal.getName());
            request.setTeacherId(teacher.getId());
            gradeService.saveGradesBatch(request);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Ошибка при сохранении: " + e.getMessage());
        }
    }


    public record SubjectDto(Long id, String name) {}
}