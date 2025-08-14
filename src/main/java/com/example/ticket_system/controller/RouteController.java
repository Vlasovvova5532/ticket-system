package com.example.ticket_system.controller;

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

@RestController
@RequestMapping("/api/routes")
@Tag(name = "Route API", description = "Управление маршрутами")
public class RouteController {
    private final RouteService service;

    public RouteController(RouteService service) {
        this.service = service;
    }

    @Operation(summary = "Создать маршрут")
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody Route route) {
        long id = service.addRoute(route);
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
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
