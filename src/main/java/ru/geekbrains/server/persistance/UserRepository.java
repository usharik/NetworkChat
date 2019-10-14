package ru.geekbrains.server.persistance;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.geekbrains.server.User;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Component("userRepository")
public class UserRepository {

    private final Connection conn;

    public UserRepository(Connection conn) throws SQLException {
        this.conn = conn;
        createTableIfNotExists(conn);
    }

    @Autowired
    public UserRepository(DataSource dataSource) throws SQLException {
        this(dataSource.getConnection());
    }

    public void initData() throws SQLException {
        if (this.getAllUsers().size() == 0) {
            this.insert(new User(-1, "ivan", "123"));
            this.insert(new User(-1, "petr", "345"));
            this.insert(new User(-1, "julia", "789"));
        }
    }

    public void insert(User user) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "insert into users(login, password) values (?, ?);")) {
            stmt.setString(1, user.getLogin());
            stmt.setString(2, user.getPassword());
            stmt.execute();
        }
    }

    public User findByLogin(String login) throws SQLException {
        try (PreparedStatement stmt = conn.prepareStatement(
                "select id, login, password from users where login = ?")) {
            stmt.setString(1, login);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new User(rs.getInt(1), rs.getString(2), rs.getString(3));
            }
        }
        return new User(-1, "", "");
    }

    public List<User> getAllUsers() throws SQLException {
        List<User> res = new ArrayList<>();
        try (Statement stmt = conn.createStatement()) {
            ResultSet rs = stmt.executeQuery("select id, login, password from users");

            while (rs.next()) {
                res.add(new User(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }
        }
        return res;
    }

    private void createTableIfNotExists(Connection conn) throws SQLException {
        try (Statement stmt = conn.createStatement()) {
            stmt.execute("create table if not exists users (\n" +
                    "\tid int auto_increment primary key,\n" +
                    "    login varchar(25),\n" +
                    "    password varchar(25),\n" +
                    "    unique index uq_login(login)\n" +
                    ");");
        }
    }
}
