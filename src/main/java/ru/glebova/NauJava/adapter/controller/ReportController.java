package ru.glebova.NauJava.adapter.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.glebova.NauJava.domain.Report;
import ru.glebova.NauJava.service.ReportService;

@Controller
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping
    public String getReportPage(Model model, Authentication authentication) {
        model.addAttribute("username", authentication.getName());
        model.addAttribute("report", reportService.getLatestReport());
        return "report";
    }

    @GetMapping("/generate")
    public String generateNewReport() {
        Long reportId = reportService.createReport();
        reportService.generateReportAsync(reportId);
        return "redirect:/report";
    }
}
