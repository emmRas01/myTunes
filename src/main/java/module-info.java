module com.example.mytunes {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.base;
    requires javafx.graphics;


    opens com.example.mytunes to javafx.fxml;
    exports com.example.mytunes;
}