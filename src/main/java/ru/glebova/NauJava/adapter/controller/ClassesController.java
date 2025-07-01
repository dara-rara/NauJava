package ru.glebova.NauJava.adapter.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.glebova.NauJava.adapter.controller.dto.ClassesDTO;
import ru.glebova.NauJava.service.ClassesService;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class ClassesController {

    private final ClassesService classesService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/classes")
    public String classesList(Model model,
                              @RequestParam(required = false) String success,
                              @RequestParam(required = false) String error) {
        model.addAttribute("classes", classesService.getAllClasses());
        if (success != null) model.addAttribute("successMessage", "Операция выполнена успешно!");
        if (error != null) model.addAttribute("errorMessage", "Произошла ошибка!");
        return "admin/classes-list";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/classes/new")
    public String showCreateClassForm(Model model) {
        model.addAttribute("classForm", new ClassesDTO());
        return "admin/classes-create";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/classes")
    public String createClass(
            @ModelAttribute("classForm") ClassesDTO classesDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/classes-create";
        }

        try {
            classesService.createClass(classesDTO);
            redirectAttributes.addFlashAttribute("success", true);
            return "redirect:/admin/classes";
        } catch (Exception e) {
            redirectAttributes.addAttribute("error", true);
            return "redirect:/admin/classes";
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/classes/edit")
    public String showEditClassForm(@RequestParam("name") String name, Model model) {
        ClassesDTO classesDTO = classesService.getClassByName(name);
        model.addAttribute("classForm", classesDTO);
        model.addAttribute("originalName", name);
        return "admin/classes-edit";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/classes/edit")
    public String updateClass(
            @RequestParam("originalName") String originalName,
            @ModelAttribute("classForm") ClassesDTO classesDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/classes-edit";
        }

        try {
            classesService.updateClasses(originalName, classesDTO.getName());
            redirectAttributes.addFlashAttribute("successMessage", "Класс успешно обновлён!");
            return "redirect:/admin/classes";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
            return "redirect:/admin/classes/edit?name=" + originalName;
        }
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/classes/delete")
    public String deleteClass(
            @RequestParam("name") String name,
            RedirectAttributes redirectAttributes) {
        try {
            classesService.deleteClassesByName(name);
            redirectAttributes.addFlashAttribute("successMessage", "Класс успешно удалён!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/admin/classes";
    }
}
