package com.example.ticket_system.controller;

import com.example.ticket_system.model.User;
import com.example.ticket_system.service.UserService;
import com.example.ticket_system.model.Role;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/users")
@Tag(name = "User API", description = "Регистрация пользователей")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/register")
    public ResponseEntity<?> info() {
        return ResponseEntity.ok("Это ручка для регистрации. Используйте POST с JSON.");
    }

    @Operation(summary = "Регистрация нового пользователя")
    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody User user, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(e ->
                    errors.put(
                            e.getField(),
                            e.getDefaultMessage()
                    )
            );
            return ResponseEntity.badRequest().body(Map.of("status", "error", "errors", errors));
        }
        try {
            user.setRoles(Set.of(Role.CUSTOMER));
            userService.registerUser(user);
            return ResponseEntity.status(HttpStatus.CREATED).body("Пользователь успешно зарегистрирован");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Operation(summary = "Назначить роль ADMIN пользователю")
    @PostMapping("/{userId}/make-admin")
    public ResponseEntity<?> makeAdmin(@PathVariable Long userId) {
        try {
            userService.assignAdminRole(userId);
            return ResponseEntity.ok(Map.of(
                    "status", "success",
                    "message", "Пользователю назначена роль ADMIN"
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "status", "error",
                    "message", e.getMessage()
            ));
        }
    }

    @Operation(summary = "Получить список всех пользователей")
    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        List<User> users = userService.getAllUsers();
        if (users.isEmpty()){
            return ResponseEntity.ok(Map.of(
                    "status",
                    "ok",
                    "message",
                    "Список пользователей пуст"));
        }
        return ResponseEntity.ok(users);
    }
}
