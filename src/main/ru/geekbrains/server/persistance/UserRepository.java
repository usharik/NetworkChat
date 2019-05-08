package ru.geekbrains.server.persistance;

import ru.geekbrains.server.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private final Connection conn;

    public UserRepository(Connection conn) throws SQLException {
        this.conn = conn;
        //создать таблицу пользователей, если она еще не создана
//        PreparedStatement prepareStatement = conn.prepareStatement
//                ("create table if not exists users ( id int auto_increment primary key, login varchar(25), password varchar(25), unique index uq_login(login))");
//        prepareStatement.execute();
    }

    public void insert(User user) throws SQLException {
        //добавить нового пользователя в БД
        PreparedStatement prepareStatement = conn.prepareStatement("insert into users(login, password) values (?, ?)");
        prepareStatement.setString(1, user.getLogin());
        prepareStatement.setString(2, user.getPassword());
        prepareStatement.execute();
        prepareStatement.close();
    }

    public User findByLogin(String login) throws SQLException {
        // найти пользователя в БД по логину
        PreparedStatement prepareStatement = conn.prepareStatement("select * from users where login = ?");
        prepareStatement.setString(1, login);
        User resultUser = null;
        ResultSet resultSet = prepareStatement.executeQuery();
        if (!resultSet.wasNull()) {
            resultUser = new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
        }
        resultSet.close();
        return resultUser;
    }

    public List<User> getAllUsers() throws SQLException {
        // извлечь из БД полный список пользователей
        List<User> resultList = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery("select * from users");

        while (resultSet.next()) {
            resultList.add(new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)));
        }
        resultSet.close(); // Или использовать try-with-resources
        return resultList;
    }

    public boolean checkAuth(User user) throws SQLException {
        int resultCount = 0;
        PreparedStatement prepareStatement = conn.prepareStatement("select count(*) from users where login = ? and password = ?");
        prepareStatement.setString(1, user.getLogin());
        prepareStatement.setString(2, user.getPassword());
        ResultSet resultSet = prepareStatement.executeQuery();
        if (resultSet.next()) {
            resultCount = resultSet.getInt(1);
        }
        resultSet.close();
        return resultCount > 0;
    }
}
