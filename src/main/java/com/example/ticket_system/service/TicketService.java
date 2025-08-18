package com.example.ticket_system.service;

import com.example.ticket_system.dao.TicketDAO;
import com.example.ticket_system.model.Ticket;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class TicketService {
    private final TicketDAO dao;

    public TicketService(TicketDAO dao) {
        this.dao = dao;
    }

    public long addTicket(Ticket ticket) {
        return dao.createTicket(ticket);
    }

    public List<Ticket> getAvailable(String departure, String destination, String carrier,
                                     LocalDate date, int page, int size) {
        int offset = Math.max(0, (page - 1) * size);
        size = Math.min(size, 100);
        return dao.findAvailable(departure, destination, carrier, date, offset, size);
    }

    /**
     * @return 1 — купили; 0 — не удалось (уже куплен); -1 — нет такого билета
     */
    @Transactional
    public int buy(long ticketId, long userId) {
        if (!dao.existsById(ticketId)){
            return -1;
        }
        return dao.buyTicket(ticketId, userId);
    }

    public List<Ticket> getUserTickets(long userId, int page, int size) {
        int offset = Math.max(0, (page - 1) * size);
        size = Math.min(size, 100);
        return dao.findUserTickets(userId, offset, size);
    }

    public Ticket getById(long id) {
        return dao.findById(id);
    }

    public boolean updateTicket(long id, Ticket ticket) {
        if (!dao.existsById(id)){
            return false;
        }
        return dao.updateTicket(id, ticket) > 0;
    }

    public boolean deleteTicket(long id) {
        if (!dao.existsById(id)){
            return false;
        }
        return dao.deleteTicket(id) > 0;
    }
}
