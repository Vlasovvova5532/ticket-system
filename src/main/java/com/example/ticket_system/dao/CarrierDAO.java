package com.example.ticket_system.dao;

import com.example.ticket_system.model.Carrier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

@Repository
public class CarrierDAO {

    private final JdbcTemplate jdbcTemplate;

    public CarrierDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    private final RowMapper<Carrier> mapper = (rs, rowNum) -> {
        Carrier carrier = new Carrier();
        carrier.setId(rs.getLong("id"));
        carrier.setName(rs.getString("name"));
        carrier.setPhone(rs.getString("phone"));
        return carrier;
    };

    //создание перевозчика
    public long create(Carrier carrier) {
        Long id = jdbcTemplate.queryForObject(
                "INSERT INTO carriers(name, phone) VALUES(?, ?) RETURNING id",
                Long.class,
                carrier.getName(),
                carrier.getPhone()
        );
        if (id == null) {
            throw new IllegalStateException("Не удалось получить ID перевозчика");
        }
        return id;
    }

    //найти всех перевозчиков
    public List<Carrier> findAll() {
        return jdbcTemplate.query("SELECT id, name, phone FROM carriers ORDER BY id", mapper);
    }

    public Carrier findById(long id) {
        try {
            return jdbcTemplate.queryForObject("SELECT id, name, phone FROM carriers WHERE id = ?", mapper, id);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public int countRoutesByCarrier(long carrierId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM routes WHERE carrier_id = ?",
                Integer.class,
                carrierId
        );
        return count == null ? 0 : count;
    }

    public boolean existsById(long id) {
        Integer c = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM carriers WHERE id = ?", Integer.class, id);
        return c != null && c > 0;
    }

    public int update(long id, Carrier carrier) {
        return jdbcTemplate.update(
                "UPDATE carriers SET name = ?, phone = ? WHERE id = ?",
                carrier.getName(), carrier.getPhone(), id
        );
    }

    public int delete(long id) {
        return jdbcTemplate.update("DELETE FROM carriers WHERE id = ?", id);
    }
}
