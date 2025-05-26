package com.example.marketing_agency_app.config;

import com.example.marketing_agency_app.model.AppUser;
import com.example.marketing_agency_app.repository.AppUserRepository;
import com.example.marketing_agency_app.service.CustomUserDetailsService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Конфигурация безопасности Spring Security.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final CustomUserDetailsService userDetailsService;

    /**
     * Конструктор с внедрением сервиса пользовательских данных.
     *
     * @param userDetailsService сервис, реализующий логику загрузки пользователей
     */
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    /**
     * Конфигурация фильтра безопасности, определяющая правила доступа и настройки формы входа.
     *
     * @param http объект конфигурации безопасности
     * @return фильтр безопасности
     * @throws Exception в случае ошибки конфигурации
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(requests -> requests
                        .requestMatchers("/login", "/register", "/error").permitAll()
                        .anyRequest().authenticated()
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );
        return http.build();
    }

    /**
     * Бин для шифрования паролей с использованием BCrypt.
     *
     * @return шифратор паролей
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Настройка аутентификации с использованием DAO-провайдера и шифрования пароля.
     *
     * @return объект аутентификации
     */
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    /**
     * Создаёт администратора с логином admin и паролем admin при первом запуске приложения,
     * если он ещё не существует в базе данных.
     *
     * @param userRepository репозиторий пользователей
     * @param passwordEncoder шифратор пароля
     * @return выполняемый компонент при старте приложения
     */
    @Bean
    public CommandLineRunner createAdminUser(AppUserRepository userRepository, PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.findByUsername("admin") == null) {
                AppUser admin = new AppUser();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole("ADMIN");
                userRepository.save(admin);
                System.out.println("Admin user created with username 'admin' and password 'admin'");
            }
        };
    }

}
