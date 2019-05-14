package ru.geekbrains.server.persistance;

import ru.geekbrains.server.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final Connection conn;

    public UserRepository(Connection conn) {
        this.conn = conn;
        try {
            Statement statement = conn.createStatement();
            statement.execute("CREATE TABLE IF NOT EXISTS users(id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,\n" +
                    "       login VARCHAR(25) UNIQUE NOT NULL,\n" +
                    "       password VARCHAR(25) NOT NULL);");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void insert(User user) {
        try {
            Statement statement = conn.createStatement();
            statement.executeQuery("INSERT INTO users (login, password) VALUES ('" + user.getLogin() + "', '"
                                    + user.getPassword() + "')");
            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public User findByLogin(String login) {
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM users WHERE login = '" + login + "'");
            User user = new User(rs.getInt(1), rs.getString(2),rs.getString(3));
            rs.close();
            statement.close();
            return user;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    //не использовал
    /*    public List<User> getAllUsers() {
        List<User> allUsers = new ArrayList<>();
        try {
            Statement statement = conn.createStatement();
            ResultSet rs = statement.executeQuery("select * from users");
            while (rs.next()) {
                allUsers.add(new User(rs.getInt(1),
                        rs.getString(2),
                        rs.getString(3)));
            }
            rs.close();
            statement.close();
            return allUsers;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }*/
}
