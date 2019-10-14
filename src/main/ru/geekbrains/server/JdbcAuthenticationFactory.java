package ru.geekbrains.server;

import ru.geekbrains.server.auth.AuthService;
import ru.geekbrains.server.auth.AuthServiceJdbcImpl;
import ru.geekbrains.server.persistance.UserRepository;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class JdbcAuthenticationFactory {

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection("jdbc:mysql://localhost:3306/network_chat",
                "root", "root");
    }

    public static UserRepository getUserRepository(Connection conn) throws SQLException {
        return new UserRepository(conn);
    }

    public static AuthService getAuthService(UserRepository userRepository) {
        return new AuthServiceJdbcImpl(userRepository);
    }
}
