package com.example.ticket_system.controller;

import com.example.ticket_system.dao.UserDAO;
import com.example.ticket_system.model.Role;
import com.example.ticket_system.model.Route;
import com.example.ticket_system.service.RouteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/routes")
@Tag(name = "Route API", description = "Управление маршрутами")
public class RouteController {
    private final RouteService service;
    private final UserDAO userDao;

    public RouteController(RouteService service, UserDAO userDao) {
        this.service = service;
        this.userDao = userDao;
    }

    @Operation(summary = "Создать маршрут")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Route route, @RequestParam long userId) {
        Set<Role> roles = userDao.getUserRoles(userId);
        if (!roles.contains(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
        }
        long id = service.addRoute(route);
        return ResponseEntity.status(HttpStatus.CREATED).body("Маршрут добавлен");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRoute(@PathVariable long id, @RequestBody Route route, @RequestParam long userId) {
        if (!userDao.getUserRoles(userId).contains(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("status","error","message","Недостаточно прав"));
        }
        boolean ok = service.updateRoute(id, route);
        if (!ok){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status","error","message","Билет не найден"));
        }
        return ResponseEntity.ok("Маршрут обновлён");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRoute(@PathVariable long id, @RequestParam long userId) {
        if (!userDao.getUserRoles(userId).contains(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
        }
        boolean ok = service.deleteRoute(id);
        if (!ok){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Маршрут не найден");
        }
        service.deleteRoute(id);
        return ResponseEntity.ok("Маршрут удалён");
    }

    @Operation(summary = "Список маршрутов")
    @GetMapping
    public ResponseEntity<?> list() {
        List<Route> routes = service.getAll();
        if(routes.isEmpty()){
            return ResponseEntity.ok(Map.of(
                    "status",
                    "ok",
                    "message",
                    "Список маршрутов пуст"));
        }
        return ResponseEntity.ok(service.getAll());
    }

    @Operation(summary = "Получить маршрут по ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable long id) {
        Route route = service.getById(id);
        if (route == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("status","error","message","Маршрут не найден"));
        return ResponseEntity.ok(route);
    }

    @Operation(summary = "Сколько маршрутов у перевозчика")
    @GetMapping("/by-carrier/{carrierId}/count")
    public ResponseEntity<Map<String,Object>> countByCarrier(@PathVariable long carrierId) {
        return ResponseEntity.ok(Map.of("carrierId", carrierId, "routes", service.countByCarrier(carrierId)));
    }
}
