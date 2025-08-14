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
        String sql = "INSERT INTO routes(departure, destination, carrier_id, duration_minutes) VALUES(?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(con -> {
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, route.getDeparture());
            ps.setString(2, route.getDestination());
            ps.setLong(3, route.getCarrierId());
            ps.setInt(4, route.getDurationMinutes());
            return ps;
        }, keyHolder);
        return keyHolder.getKey().longValue();
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
