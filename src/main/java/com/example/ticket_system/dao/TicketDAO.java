package com.example.ticket_system.dao;

import com.example.ticket_system.model.Ticket;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Repository
public class TicketDAO {

    private final JdbcTemplate jdbcTemplate;

    public TicketDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Ticket> mapper = (rs, rowNum) -> {
        Ticket ticket = new Ticket();
        ticket.setId(rs.getLong("id"));
        ticket.setRouteId(rs.getLong("route_id"));

        ticket.setUserId(rs.getObject("user_id") != null ? rs.getLong("user_id") : null);

        ticket.setDateTime(rs.getTimestamp("date_time").toLocalDateTime());
        ticket.setSeatNumber(rs.getInt("seat_number"));
        ticket.setPrice(rs.getBigDecimal("price"));
        ticket.setIsSold(rs.getBoolean("is_sold"));
        return ticket;
    };

    //создать билет
    public long createTicket(Ticket ticket) {
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO tickets(route_id, date_time, seat_number, price, is_sold) " +
                        "VALUES(?, ?, ?, ?, ?) RETURNING id",
                Long.class,
                ticket.getRouteId(),
                Timestamp.valueOf(ticket.getDateTime()),
                ticket.getSeatNumber(),
                ticket.getPrice(),
                ticket.getIsSold()
        );

        if (id == null) {
            throw new IllegalStateException("Не удалось получить ID билета");
        }
        return id;
    }

    public boolean existsById(long ticketId) {
        Integer countTicket = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tickets WHERE id = ?", Integer.class, ticketId);
        return countTicket != null && countTicket > 0;
    }

    /**
     * Покупка: атомарно помечаем билет проданным и ставим user_id,
     * только если он ещё не продан.
     * Возвращает число затронутых строк (0 — не получилось; 1 — купили).
     */
    public int buyTicket(long ticketId, long userId) {
        return jdbcTemplate.update("""
            UPDATE tickets
               SET user_id = ?, is_sold = TRUE
             WHERE id = ?
               AND (is_sold = FALSE OR is_sold IS NULL)
               AND user_id IS NULL
        """, userId, ticketId);
    }

    public List<Ticket> findUserTickets(long userId, int offset, int limit) {
        return jdbcTemplate.query("""
            SELECT id, route_id, user_id, date_time, seat_number, price, is_sold
            FROM tickets
            WHERE user_id = ?
            ORDER BY date_time DESC
            LIMIT ? OFFSET ?
        """, mapper, userId, limit, offset);
    }

    /**
     * Список доступных билетов с фильтрами и пагинацией.
     * Фильтры: дата (YYYY-MM-DD), departure/destination (LIKE), carrierName (LIKE).
     * Доступные = не проданы (is_sold = false AND user_id IS NULL).
     */
    public List<Ticket> findAvailable(String departure, String destination, String carrierName,
                                      LocalDate date, int offset, int limit) {
        StringBuilder sql = new StringBuilder("""
            SELECT t.id, t.route_id, t.user_id, t.date_time, t.seat_number, t.price, t.is_sold
              FROM tickets t
              JOIN routes r ON t.route_id = r.id
              JOIN carriers c ON r.carrier_id = c.id
             WHERE (t.is_sold = FALSE OR t.is_sold IS NULL)
               AND t.user_id IS NULL
        """);

        List<Object> params = new ArrayList<>();

        if (departure != null && !departure.isBlank()) {
            sql.append(" AND LOWER(r.departure) LIKE LOWER(?) ");
            params.add("%" + departure + "%");
        }
        if (destination != null && !destination.isBlank()) {
            sql.append(" AND LOWER(r.destination) LIKE LOWER(?) ");
            params.add("%" + destination + "%");
        }
        if (carrierName != null && !carrierName.isBlank()) {
            sql.append(" AND LOWER(c.name) LIKE LOWER(?) ");
            params.add("%" + carrierName + "%");
        }
        if (date != null) {
            sql.append(" AND DATE(t.date_time) = ? ");
            params.add(date);
        }

        sql.append(" ORDER BY t.date_time ASC LIMIT ? OFFSET ? ");
        params.add(limit);
        params.add(offset);

        return jdbcTemplate.query(sql.toString(), mapper, params.toArray());
    }

    public Ticket findById(long id) {
        try {
            return jdbcTemplate.queryForObject("""
                SELECT id, route_id, user_id, date_time, seat_number, price, is_sold
                FROM tickets WHERE id = ?
            """, mapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
