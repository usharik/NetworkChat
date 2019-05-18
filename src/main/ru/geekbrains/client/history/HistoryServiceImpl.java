package ru.geekbrains.client.history;

import ru.geekbrains.client.TextMessage;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

public class HistoryServiceImpl implements HistoryService {
    public static final String CURRENT_FILE_PATH = "./history_%s";
    public static final String MESSAGE_FORMAT = "%s;%s;%s;%s";

    private ArrayList<TextMessage> listMessages;
    private String login;

    public HistoryServiceImpl(String login) {
        this.listMessages = new ArrayList<>();
        this.login = login;
    }

    @Override
    public ArrayList<TextMessage> getLastMessages(int count) {
        ArrayList<TextMessage> listMessages = new ArrayList<>();
        ArrayList<TextMessage> allMessages = getAllMessages();

        if (allMessages.size() > count) {
            int maxInd = allMessages.size() - 1;
            int minInd = allMessages.size() - count;

            for (int i = minInd; i <= maxInd; i++) {
                listMessages.add(allMessages.get(i));
            }

            return listMessages;

        } else return allMessages;
    }

    private ArrayList<TextMessage> getAllMessages() {

        ArrayList<TextMessage> listMessages = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(String.format(CURRENT_FILE_PATH, login));
            Scanner scanner = new Scanner(fileReader);
            String message;
            while (scanner.hasNextLine()) {
                message = scanner.nextLine();

                listMessages.add(parseMessage(message));

            }
        } catch (FileNotFoundException e) {
            System.out.println(String.format("Нет истории для пользователя %s", login));
        }

        return listMessages;

    }

    private TextMessage parseMessage(String text) {

        String[] parts = text.split(";");

        if (parts.length == 4) {

            return new TextMessage(parts[1], parts[2], parts[3], LocalDateTime.parse(parts[0]));
        }

        System.out.println(String.format("Ошибка разбора строки %s", text));

        return null;
    }

    @Override
    public void putMessage(TextMessage textMessage) {
        try {
            FileWriter fileWriter = new FileWriter(String.format(CURRENT_FILE_PATH, login), true);
            fileWriter.write(String.format(MESSAGE_FORMAT, textMessage.getCreated(), textMessage.getUserFrom(), textMessage.getUserTo(), textMessage.getText()));
            fileWriter.write("\n");

            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
