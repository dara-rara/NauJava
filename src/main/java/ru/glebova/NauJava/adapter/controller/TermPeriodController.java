package ru.glebova.NauJava.adapter.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.glebova.NauJava.domain.TermPeriod;
import ru.glebova.NauJava.service.TermPeriodService;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequestMapping("/admin/periods")
public class TermPeriodController {

    private final TermPeriodService termPeriodService;

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping
    public String showAllPeriods(Model model) {
        model.addAttribute("periods", termPeriodService.getAllPeriods());
        return "admin/period-list";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("period", new TermPeriod());
        return "admin/period-create";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/create")
    public String createPeriod(@ModelAttribute TermPeriod period) {
        termPeriodService.createTermPeriod(period);
        return "redirect:/admin/periods";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        TermPeriod period = termPeriodService.getById(id);
        model.addAttribute("period", period);
        return "admin/period-edit";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/update")
    public String updatePeriod(@ModelAttribute TermPeriod period) {
        termPeriodService.updateTermPeriod(period);
        return "redirect:/admin/periods";
    }
}
