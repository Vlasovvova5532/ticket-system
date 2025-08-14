package com.example.ticket_system.controller;

import com.example.ticket_system.model.Ticket;
import com.example.ticket_system.service.TicketService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Ticket API", description = "Работа с билетами")
public class TicketController {

    private final TicketService service;

    public TicketController(TicketService service) { this.service = service; }

    @Operation(summary = "Добавить билет")
    @PostMapping
    public ResponseEntity<?> createTicket(@Valid @RequestBody Ticket ticket) {
        try {
            long id = service.addTicket(ticket);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("id", id, "status", "ok"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @Operation(summary = "Доступные билеты (фильтры + пагинация)")
    @GetMapping("/available")
    public ResponseEntity<List<Ticket>> available(
            @RequestParam(required = false) String departure,
            @RequestParam(required = false) String destination,
            @RequestParam(required = false) String carrierName,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.getAvailable(departure, destination, carrierName, date, page, size));
    }

    @Operation(summary = "Купить билет (атомарно)")
    @PostMapping("/buy/{ticketId}")
    public ResponseEntity<?> buy(@PathVariable long ticketId, @RequestParam long userId) {
        int res = service.buy(ticketId, userId);
        if (res == -1) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("status","error","message","Билет не найден"));
        }
        if (res == 0) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("status","error","message","Билет куплен другим человеком"));
        }
        return ResponseEntity.ok(Map.of("status","success","ticketId", ticketId, "userId", userId));
    }

    @Operation(summary = "Мои билеты (купленные)")
    @GetMapping("/my")
    public ResponseEntity<List<Ticket>> myTickets(
            @RequestParam long userId,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(service.getUserTickets(userId, page, size));
    }

    @Operation(summary = "Детали билета")
    @GetMapping("/{id}")
    public ResponseEntity<?> get(@PathVariable long id) {
        Ticket ticket = service.getById(id);
        if (ticket == null) return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("status","error","message","Не найдены детали билета"));
        return ResponseEntity.ok(ticket);
    }
}
