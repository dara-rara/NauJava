package ru.glebova.NauJava.adapter.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.glebova.NauJava.domain.Homework;
import ru.glebova.NauJava.domain.PeriodLesson;
import ru.glebova.NauJava.service.HomeworkService;
import ru.glebova.NauJava.service.PeriodLessonService;

import java.time.DayOfWeek;

@Controller
@RequestMapping("/teacher")
@PreAuthorize("hasAuthority('ROLE_TEACHER')")
@RequiredArgsConstructor
public class HomeworkController {
    private final PeriodLessonService periodLessonService;
    private final HomeworkService homeworkService;

    @PostMapping("/homework/add")
    public String addHomework(
            @RequestParam Long lessonId,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {
        PeriodLesson lesson = periodLessonService.findByLessonId(lessonId);
        homeworkService.saveHomework(lesson, description);
        redirectAttributes.addAttribute("weekStart", lesson.getDate().with(DayOfWeek.MONDAY));
        return "redirect:/teacher/schedule";
    }

    @PostMapping("/homework/edit")
    public String editHomework(
            @RequestParam Long homeworkId,
            @RequestParam String description,
            RedirectAttributes redirectAttributes) {

        Homework homework = homeworkService.editHomework(homeworkId, description);

        redirectAttributes.addAttribute("weekStart",
                homework.getPeriodLesson().getDate().with(DayOfWeek.MONDAY));
        return "redirect:/teacher/schedule";
    }
}
