package ru.geekbrains.server;

import java.sql.*;

public class JdbcDemo {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//         Class.forName("com.mysql.jdbc.Driver");

        // Для SQLite
        // Connection conn = DriverManager.getConnection("jdbc:sqlite:mydatabase.db");

        // Для MySQL
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/network_chat?characterEncoding=utf8",
                "chatserver", "chatserver");

//        PreparedStatement prepareStatement = conn.prepareStatement("insert into users(login, password) values (?, ?)");
//        prepareStatement.setString(1, "max");
//        prepareStatement.setString(2, "789");
//        prepareStatement.execute();

        Statement stmt = conn.createStatement();
        ResultSet resultSet = stmt.executeQuery("select * from users");

        while (resultSet.next()) {
            System.out.printf("%d\t%s\t%s%n",
                    resultSet.getInt(1),
                    resultSet.getString(2),
                    resultSet.getString(3));
        }
        resultSet.close(); // Или использовать try-with-resources

        // Несколько комманд в одной транзакции
//        conn.setAutoCommit(false);
//
//        prepareStatement.setString(1, "max");
//        prepareStatement.setString(2, "789");
//        prepareStatement.execute();
//
//        prepareStatement.setString(1, "max1");
//        prepareStatement.setString(2, "789");
//        prepareStatement.execute();
//
//        prepareStatement.setString(1, "max2");
//        prepareStatement.setString(2, "789");
//        prepareStatement.execute();
//
//        conn.commit();
//        conn.setAutoCommit(true);

        PreparedStatement prepareStatement = conn.prepareStatement("select count(*) from users where login = ? and password = ?");
        prepareStatement.setString(1, "ivan");
        prepareStatement.setString(2, "123");
        resultSet = prepareStatement.executeQuery();
        resultSet.next();
        System.out.println(resultSet.getInt(1));
        resultSet.close();
    }
}
