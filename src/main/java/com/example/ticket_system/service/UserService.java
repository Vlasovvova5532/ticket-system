package com.example.ticket_system.service;

import com.example.ticket_system.dao.UserDAO;
import com.example.ticket_system.model.Role;
import com.example.ticket_system.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import java.util.stream.Collectors;

import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserDAO userDao;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserDAO userDao) {
        this.userDao = userDao;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void registerUser(User user) {

        if (userDao.existsByLogin(user.getLogin())) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.saveUser(user);
        userDao.addRoleToUser(user.getId(), Role.BUYER);
    }

    public boolean checkPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    public void createAdminIfNotExists() {
        String adminLogin = "admin";
        String adminPassword = "admin";
        if (!userDao.existsByLogin(adminLogin)) {
            User admin = new User();
            admin.setLogin(adminLogin);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setFullName("Администратор");
            userDao.saveUser(admin);
            userDao.addRoleToUser(admin.getId(), Role.ADMIN);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userDao.findByLogin(username)
                .map(user -> org.springframework.security.core.userdetails.User
                        .withUsername(user.getLogin())
                        .password(user.getPassword()) // захешированный пароль из БД
                        .roles(user.getRoles().stream()
                                .map(Enum::name)
                                .toArray(String[]::new)) // роли конвертируем в массив строк
                        .build()
                )
                .orElseThrow(() -> new UsernameNotFoundException("Пользователь не найден: " + username));
    }

    public void assignAdminRole(Long userId) {
        if (!userDao.existsById(userId)) {
            throw new IllegalArgumentException("Пользователь с таким ID не найден");
        }
        userDao.addRoleToUser(userId, Role.ADMIN); // назначаем ADMIN
    }

    public List<User> getAllUsers() {
        return userDao.findAll();
    }

    public boolean existsByLogin(String login) {
        return userDao.existsByLogin(login);
    }
}
