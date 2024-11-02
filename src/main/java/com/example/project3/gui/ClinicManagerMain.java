package com.example.project3.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class for JavaFx Project.
 */
public class ClinicManagerMain extends Application {
    /**
     * Starting the JavaFX
     * @param primaryStage JavaFx Main stage
     * @throws Exception Error if problem
     */
    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the FXML file from the resources directory
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/project3/clinic-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        primaryStage.setTitle("Clinic Manager");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Main function to start JavaFx
     * @param args console input
     */
    public static void main(String[] args) {
        launch(args);
    }
}
