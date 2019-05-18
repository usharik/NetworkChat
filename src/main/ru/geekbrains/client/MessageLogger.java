package ru.geekbrains.client;

import java.io.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import static ru.geekbrains.client.MessagePatterns.TIME_FORMATTER;

public class MessageLogger {

    private final File file;
    private PrintWriter logger;
    private List<String> messageList = new ArrayList<>();

    public MessageLogger (String login) throws IOException {
        file = new File(login + "_log.txt");
        if (!file.exists()) file.createNewFile();
    }

    public synchronized void addToLog (TextMessage message) throws FileNotFoundException {
        logger = new PrintWriter(new BufferedOutputStream(new FileOutputStream(file, true)));
        String msg = String.format("%s\t%s\t%s\t%s", message.getUserFrom(), message.getUserTo(),
                                                    message.getText(), message.getCreated());
        logger.println(msg);
        logger.flush();
    }

    public List<String> loadLog(){
        try (BufferedReader reader = new BufferedReader(new FileReader(file))){
            while (reader.ready()) messageList.add(reader.readLine());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return messageList;
    }

    public List<TextMessage> log(){
        loadLog();
        int amt = 100;
        List<TextMessage> log = new ArrayList<>();
        if (messageList.size()> amt){
            messageList = messageList.subList(messageList.size() - amt, messageList.size());
        }
        for (String string : messageList) {
            String[] split = string.split("\t", 4);
            log.add(new TextMessage(split[0], split[1], split[2], LocalDateTime.parse(split[3], TIME_FORMATTER)));
        }
        return log;
    }
}
