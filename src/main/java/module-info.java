module com.example.project3 {
    requires javafx.controls;
    requires javafx.fxml;

    exports com.example.project3.gui;
    opens com.example.project3.gui to javafx.fxml;

    exports com.example.project3.clinicscheduler;
    exports com.example.project3.util;
}