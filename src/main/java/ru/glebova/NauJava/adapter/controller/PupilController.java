package ru.glebova.NauJava.adapter.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.glebova.NauJava.domain.Classes;
import ru.glebova.NauJava.domain.Pupil;
import ru.glebova.NauJava.domain.Role;
import ru.glebova.NauJava.domain.Users;
import ru.glebova.NauJava.service.ClassesService;
import ru.glebova.NauJava.service.PupilService;
import ru.glebova.NauJava.service.UsersService;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class PupilController {

    private final ClassesService classService;
    private final PupilService pupilService;
    private final UsersService userService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("admin/{className}/pupils")
    public String showClassPupils(@PathVariable String className, Model model) {
        Classes schoolClass = classService.findByName(className);

        List<Pupil> pupils = pupilService.findByClass(schoolClass);
        List<Users> availablePupils = userService.findUsersByRole(Role.PUPIL)
                .stream()
                .filter(user -> pupilService.findByUser(user).isEmpty())
                .collect(Collectors.toList());

        model.addAttribute("class", schoolClass);
        model.addAttribute("pupils", pupils);
        model.addAttribute("availablePupils", availablePupils);

        return "admin/classes-pupils";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("admin/pupil/add")
    public String addPupilToClass(@RequestParam String className,
                                  @RequestParam Long userId,
                                  RedirectAttributes redirectAttributes) {
        try {
            pupilService.addPupilToClass(userId, className);
            redirectAttributes.addFlashAttribute("successMessage", "Ученик успешно добавлен в класс");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при добавлении ученика: " + e.getMessage());
        }
        String encodedClassName = URLEncoder.encode(className, StandardCharsets.UTF_8);
        return "redirect:/admin/" + encodedClassName + "/pupils";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("admin/pupil/remove")
    public String removePupilFromClass(@RequestParam String className,
                                       @RequestParam Long pupilId,
                                       RedirectAttributes redirectAttributes) {
        try {
            pupilService.removePupilFromClass(pupilId);
            redirectAttributes.addFlashAttribute("successMessage", "Ученик успешно удален из класса");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении ученика: " + e.getMessage());
        }
        String encodedClassName = URLEncoder.encode(className, StandardCharsets.UTF_8);
        return "redirect:/admin/" + encodedClassName + "/pupils";
    }
}
