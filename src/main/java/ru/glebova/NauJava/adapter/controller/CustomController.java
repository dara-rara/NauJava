package ru.glebova.NauJava.adapter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.glebova.NauJava.adapter.controller.dto.ListDTO;
import ru.glebova.NauJava.service.GradeService;
import ru.glebova.NauJava.service.ScheduleService;

@RestController
@RequestMapping(value = "/custom")
public class CustomController {
    private final GradeService gradeService;
    private final ScheduleService scheduleService;

    public CustomController(GradeService gradeService, ScheduleService scheduleService) {
        this.gradeService = gradeService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/grades")
    public ResponseEntity<?> getGradesByPupilAndSubject(@RequestParam("pupilId") Long pupilId,
                                                        @RequestParam("subjectId") Long subjectId) {
        return ResponseEntity.ok(new ListDTO(gradeService.getGradesPupilAndSubject(pupilId, subjectId)));
    }

    @GetMapping("/schedule")
    public ResponseEntity<?> getScheduleByClassAndSubject(@RequestParam("className") String className,
                                                          @RequestParam("subjectName") String subjectName) {
        return ResponseEntity.ok(scheduleService.getScheduleClassAndSubject(className, subjectName));
    }

}
