package ru.glebova.NauJava.adapter.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.glebova.NauJava.adapter.controller.dto.SubjectDTO;
import ru.glebova.NauJava.service.SubjectService;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class SubjectController {
    private final SubjectService subjectService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/subjects")
    public String subjectsList(Model model,
                               @RequestParam(required = false) String success,
                               @RequestParam(required = false) String error) {
        model.addAttribute("subjects", subjectService.getAllSubjects());
        if (success != null) model.addAttribute("successMessage", "Операция выполнена успешно!");
        if (error != null) model.addAttribute("errorMessage", "Произошла ошибка!");
        return "admin/subject-list";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/subjects/new")
    public String showCreateSubjectForm(Model model) {
        model.addAttribute("subjectForm", new SubjectDTO());
        return "admin/subject-create";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/subjects")
    public String createSubject(
            @ModelAttribute("subjectForm") SubjectDTO subjectDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/subject-create";
        }

        try {
            subjectService.createSubject(subjectDTO);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/admin/subjects";
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", true);
            return "redirect:/admin/subjects";
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/subjects/edit")
    public String showEditSubjectForm(
            @RequestParam("name") String name,
            Model model) {
        SubjectDTO subjectDTO = subjectService.getSubjectByName(name);
        model.addAttribute("subjectForm", subjectDTO);
        return "admin/subject-edit";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/subjects/edit")
    public String updateSubject(
            @RequestParam("originalName") String originalName,
            @ModelAttribute("subjectForm") SubjectDTO subjectDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/subject-edit";
        }

        try {
            subjectService.updateSubject(originalName, subjectDTO.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Предмет успешно обновлён!");
            return "redirect:/admin/subjects";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
            return "redirect:/admin/subjects/edit?name=" + originalName;
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/subjects/delete")
    public String deleteSubject(
            @RequestParam("name") String name,
            RedirectAttributes redirectAttributes) {
        try {
            subjectService.deleteSubjectByName(name);
            redirectAttributes.addFlashAttribute("successMessage", "Предмет успешно удалён!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/admin/subjects";
    }
}
