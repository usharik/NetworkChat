package ru.geekbrains.server.auth;

import ru.geekbrains.server.User;
import ru.geekbrains.server.persistance.UserRepository;

import java.sql.SQLException;

public class AuthServiceJdbcImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceJdbcImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean authUser(User user) {
        //авторизовать пользователя используя userRepository
        boolean result = false;
        try {
            result = userRepository.checkAuth(user);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public boolean regNewUser(User user) {
        boolean result = false;
        try {
            userRepository.insert(user);
            result = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return result;
    }
}
