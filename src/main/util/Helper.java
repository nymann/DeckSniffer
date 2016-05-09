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
}
