package main.gui;

import com.sun.javafx.application.PlatformImpl;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import main.gamedata.LogReader;

import java.io.File;


public class GetDirectory extends Application {

    @Override
    public void start(final Stage primaryStage) {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("TXT files (*.txt)", "*.txt");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);
        main.gamedata.LogReader logReader = new LogReader();
        logReader.setPathTemp(file.getAbsolutePath());
        primaryStage.close();
        PlatformImpl.tkExit();
        //Platform.exit();
    }
}
