package com.example.marketing_agency_app.service;

import com.example.marketing_agency_app.model.AppUser;
import com.example.marketing_agency_app.repository.AppUserRepository;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для управления пользователями приложения:
 * регистрация, получение, список и обновление роли.
 */
@Service
public class AppUserService {

    private final AppUserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(AppUserRepository userRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    /**
     * Регистрирует нового пользователя.
     * @throws IllegalArgumentException если username или email уже заняты.
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
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        // по умолчанию роль USER уже стоит в поле role
        return userRepository.save(user);
    }

    /**
     * Возвращает всех пользователей, отсортированных по username.
     */
    public List<AppUser> listAllUsers() {
        return userRepository.findAll(Sort.by("username"));
    }

    /**
     * Ищет пользователя по ID.
     * @throws IllegalArgumentException, если не найден.
     */
    public AppUser getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + id));
    }

    /**
     * Меняет роль пользователя.
     * Операция — в транзакции, чтобы автоматически сохранить изменения.
     *
     * @param userId  ID пользователя
     * @param newRole новая роль (без префикса ROLE_)
     */
    @Transactional
    public void updateUserRole(Long userId, String newRole) {
        AppUser user = getUserById(userId);
        user.setRole(newRole);
        // благодаря @Transactional и dirty-checking save() не нужен
    }
}
