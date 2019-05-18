package ru.geekbrains.server.auth;

import ru.geekbrains.server.User;

import java.util.HashMap;
import java.util.Map;

public class AuthServiceImpl {

    public Map<String, String> users = new HashMap<>();

    public AuthServiceImpl() {
        users.put("ivan", "123");
        users.put("petr", "345");
        users.put("julia", "789");
    }

    public boolean authUser(User user) {
        String pwd = users.get(user.getLogin());
        return pwd != null && pwd.equals(user.getPassword());
    }
}
