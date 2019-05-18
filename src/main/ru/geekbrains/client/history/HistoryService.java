package ru.geekbrains.client.history;

import ru.geekbrains.client.TextMessage;
import ru.geekbrains.server.User;

import java.util.ArrayList;

public interface HistoryService {

    ArrayList<TextMessage> getLastMessages(int count);

    void putMessage(TextMessage textMessage);
}
