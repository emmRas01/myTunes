package com.example.mytunes;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application
{
    @Override
    public void start(Stage stage) throws IOException
    {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("MyTunesGUI.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1510, 855);
        stage.setTitle("MyTunes");
        stage.setScene(scene);
        stage.show();

        // Når brugeren lukker vinduet -> gemmes data i filerne
        MyTunesController controller = fxmlLoader.getController(); // Finder vores controller
        stage.setOnCloseRequest(e ->
        {
            try
            {
                controller.gemData(); // Alle objekterne gemmes
            }
            catch (IOException ex) // Hvis der sker fejl, så får brugeren besked
            {
                e.consume(); // Forhindre vinduet i at lukke, så brugeren kan se fejlmeddelelsen
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Data could not be saved");
                alert.setContentText("An error occurred while saving data");
                alert.showAndWait();
            }
        });
    }
}
