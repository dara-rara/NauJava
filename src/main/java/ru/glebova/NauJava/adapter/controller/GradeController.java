package ru.glebova.NauJava.adapter.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.glebova.NauJava.adapter.controller.dto.GradeSetupDTO;
import ru.glebova.NauJava.adapter.controller.dto.GradeSetupPupilDTO;
import ru.glebova.NauJava.adapter.controller.dto.GradeTableDTO;
import ru.glebova.NauJava.adapter.controller.dto.PupilGradesOverviewDTO;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.domain.Teacher;
import ru.glebova.NauJava.service.GradeService;
import ru.glebova.NauJava.service.PupilService;
import ru.glebova.NauJava.service.TeacherService;
import ru.glebova.NauJava.service.TermPeriodService;

import java.security.Principal;


@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GradeController {

    private final GradeService gradeService;
    private final TeacherService teacherService;
    private final TermPeriodService termPeriodService;
    private final PupilService pupilService;

    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    @GetMapping("/teacher/grades/setup")
    public String showSetupFormTeacher(Principal principal, Model model) {
        Teacher teacher = teacherService.findByTeacher(principal.getName());
        model.addAttribute("teacher", teacher);
        model.addAttribute("teacherClasses", teacher.getClasses());
        model.addAttribute("termPeriods", termPeriodService.getAllPeriods());
        model.addAttribute("request", new GradeSetupDTO());
        return "teacher/grade-setup";
    }

    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    @PostMapping("/teacher/grades/setup")
    public String processSetupFormTeacher(
            @ModelAttribute GradeSetupDTO request,
            Principal principal,
            Model model) {
        request.setTeacherId(teacherService.findByTeacher(principal.getName()).getId());
        GradeTableDTO gradeTable = gradeService.prepareGradeTableForTeacher(request);

        model.addAttribute("gradeTable", gradeTable);
        return "teacher/grade-table";
    }

    @PreAuthorize("hasAuthority('ROLE_PUPIL')")
    @GetMapping("/pupil/grades/setup")
    public String showSetupFormPupil(Principal principal, Model model) {
        Pupil pupil = pupilService.findByPupil(principal.getName());
        model.addAttribute("pupil", pupil);
        model.addAttribute("termPeriods", termPeriodService.getAllPeriods());
        model.addAttribute("request", new GradeSetupPupilDTO());
        return "pupil/grade-setup";
    }

    @PreAuthorize("hasAuthority('ROLE_PUPIL')")
    @PostMapping("/pupil/grades/setup")
    public String processSetupFormPupil(
            @ModelAttribute GradeSetupPupilDTO request,
            Principal principal,
            Model model) {
        request.setPupil(pupilService.findByPupil(principal.getName()));
        PupilGradesOverviewDTO gradeTable = gradeService.getPupilGradesOverview(request);
        model.addAttribute("gradeTable", gradeTable);
        return "pupil/grade-table";
    }

}
