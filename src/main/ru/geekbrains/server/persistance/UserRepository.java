package ru.geekbrains.server.persistance;

import ru.geekbrains.server.User;

import java.sql.Connection;
import java.util.List;

public class UserRepository {

    private final Connection conn;

    public UserRepository(Connection conn) {
        this.conn = conn;
        // TODO создать таблицу пользователей, если она еще не создана
    }

    public void insert(User user) {
        // TODO добавить нового пользователя в БД
    }

    public User findByLogin(String login) {
        // TODO найти пользователя в БД по логину
        return null;
    }

    public List<User> getAllUsers() {
        // TODO извлечь из БД полный список пользователей
        return null;
    }
}
