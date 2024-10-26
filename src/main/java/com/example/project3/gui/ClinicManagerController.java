package com.example.project3.gui;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.net.URL;
import java.util.ResourceBundle;

public class ClinicManagerController implements Initializable {

    @FXML
    private DatePicker appointmentDate, followUpDate;

    @FXML
    private TextField patientName, providerName;

    @FXML
    private RadioButton officeVisitRadio, imagingServiceRadio;

    @FXML
    private ComboBox<String> timeslotCombo, providerCombo;

    @FXML
    private Button scheduleButton, cancelButton, clearButton;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        providerCombo.getItems().addAll("Provider 1", "Provider 2");
        timeslotCombo.getItems().addAll("9:00 AM", "10:45 AM", "1:30 PM");
    }
}
