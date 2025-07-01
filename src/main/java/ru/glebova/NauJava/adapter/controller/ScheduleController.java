package ru.glebova.NauJava.adapter.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.glebova.NauJava.adapter.controller.dto.LessonDTO;
import ru.glebova.NauJava.domain.*;
import ru.glebova.NauJava.service.*;

import java.security.Principal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ScheduleController {

    private final ScheduleService scheduleService;
    private final ClassesService classesService;
    private final SubjectService subjectService;
    private final TeacherService teacherService;
    private final PeriodScheduleService periodScheduleService;
    private final PeriodLessonService periodLessonService;
    private final TermPeriodService termPeriodService;
    private final PupilService pupilService;
    private final GradeService gradeService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/schedule/class/{id}")
    public String getClassSchedule(
            @PathVariable Long id,
            @RequestParam(required = false) String day,
            Model model) {

        Week selectedDay = day != null ? Week.valueOf(day) : Week.MONDAY;
        model.addAttribute("selectedDay", selectedDay);

        Classes cls = classesService.findById(id);
        Schedule schedule = scheduleService.save(cls);

        model.addAttribute("class", cls);
        model.addAttribute("schedule", schedule);
        model.addAttribute("lessonsByDay", scheduleService.getLesson(schedule));
        model.addAttribute("weekDays", Week.values());
        model.addAttribute("teacherList", teacherService.findAll());
        model.addAttribute("subjectList", subjectService.getAllSubjects());
        model.addAttribute("periodList", termPeriodService.getAllPeriods());

        return "admin/schedule-editor";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/schedule/add-lesson")
    public String addLesson(
            @RequestParam Long scheduleId,
            @RequestParam Week weekDay,
            @RequestParam int lessonNumber,
            @RequestParam Long subjectId,
            @RequestParam Long teacherId,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime timeStart,
            @RequestParam @DateTimeFormat(pattern = "HH:mm") LocalTime timeEnd,
            RedirectAttributes redirectAttributes) {
        Schedule schedule = scheduleService.findById(scheduleId);
        try {
            scheduleService.createLesson(scheduleId, weekDay, lessonNumber, subjectId, teacherId,timeStart, timeEnd);
            redirectAttributes.addFlashAttribute("successMessage", "Урок успешно добавлен");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Произошла ошибка при добавлении урока");
        }
        return "redirect:/admin/schedule/class/" + schedule.getClasses().getId();
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/schedule/remove-lesson/{lessonId}")
    public String removeLesson(@PathVariable Long lessonId, RedirectAttributes redirectAttributes) {
        Long classId = scheduleService.removeLesson(lessonId);
        redirectAttributes.addFlashAttribute("successMessage", "Урок удалён из расписания");
        return "redirect:/admin/schedule/class/" + classId;
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/schedule/generate")
    public String generateSchedule(
            @RequestParam Long classId,
            @RequestParam Long periodId,
            @RequestParam(required = false) boolean overwrite,
            RedirectAttributes redirectAttributes) {

        try {
            periodScheduleService.generateForClass(classId, periodId, overwrite);
            redirectAttributes.addFlashAttribute("successMessage", "Расписание успешно сгенерировано");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка генерации: " + e.getMessage());
        }

        return "redirect:/admin/schedule/class/" + classId;
    }

    @GetMapping("/teacher/schedule")
    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    public String getTeacherSchedule(
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate weekStart,
            Principal principal,
            Model model) {

        Teacher teacher = teacherService.findByTeacher(principal.getName());
        LocalDate startOfWeek = (weekStart != null) ? weekStart : LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);

        List<PeriodLesson> lessons = periodLessonService.findByTeacherAndDateBetween(teacher, startOfWeek, endOfWeek);

        Map<LocalDate, List<PeriodLesson>> lessonsByDate = lessons.stream()
                .collect(Collectors.groupingBy(PeriodLesson::getDate));

        model.addAttribute("teacher", teacher);
        model.addAttribute("lessonsByDate", lessonsByDate);
        model.addAttribute("currentWeekStart", startOfWeek);
        model.addAttribute("currentWeekEnd", endOfWeek);
        model.addAttribute("nextWeekStart", startOfWeek.plusWeeks(1));
        model.addAttribute("prevWeekStart", startOfWeek.minusWeeks(1));

        return "teacher/schedule";
    }

    @GetMapping("/pupil/schedule")
    @PreAuthorize("hasAuthority('ROLE_PUPIL')")
    public String getPupilSchedule(
            @RequestParam(required = false) @DateTimeFormat(pattern = "dd.MM.yyyy") LocalDate weekStart,
            Principal principal,
            Model model) {

        Pupil pupil = pupilService.findByPupil(principal.getName());
        LocalDate startOfWeek = (weekStart != null) ? weekStart : LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate endOfWeek = startOfWeek.plusDays(6);
        Map<Long, Grade> grades = gradeService.getGradesForPupilAndWeek(pupil, startOfWeek, endOfWeek);
        Map<LocalDate, List<LessonDTO>> lessonsByDate = periodLessonService
                .findByPupilAndDateBetween(pupil, startOfWeek, endOfWeek, grades);
        model.addAttribute("pupil", pupil);
        model.addAttribute("lessonsByDate", lessonsByDate);
        model.addAttribute("currentWeekStart", startOfWeek);
        model.addAttribute("currentWeekEnd", endOfWeek);
        model.addAttribute("nextWeekStart", startOfWeek.plusWeeks(1));
        model.addAttribute("prevWeekStart", startOfWeek.minusWeeks(1));

        return "pupil/schedule";
    }
}