package ru.geekbrains.server.auth;

import ru.geekbrains.server.User;
import ru.geekbrains.server.persistance.UserRepository;

import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class AuthServiceJdbcImpl implements AuthService {

    private static Logger logger = Logger.getLogger(AuthServiceJdbcImpl.class.getName());

    private final UserRepository userRepository;

    public AuthServiceJdbcImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean authUser(User user) {
        try {
            User usr = userRepository.findByLogin(user.getLogin());
            return usr.getId() > 0 && usr.getPassword().equals(user.getPassword());
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Authentication exception", e);
        }
        return false;
    }
}
