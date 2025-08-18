package com.example.ticket_system.dao;

import com.example.ticket_system.model.Role;
import com.example.ticket_system.model.User;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class UserDAO {
    private final JdbcTemplate jdbcTemplate;

    public UserDAO(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    //сохранить пользователя
    public void saveUser(User user) {
        String sql = "INSERT INTO users (login, password, full_name) VALUES (?, ?, ?) RETURNING id";
        Long id = jdbcTemplate.queryForObject(
                sql,
                Long.class,
                user.getLogin(),
                user.getPassword(),
                user.getFullName()
        );
        user.setId(id);
    }

    public void addRoleToUser(Long userId, Role role) {
        Long roleId = jdbcTemplate.queryForObject(
                "SELECT id FROM roles WHERE name = ?",
                Long.class,
                role.name()
        );
        jdbcTemplate.update(
                "INSERT INTO user_roles(user_id, role_id) VALUES (?, ?) ON CONFLICT DO NOTHING",
                userId, roleId
        );
    }

    public Set<Role> getUserRoles(long userId) {
        List<String> roleNames = jdbcTemplate.queryForList(
                "SELECT r.name FROM roles r " +
                        "JOIN user_roles ur ON r.id = ur.role_id " +
                        "WHERE ur.user_id = ?",
                String.class,
                userId
        );

        Set<Role> role = new HashSet<>();
        for(String roleName : roleNames) {
            role.add(Role.valueOf(roleName)); // конвертация строки в enum
        }
        return role;
    }

    public boolean existsById(long userId) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM users WHERE id = ?",
                Integer.class,
                userId
        );
        return count != null && count > 0;
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
                u.setRoles(getUserRoles(u.getId()));
                return u;
            }, login);
            return Optional.of(user);
        }
        catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
}
}
