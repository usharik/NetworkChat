package ru.geekbrains;

public class WaitNotifyDemo {

    private static volatile char currentLetter = 'A';

    public static void main(String[] args) {
        new Thread(() -> printLetter(5,'A','B')).start();
        new Thread(() -> printLetter(5,'B','C')).start();
        new Thread(() -> printLetter(5,'C','A')).start();
    }

    private synchronized static void printLetter(int n, char letter, char next) {
        for (int i = 0; i < n; i++) {
            try {
                while (currentLetter != letter) {
                    WaitNotifyDemo.class.wait();
                }
                Thread.sleep(100);
                System.out.println(letter);
                currentLetter = next;
                WaitNotifyDemo.class.notifyAll();
            } catch (InterruptedException ex) {
                ex.printStackTrace();
            }
        }
    }

}
