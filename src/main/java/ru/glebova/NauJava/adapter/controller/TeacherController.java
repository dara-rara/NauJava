package ru.glebova.NauJava.adapter.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.glebova.NauJava.adapter.repository.PeriodLessonRepository;
import ru.glebova.NauJava.domain.Role;
import ru.glebova.NauJava.domain.Subject;
import ru.glebova.NauJava.domain.Teacher;
import ru.glebova.NauJava.service.ClassesService;
import ru.glebova.NauJava.service.SubjectService;
import ru.glebova.NauJava.service.TeacherService;
import ru.glebova.NauJava.service.UsersService;

import java.security.Principal;
import java.util.List;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class TeacherController {

    private final TeacherService teacherService;
    private final UsersService userService;
    private final SubjectService subjectService;
    private final ClassesService classService;
    private final PeriodLessonRepository periodLessonRepository;


    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/teachers")
    public String listTeachers(Model model) {
        model.addAttribute("teachers", teacherService.findAll());
        return "admin/teacher-list";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/teachers/add")
    public String showAddForm(Model model) {
        model.addAttribute("teacher", new Teacher());
        model.addAttribute("availableUsers", userService.findUsersByRole(Role.TEACHER));
        model.addAttribute("allSubjects", subjectService.getAllSubjects());
        model.addAttribute("allClasses", classService.getAllClasses());
        return "admin/teacher-create";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/teachers/add")
    public String addTeacher(@ModelAttribute Teacher teacher,
                             @RequestParam Long userId,
                             @RequestParam(required = false) List<Long> subjectIds,
                             @RequestParam(required = false) List<Long> classIds,
                             RedirectAttributes redirectAttributes) {
        try {
            teacherService.createTeacher(teacher, userId, subjectIds, classIds);
            redirectAttributes.addFlashAttribute("successMessage", "Учитель успешно добавлен");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/teachers";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/teachers/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Teacher teacher = teacherService.findById(id);

        model.addAttribute("teacher", teacher);
        model.addAttribute("allSubjects", subjectService.getAllSubjects());
        model.addAttribute("allClasses", classService.getAllClasses());
        return "admin/teacher-edit";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/teachers/edit/{id}")
    public String updateTeacher(@PathVariable Long id,
                                @ModelAttribute Teacher teacher,
                                @RequestParam(required = false) List<Long> subjectIds,
                                @RequestParam(required = false) List<Long> classIds,
                                RedirectAttributes redirectAttributes) {
        try {
            teacherService.updateTeacher(id, teacher, subjectIds, classIds);
            redirectAttributes.addFlashAttribute("successMessage", "Данные учителя обновлены");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/teachers";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/teachers/delete/{id}")
    public String deleteTeacher(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            teacherService.deleteTeacher(id);
            redirectAttributes.addFlashAttribute("successMessage", "Учитель удален");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/teachers";
    }
}
