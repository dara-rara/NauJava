package ru.glebova.NauJava.adapter.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.glebova.NauJava.adapter.controller.dto.RegisterDTO;
import ru.glebova.NauJava.service.UsersService;

@Controller
public class UsersController {
    private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new RegisterDTO());
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(
            @ModelAttribute("user") RegisterDTO registrationDto,
            Model model) {
        if (usersService.userExist(registrationDto.getUsername())) {
            model.addAttribute("errorMessage", "Такой логин уже существует");
            return "registration";
        }
        usersService.registerNewUser(registrationDto);
        return "redirect:/login?registered";
    }

    @GetMapping("/login")
    public String showLoginForm(
            @RequestParam(value = "error", required = false) String error,
            @RequestParam(value = "registered", required = false) String registered,
            Model model) {
        return "login";
    }

    @GetMapping("/logout")
    public String showLogoutPage() {
        return "logout";
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("username", authentication.getName());
        } else {
            model.addAttribute("username", "Гость");
        }
        return "home";
    }
}
