package ru.glebova.NauJava.adapter.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.glebova.NauJava.service.GradeService;


@Controller
@RequestMapping("/grade/view")
public class GradeController {

    private final GradeService gradeService;

    public GradeController(GradeService gradeService) {
        this.gradeService = gradeService;
    }

    @GetMapping
    public String getGradesByPupil(@RequestParam("pupilId") Long pupilId, Model model) {
        model.addAttribute("grades", gradeService.getGradesPupil(pupilId));
        return "grade-list";
    }

}
