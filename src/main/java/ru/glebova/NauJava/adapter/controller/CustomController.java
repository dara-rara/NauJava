package ru.glebova.NauJava.adapter.controller;

import org.springframework.web.bind.annotation.*;
import ru.glebova.NauJava.adapter.controller.dto.GradeDTO;
import ru.glebova.NauJava.adapter.controller.dto.ScheduleDTO;
import ru.glebova.NauJava.domain.Grade;
import ru.glebova.NauJava.domain.Schedule;
import ru.glebova.NauJava.service.GradeService;
import ru.glebova.NauJava.service.ScheduleService;

import java.util.List;

@RestController
@RequestMapping("/custom")
public class CustomController {
    private final GradeService gradeService;
    private final ScheduleService scheduleService;

    public CustomController(GradeService gradeService, ScheduleService scheduleService) {
        this.gradeService = gradeService;
        this.scheduleService = scheduleService;
    }

    @GetMapping("/grades")
    public List<GradeDTO> getGradesByPupilAndSubject(@RequestParam("pupilId") Long pupilId,
                                                     @RequestParam("subjectId") Long subjectId) {
        return gradeService.getGradesPupilAndSubject(pupilId, subjectId);
    }

    @GetMapping("/schedule")
    public List<ScheduleDTO> getScheduleByClassAndSubject(@RequestParam("className") String className,
                                                          @RequestParam("subjectName") String subjectName) {
        return scheduleService.getScheduleClassAndSubject(className, subjectName);
    }

}
