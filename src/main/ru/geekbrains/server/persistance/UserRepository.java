package ru.geekbrains.server.persistance;

import ru.geekbrains.server.User;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.ArrayList;

public class UserRepository {

    private final Connection conn;

    public UserRepository(Connection conn) throws SQLException {
        this.conn = conn;
        // создать таблицу пользователей, если она еще не создана
        PreparedStatement prepareStatement = conn.prepareStatement
                ("CREATE TABLE IF NOT EXISTS `users` (`id` int(11) NOT NULL AUTO_INCREMENT, `login` varchar(25) CHARACTER SET utf8 NOT NULL, `password` varchar(25) CHARACTER SET utf8 NOT NULL, PRIMARY KEY (`id`), UNIQUE KEY `uq_login` (`login`)) ENGINE=InnoDB DEFAULT CHARSET=utf8;");
        prepareStatement.execute();
    }

    public void insert(User user) {
        // TODO добавить нового пользователя в БД
    }

    public User findByLogin(String login) throws SQLException {
        // найти пользователя в БД по логину
        User resultUser = null;
        PreparedStatement prepareStatement = conn.prepareStatement("SELECT `id`, `login`, `password` FROM `users` WHERE login = ?");
        prepareStatement.setString(1, login);
        ResultSet resultSet = prepareStatement.executeQuery();
        if (!resultSet.wasNull()){
            resultUser = new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3));
        }
        resultSet.close();
        return resultUser;
    }

    public List<User> getAllUsers() throws SQLException {
        //  извлечь из БД полный список пользователей
        List<User> resultList = new ArrayList<>();
        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery("SELECT `id`, `login`, `password` FROM `users`");

        while (resultSet.next()) {
            resultList.add(new User(resultSet.getInt(1), resultSet.getString(2), resultSet.getString(3)));
        }
        resultSet.close();
        return resultList;
    }

//    public boolean userCheck(User user) throws SQLException {
//        int resultCount = 0;
//        PreparedStatement prepareStatement = conn.prepareStatement("select count(*) from users where login = ? and password = ?");
//        prepareStatement.setString(1, user.getLogin());
//        prepareStatement.setString(2, user.getPassword());
//        ResultSet resultSet = prepareStatement.executeQuery();
//        if (resultSet.next()) {
//            resultCount = resultSet.getInt(1);
//        }
//        resultSet.close();
//        return resultCount > 0;
//    }
}
