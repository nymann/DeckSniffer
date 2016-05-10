package main.gui;

import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;


public class GetDirectory extends Application {
    public static String pathToLogFile = "C:/Program Files (x86)/Hearthstone/Hearthstone_Data/output_log.txt";

    @Override
    public void start(final Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);
        pathToLogFile = file.getAbsolutePath();

        primaryStage.setOpacity(0);
        primaryStage.show();
        primaryStage.close();
    }
}
