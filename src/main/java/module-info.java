module com.example.mytunes {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.mytunes to javafx.fxml;
    exports com.example.mytunes;
}