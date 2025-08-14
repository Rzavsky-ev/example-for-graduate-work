package ru.skypro.homework.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import ru.skypro.homework.dto.Role;


import static org.springframework.security.config.Customizer.withDefaults;

/**
 * Конфигурационный класс Spring Security, определяющий настройки безопасности приложения.
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig {

    /**
     * Список URL-путей, исключенных из проверки аутентификации.
     */
    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources/**",
            "/swagger-ui.html",
            "/v3/api-docs",
            "/webjars/**",
            "/login",
            "/register",
            "/ads/image/**"
    };

    /**
     * Конфигурирует цепочку фильтров безопасности HTTP.
     *
     * @param http объект конфигурации HTTP-безопасности
     * @return сконфигурированная цепочка фильтров
     * @throws Exception если произошла ошибка конфигурации
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf()
                .disable()
                .authorizeHttpRequests(authorization ->
                        authorization
                                .mvcMatchers(AUTH_WHITELIST)
                                .permitAll()
                                .mvcMatchers("/ads/**", "/users/**")
                                .authenticated()
                                .mvcMatchers("/admin/**").hasRole(Role.ADMIN.name()))
                .cors()
                .and()
                .httpBasic(withDefaults());
        return http.build();
    }

    /**
     * Создает бин для кодирования паролей.
     *
     * @return реализация {@code PasswordEncoder} с алгоритмом BCrypt
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
