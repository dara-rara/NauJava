package ru.glebova.NauJava.adapter.controller;

import jakarta.persistence.EntityExistsException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.glebova.NauJava.adapter.controller.dto.RegisterDTO;
import ru.glebova.NauJava.service.UsersService;

@Controller
public class AuthController {

    private final UsersService usersService;

    public AuthController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegisterDTO());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") RegisterDTO registrationDto, Model model) {
        try {
            usersService.registerNewUser(registrationDto);
            return "redirect:/login?registered";
        } catch (EntityExistsException e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "registration";
        }
    }

    @GetMapping("/login")
    public String showLoginForm(Model model,
                                @RequestParam(value = "error", required = false) String error,
                                @RequestParam(value = "registered", required = false) String registered) {
        return "login";
    }

    @GetMapping("/logout")
    public String showLogoutPage() {
        return "logout";
    }
}
