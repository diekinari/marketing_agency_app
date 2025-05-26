package com.example.marketing_agency_app.service;

import com.example.marketing_agency_app.model.AppUser;
import com.example.marketing_agency_app.repository.AppUserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Сервис для управления пользователями приложения:
 * регистрация и базовые операции над сущностью AppUser.
 */
@Service
public class AppUserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Конструктор сервиса пользователей.
     * Инициализирует репозиторий пользователей и шифратор паролей.
     *
     * @param userRepository репозиторий для работы с AppUser
     */
    public AppUserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Регистрирует нового пользователя.
     * <ul>
     *     <li>Проверяет, что имя пользователя ещё не занято.</li>
     *     <li>Шифрует пароль с помощью BCrypt.</li>
     *     <li>Сохраняет пользователя в базе.</li>
     * </ul>
     *
     * @param user объект пользователя с исходными данными
     * @return сохранённый объект пользователя с зашифрованным паролем
     * @throws IllegalArgumentException если пользователь с таким именем уже существует
     */
    public AppUser registerUser(AppUser user) {
        if (userRepository.findByUsername(user.getUsername()) != null) {
            throw new IllegalArgumentException(
                    "User with username \"" + user.getUsername() + "\" already exists"
            );
        }
        if (userRepository.findByEmail(user.getEmail()) != null) {
            throw new IllegalArgumentException("Email already registered");
        }
        // Шифруем пароль перед сохранением
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

}
