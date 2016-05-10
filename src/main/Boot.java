package main;

import main.gamedata.LogReader;
import main.util.Helper;

public class Boot {
    public Boot() {
        LogReader logReader = new LogReader();

        if (Helper.isProcessRunning("Hearthstone")) {
            while (true) {
                logReader.readHearthstoneLog();
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            System.out.println("\nStart Hearthstone before you start DeckSniffer!");
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        new Boot();
    }
}
