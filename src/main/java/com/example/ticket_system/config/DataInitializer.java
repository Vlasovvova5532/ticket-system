package com.example.ticket_system.config;

import com.example.ticket_system.dao.UserDAO;
import com.example.ticket_system.model.Role;
import com.example.ticket_system.model.User;
import com.example.ticket_system.service.UserService;
import jakarta.annotation.PostConstruct;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Set;

@Configuration
public class DataInitializer {
    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @PostConstruct
    public void init() {
        String adminLogin = "admin";
        String adminPassword = "admin";

        if (!userService.existsByLogin(adminLogin)) {
            userService.createAdminIfNotExists();
            System.out.println("Админ создан: " + adminLogin + " / " + adminPassword);
        } else {
            System.out.println("Админ уже существует: " + adminLogin);
        }
    }

}
