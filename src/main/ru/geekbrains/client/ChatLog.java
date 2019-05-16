package ru.geekbrains.client;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class ChatLog {
    private Path path;
    private File historyFile;


    public ChatLog(String userName) {
        String logFile = String.format("./src/main/ru/geekbrains/logs/%s.txt", userName);
        this.historyFile = new File(logFile);
        this.path = Paths.get(logFile);
    }

    public ArrayList<String> getTail(int rows) throws IOException {
        ArrayList<String> history = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(historyFile))) {
            long lineCount = Files.lines(path).count();
            long delta = rows == 0 ? lineCount : rows; //if 0 then all
            delta = (lineCount - delta) < 0 ? 0 : lineCount - delta;
            long count = 0;
            while (reader.ready()) {
                count++;
                String tmp = reader.readLine();
                if (count > delta) {
                    history.add(tmp);
                }
            }
        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            it will crate a new one
        }
        return history;
    }

    public String tailAsString(int rows) throws IOException {
        StringBuilder sb = new StringBuilder();
        ArrayList<String> tail = getTail(rows);
        for (String s : tail) {
            sb.append(s).append("\n");
        }
        return sb.toString();
    }

    private void writeToFile(String s) throws IOException {
        try (PrintWriter writer = new PrintWriter(new BufferedWriter(new FileWriter(historyFile, true)))) {
            writer.println(s);
        }
    }

    public void writeHistory(String s) throws IOException {
        writeToFile(s);
    }
}
