package main;

import main.gamedata.LogReader;

public class Boot {
    public Boot() {
        LogReader logReader = new LogReader();

        while (true) {
            logReader.readHearthstoneLog();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        new Boot();
    }
}
