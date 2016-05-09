package main.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Helper {
    public Helper() {

    }

    private Boolean isProcessRunning(String exeName) {
        try {
            String line;
            String pidInfo = "";
            Process process = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
            BufferedReader input = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((line = input.readLine()) != null) {
                pidInfo += line;
            }
            input.close();

            return pidInfo.contains(exeName);

        } catch (IOException e1) {
            e1.printStackTrace();
        }

        return false;
    }

    public static void clearTerminal() {
        try {
            if (System.getProperty("os.name").contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            }
            else {
                System.out.println("Your OS is not windows, clearing the terminal by printing 50 empty new lines.");
                for (int i = 0; i < 50; i++) {
                    System.out.println();
                }
            }
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }
    }
}
