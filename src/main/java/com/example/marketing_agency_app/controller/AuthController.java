package com.example.marketing_agency_app.controller;

import com.example.marketing_agency_app.model.AppUser;
import com.example.marketing_agency_app.service.AppUserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * Контроллер для обработки регистрации и входа пользователей.
 */
@Controller
public class AuthController {

    private final AppUserService userService;

    /**
     * Конструктор с внедрением сервиса пользователей.
     *
     * @param userService сервис для управления пользователями
     */
    public AuthController(AppUserService userService) {
        this.userService = userService;
    }

    /**
     * Обрабатывает GET-запрос на страницу регистрации.
     *
     * @param model модель для передачи данных в представление
     * @return имя шаблона страницы регистрации
     */
    @GetMapping("/register")
    public String showRegisterForm(Model model) {
        model.addAttribute("user", new AppUser());
        return "register";
    }

    /**
     * Обрабатывает POST-запрос регистрации нового пользователя.
     *
     * @param user  объект пользователя, заполненный из формы
     * @param model модель для передачи ошибок обратно в представление
     * @return перенаправление на страницу входа или возврат на форму с ошибкой
     */
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") AppUser user, Model model) {
        try {
            userService.registerUser(user);
            return "redirect:/login?registered";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "register";
        }
    }

    /**
     * Обрабатывает GET-запрос на страницу входа.
     *
     * @return имя шаблона страницы входа
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
