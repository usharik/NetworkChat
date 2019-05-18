package ru.geekbrains.server.persistance;

import ru.geekbrains.server.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final Connection conn;

    public UserRepository(Connection conn) throws SQLException {
        this.conn = conn;
        // TODO создать таблицу пользователей, если она еще не создана

        try {

            Statement stmt = conn.createStatement();
            stmt.executeUpdate("CREATE TABLE IF NOT EXISTS \"users\" (\n" +
                    "\t\"id\"\tINTEGER NOT NULL PRIMARY KEY AUTOINCREMENT UNIQUE,\n" +
                    "\t\"login\"\tTEXT NOT NULL UNIQUE,\n" +
                    "\t\"password\"\tTEXT NOT NULL DEFAULT ''\n" +
                    ")");

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    public void insert(User user) {
        // TODO добавить нового пользователя в БД

        Statement updateStmt = null;
        try {
            updateStmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();

            return;
        }

        try {
            PreparedStatement insertStatement = conn.prepareStatement("INSERT INTO users (login, password) VALUES (?, ?)");
            insertStatement.setString(1, user.getLogin());
            insertStatement.setString(2, user.getPassword());
            int result = insertStatement.executeUpdate();

            if (result == 1) {
                System.out.println("Пользователель добавлен.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public boolean authByLoginPassword(String login, String password) {
        // TODO найти пользователя в БД по логину
        // DONE

        // Сделал простенькую авторизацию по логину-паролю.

        boolean result = false;

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        try {
            PreparedStatement insertStatement = conn.prepareStatement("SELECT login FROM users WHERE login = ? AND password = ?");
            insertStatement.setString(1, login);
            insertStatement.setString(2, password);
            ResultSet rs = insertStatement.executeQuery();


            while (rs.next()) {
                result = true;
            }

            return result;

        } catch (SQLException e) {
            e.printStackTrace();

            return false;
        }
    }

    public List<User> getAllUsers() {
        // TODO извлечь из БД полный список пользователей
        // DONE

        // Не совсем понял зачем извлекать весь список пользователей, а если их 1 млрд? ...

        List<User> allUsers = new ArrayList();

        Statement stmt = null;
        try {
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        ResultSet rs = null;
        try {
            rs = stmt.executeQuery("SELECT id, login, password FROM users");

            while (rs.next()) {
                allUsers.add(new User(rs.getInt(1), rs.getString(2), rs.getString(3)));
            }

            return allUsers;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
