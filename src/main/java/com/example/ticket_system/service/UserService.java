package com.example.ticket_system.service;

import com.example.ticket_system.dao.UserDAO;
import com.example.ticket_system.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserDAO userDao;
    //private final PasswordEncoder passwordEncoder;

    public UserService(UserDAO userDao) {
        this.userDao = userDao;
        //this.passwordEncoder = new BCryptPasswordEncoder();
    }

    public void registerUser(User user) {
//        if(userDao.findByLogin(user.getLogin()).isPresent()) {
//            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
//        }
        if (userDao.existsByLogin(user.getLogin())) {
            throw new IllegalArgumentException("Пользователь с таким логином уже существует");
        }

        user.setLogin(user.getLogin());
        //user.setPassword(passwordEncoder.encode(user.getPassword())); // хэшируем пароль
        user.setFullName(user.getFullName());

        userDao.saveUser(user);
    }
    public List<User> getAllUsers() {
        return userDao.findAll();
    }
}
