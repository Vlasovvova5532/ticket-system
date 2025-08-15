package com.example.ticket_system.dao;

import com.example.ticket_system.model.Route;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class RouteDAO {

    private final JdbcTemplate jdbcTemplate;

    public RouteDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Route> mapper = (rs, rowNum) -> {
        Route route = new Route();
        route.setId(rs.getLong("id"));
        route.setDeparture(rs.getString("departure"));
        route.setDestination(rs.getString("destination"));
        route.setCarrierId(rs.getLong("carrier_id"));
        route.setDurationMinutes(rs.getInt("duration_minutes"));
        return route;
    };

    public long createRoute(Route route) {
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO routes(departure, destination, duration_minutes, carrier_id) " +
                        "VALUES (?, ?, ?, ?) RETURNING id",
                Long.class,
                route.getDeparture(),
                route.getDestination(),
                route.getDurationMinutes(),
                route.getCarrierId()
        );

        if (id == null) {
            throw new IllegalStateException("Не удалось получить ID маршрута");
        }
        return id;
    }

    public List<Route> findAll() {
        return jdbcTemplate.query("""
            SELECT id, departure, destination, carrier_id, duration_minutes
            FROM routes ORDER BY id
        """, mapper);
    }

    public Route findById(long id) {
        try {
            return jdbcTemplate.queryForObject("""
                SELECT id, departure, destination, carrier_id, duration_minutes
                FROM routes WHERE id = ?
            """, mapper, id);
        }
        catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int countByCarrier(long carrierId) {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM routes WHERE carrier_id = ?", Integer.class, carrierId);
    }
}
