package ru.geekbrains.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;

@Configuration
@ComponentScan("ru.geekbrains.server")
public class Config {

    @Bean
    public DataSource dataSource() {
        DriverManagerDataSource ds = new DriverManagerDataSource("jdbc:mysql://localhost:3306/network_chat", "root", "root");
        ds.setDriverClassName("com.mysql.jdbc.Driver");
        return ds;
    }
}
