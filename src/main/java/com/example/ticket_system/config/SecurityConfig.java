package com.example.ticket_system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
//                        .requestMatchers("/register",
//                                "/swagger-ui.html",
//                                "/swagger-ui/**",
//                                "/v3/api-docs/**",
//                                "/v3/api-docs",
//                                "/v3/api-docs.yaml",
//                                "/v3/api-docs/swagger-config").permitAll() // разрешаем без токена
//                        .anyRequest().authenticated() // остальные требуют авторизации
                                .anyRequest().permitAll() // разрешаем все запросы без авторизации
                );

        return http.build();
    }
}
