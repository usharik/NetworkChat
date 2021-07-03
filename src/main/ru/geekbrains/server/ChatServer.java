package ru.geekbrains.server;

import ru.geekbrains.client.AuthException;
import ru.geekbrains.client.RegException;
import ru.geekbrains.client.TextMessage;
import ru.geekbrains.server.auth.AuthService;
import ru.geekbrains.server.auth.AuthServiceJdbcImpl;
import ru.geekbrains.server.persistance.UserRepository;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static ru.geekbrains.client.MessagePatterns.*;

public class ChatServer {

    private AuthService authService;
    private Map<String, ru.geekbrains.server.ClientHandler> clientHandlerMap = Collections.synchronizedMap(new HashMap<>());
    private ExecutorService executorService;

    public static void main(String[] args) {
        AuthService authService;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/network_chat?characterEncoding=utf8",
                    "chatserver", "chatserver");
            UserRepository userRepository = new UserRepository(conn);
            if (userRepository.getAllUsers().size() == 0) {
                userRepository.insert(new User(-1, "ivan", "123"));
                userRepository.insert(new User(-1, "petr", "345"));
                userRepository.insert(new User(-1, "julia", "789"));
            }
            authService = new AuthServiceJdbcImpl(userRepository);
        } catch (SQLException e) {
            e.printStackTrace();
            return;
        }

        ChatServer chatServer = new ChatServer(authService);
        chatServer.start(7777);
    }

    public ChatServer(AuthService authService) {

        this.authService = authService;
        this.executorService = Executors.newFixedThreadPool(2);
    }

    private void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started!");
            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream inp = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                System.out.println("New client connected!");

                ru.geekbrains.server.User user = null;
                try {
                    String authMessage = inp.readUTF();
                    if (!isRegistration(authMessage)) {
                        user = checkAuthentication(authMessage);
                    }else {
                        out.writeUTF(REG_SUCCESS_RESPONSE);
                        out.flush();
                        continue;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (AuthException ex) {
                    out.writeUTF(AUTH_FAIL_RESPONSE);
                    out.flush();
                    socket.close();
                } catch (RegException e) {
                    out.writeUTF(REG_FAIL_RESPONSE);
                    out.flush();

                }
                if (user != null && authService.authUser(user)) {
                    System.out.printf("User %s authorized successful!%n", user.getLogin());
                    subscribe(user.getLogin(), socket);
                    out.writeUTF(AUTH_SUCCESS_RESPONSE);
                    out.flush();
                } else {
                    if (user != null) {
                        System.out.printf("Wrong authorization for user %s%n", user.getLogin());
                    }
                    out.writeUTF(AUTH_FAIL_RESPONSE);
                    out.flush();
                    socket.close();
                }
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * if message code = REG_TAG return TRUE (doesn't matter particular result of registration)
     */
    private boolean isRegistration(String authMessage) throws RegException {
        String[] regParts = authMessage.split(" ");
        boolean result = false;
        if (regParts[0].equals(REG_TAG)) {
            result = true;
            //do registration
            //if return RegException throw
            if (!authService.regNewUser(new User(-1,regParts[1],regParts[2]))){
                throw new RegException();
            }
            if (regParts.length != 3) {
                System.out.printf("Incorrect registration message %s%n", authMessage);
                throw new RegException();
            }
        }
        return result;
    }

    private User checkAuthentication(String authMessage) throws AuthException {
        String[] authParts = authMessage.split(" ");
        if (authParts.length != 3 || !authParts[0].equals("/auth")) {
            System.out.printf("Incorrect authorization message %s%n", authMessage);
            throw new AuthException();
        }
        return new User(-1, authParts[1], authParts[2]);
    }

    private void sendUserConnectedMessage(String login) throws IOException {
        for (ClientHandler clientHandler : clientHandlerMap.values()) {
            if (!clientHandler.getLogin().equals(login)) {
                System.out.printf("Sending connect notification to %s about %s%n", clientHandler.getLogin(), login);
                clientHandler.sendConnectedMessage(login);
            }
        }
    }

    private void sendUserDisconnectedMessage(String login) throws IOException {
        for (ClientHandler clientHandler : clientHandlerMap.values()) {
            if (!clientHandler.getLogin().equals(login)) {
                System.out.printf("Sending disconnect notification to %s about %s%n", clientHandler.getLogin(), login);
                clientHandler.sendDisconnectedMessage(login);
            }
        }
    }

    public void sendMessage(TextMessage msg) throws IOException {
        ClientHandler userToClientHandler = clientHandlerMap.get(msg.getUserTo());
        if (userToClientHandler != null) {
            userToClientHandler.sendMessage(msg.getUserFrom(), msg.getText());
        } else {
            System.out.printf("User %s not connected%n", msg.getUserTo());
        }
    }

    public Set<String> getUserList() {
        return Collections.unmodifiableSet(clientHandlerMap.keySet());
    }

    public void subscribe(String login, Socket socket) throws IOException {
        // TODO Проверить, подключен ли уже пользователь. Если да, то отправить клиенту ошибку
        clientHandlerMap.put(login, new ClientHandler(login, socket, executorService, this));
        sendUserConnectedMessage(login);
    }

    public void unsubscribe(String login) {
        clientHandlerMap.remove(login);
        try {
            sendUserDisconnectedMessage(login);
        } catch (IOException e) {
            System.err.println("Error sending disconnect message");
            e.printStackTrace();
        }
    }
}
