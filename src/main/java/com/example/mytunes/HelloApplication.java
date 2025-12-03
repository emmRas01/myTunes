package com.example.mytunes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MyTunesGUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1400, 900);
        stage.setTitle("MyTunes");
        stage.setScene(scene);
        stage.show();

        //Når brugeren lukker vinduet på kryds, så gemmes data i filen
        MyTunesController controller = fxmlLoader.getController(); //finder vores controller
        stage.setOnCloseRequest(e -> {
            try {
                controller.gemData(); //alle objekterne gemmes i filerne
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }
}
