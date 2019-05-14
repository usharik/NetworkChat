package ru.geekbrains.server.auth;

import ru.geekbrains.server.User;
import ru.geekbrains.server.persistance.UserRepository;

public class AuthServiceJdbcImpl implements AuthService {

    private final UserRepository userRepository;

    public AuthServiceJdbcImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public boolean authUser(User user) {
        String pwd = userRepository.findByLogin(user.getLogin()).getPassword();
        return pwd != null && pwd.equals(user.getPassword());
    }

    @Override
    public boolean regUser(User user) {
        userRepository.insert(user);
        //return user.getLogin().equals(String.valueOf(userRepository.findByLogin(user.getLogin())));
        return true;
    }

}