package ru.geekbrains.server;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class ChatServerRunner {

    public static void main(String[] args) {
        //ApplicationContext context = new ClassPathXmlApplicationContext("spring-config.xml");
        ApplicationContext context = new AnnotationConfigApplicationContext(Config.class);

        ChatServer chatServer = context.getBean("chatServer", ChatServer.class);
        chatServer.start(7777);
    }
}
