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
    public boolean authUser(User user) throws SQLException {
        // авторизовать пользователя используя userRepository
        String pwd = userRepository.findByLogin(user.getLogin()).getPassword();
        return pwd != null && pwd.equals(user.getPassword());

    }
}
