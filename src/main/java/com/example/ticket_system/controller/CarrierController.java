package com.example.ticket_system.controller;

import com.example.ticket_system.model.Carrier;
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

@RestController
@RequestMapping("api/carriers")
@Tag(name="Carrier API", description = "Управлеине перевозчиками")
public class CarrierController {

    private final CarrierService service;

    public CarrierController(CarrierService service) {
        this.service = service;
    }

    @Operation(summary = "Создать перевозчика")
    @PostMapping
    public ResponseEntity<?> createCarrier(@Valid @RequestBody Carrier carrier) {
        try {
            long id = service.addCarrier(carrier);
            return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
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
