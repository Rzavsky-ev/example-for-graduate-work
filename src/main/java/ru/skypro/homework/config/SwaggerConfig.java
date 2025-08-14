package ru.skypro.homework.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Конфигурационный класс для настройки Swagger/OpenAPI документации.
 * <p>
 * Автоматически настраивает:
 * <ul>
 *   <li>Базовую информацию об API (название, версия, описание)</li>
 *   <li>Механизм авторизации через JWT-токен</li>
 *   <li>Схему безопасности для всех защищенных endpoints</li>
 * </ul>
 */
@Configuration
public class SwaggerConfig {

    /**
     * Создает конфигурацию OpenAPI с настройками безопасности и метаинформацией.
     *
     * @return Настроенный экземпляр {@link OpenAPI} со следующими параметрами:
     * <ul>
     *   <li>Схема безопасности "bearerAuth" (JWT-токен в заголовке Authorization)</li>
     *   <li>Базовая информация об API (название, версия, описание)</li>
     * </ul>
     */
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        .addSecuritySchemes("bearerAuth",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("Your API Title")
                        .version("1.0")
                        .description("API Description"));
    }
}

