package main.ru.geekbrains.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.geekbrains.client.AuthException;
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

import static ru.geekbrains.client.MessagePatterns.AUTH_FAIL_RESPONSE;
import static ru.geekbrains.client.MessagePatterns.AUTH_SUCCESS_RESPONSE;

public class ChatServer {

    private AuthService authService;
    private Map<String, ClientHandler> clientHandlerMap = Collections.synchronizedMap(new HashMap<>());
    private static final Logger LOGGER = LogManager.getLogger(ChatServer.class);

    public static void main(String[] args) {
        AuthService authService;
        try {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/network_chat",
                    "root", "root");
            UserRepository userRepository = new UserRepository(conn);
            if (userRepository.getAllUsers().size() == 0) {
                userRepository.insert(new User(-1, "ivan", "123"));
                userRepository.insert(new User(-1, "petr", "345"));
                userRepository.insert(new User(-1, "julia", "789"));
            }
            authService = new AuthServiceJdbcImpl(userRepository);
        } catch (SQLException e) {
            LOGGER.error(e);

            return;
        }

        ChatServer chatServer = new ChatServer(authService);
        chatServer.start(7777);
    }

    public ChatServer(AuthService authService) {
        this.authService = authService;
    }

    private void start(int port) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server started!");
            while (true) {
                Socket socket = serverSocket.accept();
                DataInputStream inp = new DataInputStream(socket.getInputStream());
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                System.out.println("New client connected!");

                User user = null;
                try {
                    String authMessage = inp.readUTF();
                    user = checkAuthentication(authMessage);
                } catch (IOException ex) {
                    //ex.printStackTrace();

                    LOGGER.error(ex);
                } catch (AuthException ex) {
                    out.writeUTF(AUTH_FAIL_RESPONSE);
                    out.flush();
                    socket.close();

                    LOGGER.info(AUTH_FAIL_RESPONSE);
                }
                if (user != null && authService.authUser(user)) {
                    String succesMsg = "User %s authorized successful!%n", user.getLogin();
                    System.out.printf(succesMsg);
                    subscribe(user.getLogin(), socket);
                    out.writeUTF(AUTH_SUCCESS_RESPONSE);
                    out.flush();

                    LOGGER.info(succesMsg);
                } else {
                    String failMsg = AUTH_FAIL_RESPONSE;
                    if (user != null) {
                        failMsg = "Wrong authorization for user %s%n", user.getLogin();
                        System.out.printf(failMsg);
                    }
                    out.writeUTF(AUTH_FAIL_RESPONSE);
                    out.flush();
                    socket.close();

                    LOGGER.info(failMsg);
                }
            }
        } catch (IOException ex) {
            //ex.printStackTrace();

            LOGGER.error(ex);
        }
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
            String msgToUser = "User %s not connected%n", msg.getUserTo();
            System.out.printf(msgToUser);

            LOGGER.warn(msgToUser);
        }
    }

    public Set<String> getUserList() {
        return Collections.unmodifiableSet(clientHandlerMap.keySet());
    }

    public void subscribe(String login, Socket socket) throws IOException {
        // TODO Проверить, подключен ли уже пользователь. Если да, то отправить клиенту ошибку
        clientHandlerMap.put(login, new ClientHandler(login, socket, this));
        sendUserConnectedMessage(login);
    }

    public void unsubscribe(String login) {
        clientHandlerMap.remove(login);
        try {
            sendUserDisconnectedMessage(login);
        } catch (IOException e) {
            String s = "Error sending disconnect message";
            System.err.println(s);
            //e.printStackTrace();
            LOGGER.error(s);
        }
    }
}
