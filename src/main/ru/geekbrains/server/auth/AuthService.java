package ru.geekbrains.server.auth;

import ru.geekbrains.server.User;

public interface AuthService {

    boolean authUser(User user);

    boolean regUser(User user);
}
