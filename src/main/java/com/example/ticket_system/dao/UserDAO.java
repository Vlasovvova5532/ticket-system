package com.example.ticket_system.dao;

import com.example.ticket_system.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class UserDAO {
    private final JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //сохранить пользователя
    public void saveUser(User user) {
        String sql = "INSERT INTO \"users\" (login, password, full_name) VALUES (?, ?, ?)";
        jdbcTemplate.update(
                sql,
                user.getLogin(),
                user.getPassword(),
                user.getFullName());
    }

    public boolean existsByLogin(String login) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE login = ?",
                Integer.class,
                login
        );
        return count != null && count > 0;
    }
    //получение всех пользователей
    public List<User> findAll() {
        return jdbcTemplate.query(
                "SELECT id, login, password, full_name FROM users",
                (rs, rowNum) -> {
                    User user = new User();
                    user.setId(rs.getLong("id"));
                    user.setLogin(rs.getString("login"));
                    user.setPassword(rs.getString("password"));
                    user.setFullName(rs.getString("full_name"));
                    return user;
                }
        );
    }
    public Optional<User> findByLogin(String login) {
        String sql = "SELECT id, login, password, full_name FROM \"users\" WHERE login = ?";
        try {
            User user = jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {
                User u = new User();
                u.setId(rs.getLong("id"));
                u.setLogin(rs.getString("login"));
                u.setPassword(rs.getString("password"));
                u.setFullName(rs.getString("full_name"));
                return u;
            });
            return Optional.of(user);
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
}
}
