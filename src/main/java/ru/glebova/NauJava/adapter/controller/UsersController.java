package ru.glebova.NauJava.adapter.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import ru.glebova.NauJava.adapter.controller.dto.UserDTO;
import ru.glebova.NauJava.domain.Role;
import ru.glebova.NauJava.exception.dto.ImportResult;
import ru.glebova.NauJava.service.impl.UsersServiceImpl;

import java.io.IOException;

@Controller
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class UsersController {
    private final UsersServiceImpl usersService;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Model model, Authentication authentication) {
        return authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .findFirst()
                .map(role -> {
                    switch (role) {
                        case "ROLE_ADMIN":
                            return "redirect:/admin/home";
                        case "ROLE_TEACHER":
                            return "redirect:/teacher/home";
                        case "ROLE_PUPIL":
                            return "redirect:/pupil/home";
                        default:
                            model.addAttribute("users", authentication.getPrincipal());
                            return "home";
                    }
                })
                .orElse("redirect:/login?error=no_roles");
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/home")
    public String adminHome(Model model, Authentication authentication) {
        model.addAttribute("user", authentication.getPrincipal());
        return "admin/home";
    }

    @PreAuthorize("hasAuthority('ROLE_TEACHER')")
    @GetMapping("/teacher/home")
    public String teacherHome(Model model, Authentication authentication) {
        model.addAttribute("user", authentication.getPrincipal());
        return "teacher/home";
    }

    @PreAuthorize("hasAuthority('ROLE_PUPIL')")
    @GetMapping("/pupil/home")
    public String pupilHome(Model model, Authentication authentication) {
        model.addAttribute("user", authentication.getPrincipal());
        return "pupil/home";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/users")
    public String usersList(Model model) {
        model.addAttribute("users", usersService.getAllUsers());
        return "admin/users-list";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/users/new")
    public String showCreateUserForm(Model model) {
        model.addAttribute("userForm", new UserDTO());
        model.addAttribute("roles", Role.values());
        return "admin/users-create";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/users")
    public String createUser(
            @ModelAttribute("userForm") UserDTO userDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.userForm", result);
            redirectAttributes.addFlashAttribute("userForm", userDTO);
            return "redirect:/admin/users/new";
        }

        try {
            usersService.createUser(userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно создан!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/users/edit")
    public String showEditUserForm(@RequestParam("username") String username, Model model) {
        UserDTO userDTO = usersService.getUserByUsername(username);
        model.addAttribute("userForm", userDTO);
        model.addAttribute("roles", Role.values());
        model.addAttribute("originalUsername", username);
        return "admin/users-edit";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/users/edit")
    public String updateUser(
            @RequestParam("originalUsername") String originalUsername,
            @ModelAttribute("userForm") UserDTO userDTO,
            BindingResult result,
            RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            return "admin/users-edit";
        }

        try {
            usersService.updateUser(originalUsername, userDTO);
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно обновлён!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при обновлении: " + e.getMessage());
            return "redirect:/admin/users/edit?username=" + originalUsername;
        }
        return "redirect:/admin/users";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/users/delete")
    public String deleteUser(
            @RequestParam("username") String username,
            RedirectAttributes redirectAttributes) {
        try {
            usersService.deleteUserByUsername(username);
            redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно удалён!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Ошибка при удалении: " + e.getMessage());
        }
        return "redirect:/admin/users";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/users/template")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=users_template.csv");

        response.getOutputStream().write(0xEF);
        response.getOutputStream().write(0xBB);
        response.getOutputStream().write(0xBF);

        usersService.generateUserTemplate(response.getOutputStream());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/admin/users/upload")
    public String uploadUsers(
            @RequestParam("file") MultipartFile file,
            @RequestParam("role") Role role,
            RedirectAttributes redirectAttributes) {

        try {
            ImportResult result = usersService.importUsersFromFile(file, role);

            if (result.getSuccessCount() > 0) {
                redirectAttributes.addFlashAttribute("success",
                        "Количество успешно импортированных строк: " + result.getSuccessCount());
            }

            if (result.hasErrors()) {
                redirectAttributes.addFlashAttribute("importErrors", result.getErrors());
            }

            if (result.getSuccessCount() == 0 && !result.hasErrors()) {
                redirectAttributes.addFlashAttribute("warning",
                        "Файл не содержит данных для импорта");
            }
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка чтения файла: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                    "Ошибка импорта: " + e.getMessage());
        }

        return "redirect:/admin/users";
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @GetMapping("/admin/settings")
    public String settings() {
        return "admin/settings";
    }
}
