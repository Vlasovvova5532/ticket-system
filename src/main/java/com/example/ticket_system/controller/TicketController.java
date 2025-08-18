package com.example.ticket_system.controller;

import com.example.ticket_system.dao.UserDAO;
import com.example.ticket_system.dto.TicketFilter;
import com.example.ticket_system.model.Role;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/tickets")
@Tag(name = "Ticket API", description = "Работа с билетами")
public class TicketController {

    private final TicketService service;
    private final UserDAO userDAO;

    public TicketController(TicketService ticketService, UserDAO userDAO) {
        this.service = ticketService;
        this.userDAO = userDAO;
    }

    @Operation(summary = "Добавить билет")
    @PostMapping
    public ResponseEntity<?> createTicket(@Valid @RequestBody Ticket ticket, @RequestParam long userId) {
        try {
            Set<Role> roles = userDAO.getUserRoles(userId);
            if (!roles.contains(Role.ADMIN)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Недостаточно прав");
            }

            long id = service.addTicket(ticket);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(Map.of("id", id, "status", "ok"));
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("status", "error", "message", e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTicket(@PathVariable long id, @RequestBody Ticket ticket, @RequestParam long userId) {
        if (!userDAO
                .getUserRoles(userId)
                .contains(Role.ADMIN)
        ) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("status","error","message","Недостаточно прав"));
        }
        boolean ok = service.updateTicket(id, ticket);
        if (!ok){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status","error","message","Билет не найден"));
        }
        return ResponseEntity.ok("Билет обновлён");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTicket(@PathVariable long id, @RequestParam long userId) {
        if (!userDAO.getUserRoles(userId).contains(Role.ADMIN)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Map.of("status","error","message","Недостаточно прав"));
        }
        boolean ok = service.deleteTicket(id);
        if (!ok){
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("status","error","message","Билет не найден"));
        }

        return ResponseEntity.ok("Билет удалён");
    }

    @Operation(summary = "Доступные билеты (фильтры + пагинация)")
    @GetMapping("/available")
    public ResponseEntity<?> available(@Valid TicketFilter filter){
            List<Ticket> tickets = service.getAvailable(
                    filter.getDeparture(),
                    filter.getDestination(),
                    filter.getCarrierName(),
                    filter.getDate(),
                    filter.getPage(),
                    filter.getSize()
            );

    Map<String, Object> response = new HashMap<>();
        response.put("tickets", tickets);
        response.put("page", filter.getPage());
        response.put("size", filter.getSize());
        response.put("count", tickets.size());

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Купить билет (атомарно)")
    @PostMapping("/buy/{ticketId}")
    public ResponseEntity<?> buy(@PathVariable long ticketId, @RequestBody Map<String, Long> body) {
        long userId = body.get("userId");
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
