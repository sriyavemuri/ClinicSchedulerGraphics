module com.example.project3 {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.example.project3 to javafx.fxml;
    exports com.example.project3;
    exports com.example.project3.gui;
    opens com.example.project3.gui to javafx.fxml;
}