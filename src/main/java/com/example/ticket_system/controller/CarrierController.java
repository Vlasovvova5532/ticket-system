package com.example.ticket_system.controller;

import com.example.ticket_system.dao.UserDAO;
import com.example.ticket_system.model.Carrier;
import com.example.ticket_system.model.Role;
import com.example.ticket_system.service.CarrierService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("api/carriers")
@Tag(name="Carrier API", description = "Управлеине перевозчиками")
public class CarrierController {

    private final CarrierService service;
    private final UserDAO userDao;

    public CarrierController(CarrierService service, UserDAO userDao) {
        this.service = service;
        this.userDao = userDao;
    }

    @Operation(summary = "Создать перевозчика")
    @PostMapping
    public ResponseEntity<?> createCarrier(@Valid @RequestBody Carrier carrier, @RequestParam long userId) {
        try {
            Set<Role> roles = userDao.getUserRoles(userId);
            if (!roles.contains(Role.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
            }
            long id = service.addCarrier(carrier);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("Перевозчик добавлен", id));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateCarrier(@PathVariable long id, @RequestBody Carrier carrier, @RequestParam long userId) {
        if (!userDao.getUserRoles(userId).contains(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
        }
        boolean ok = service.updateCarrier(id, carrier);
        if (!ok){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Перевозчик не найден");
        }
        return ResponseEntity.ok("Перевозчик обновлён");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCarrier(@PathVariable long id, @RequestParam long userId) {
        if (!userDao.getUserRoles(userId).contains(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
        }
        boolean ok = service.deleteCarrier(id);
        if (!ok){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Перевозчик не найден");
        }
        return ResponseEntity.ok("Перевозчик удалён");
    }

    @Operation(summary = "Список перевозчиков")
    @GetMapping
    public ResponseEntity <?> listCarriers() {
        List<Carrier> carriers = service.getAllCarrier();
        if (carriers.isEmpty()) {
            return ResponseEntity.ok(Map.of(
                    "status", "ok",
                    "message", "Список перевозчиков пуст"
            ));
        }
        return ResponseEntity.ok(carriers);
    }

    @Operation(summary = "Получить перевозчика по ID")
    @GetMapping("/{id}")
    public ResponseEntity<?> getCarrierById(@PathVariable long id) {
        Carrier carrier = service.getById(id);
        if (carrier == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("status","error","message","Перевозчик не найден"));
        return ResponseEntity.ok(carrier);
    }

    @Operation(summary = "Сколько маршрутов у перевозчика")
    @GetMapping("/{id}/routes/count")
    public ResponseEntity<Map<String,Object>> routesCount(@PathVariable long id) {
        return ResponseEntity.ok(Map.of("carrierId", id, "routes", service.getRoutesCount(id)));
    }

}
