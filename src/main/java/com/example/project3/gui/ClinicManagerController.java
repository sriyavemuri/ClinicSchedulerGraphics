package com.example.project3.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import com.example.project3.clinicscheduler.Location;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;


public class ClinicManagerController {

    // FXML Fields
    @FXML private DatePicker appointmentDate, followUpDate, newDatePicker;
    @FXML private TextField patientName, providerName;
    @FXML private ComboBox<String> timeslotCombo, providerCombo, newTimeComboBox;
    @FXML private ComboBox<String> appointmentComboBox;
    @FXML private RadioButton officeVisitRadio, imagingServiceRadio;
    @FXML private Button loadProvidersButton, scheduleButton, cancelButton, clearButton, rescheduleButton;
    @FXML private TextArea outputArea;
    @FXML private TableView<Location> clinicTable;
    @FXML private TableColumn<Location, String> locationColumn, countyColumn, zipColumn;
    @FXML private Label statusLabel;
    @FXML private Button clearButton1; // for the second clear button
    @FXML private Button clearRescheduleButton;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private DatePicker dobPicker;


    private ObservableList<Location> locations;

    // ObservableLists for ComboBox (example)
    private ObservableList<String> timeslots = FXCollections.observableArrayList("9:00 AM", "10:45 AM", "1:30 PM");
    private ObservableList<String> providers = FXCollections.observableArrayList("Dr. Patel", "Dr. Lim", "Dr. Harper");

    @FXML
    public void initialize() {
        // Set up ComboBoxes with initial values
        timeslotCombo.setItems(timeslots);
        providerCombo.setItems(providers);

        // Populate the TableView with Locations
        locations = FXCollections.observableArrayList(Location.values());
        clinicTable.setItems(locations);

        // Set up columns with PropertyValueFactory
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        countyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCounty()));
        zipColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getZip()));

        // Set button actions
        loadProvidersButton.setOnAction(this::handleLoadProviders);
        scheduleButton.setOnAction(this::handleSchedule);
        cancelButton.setOnAction(this::handleCancel);
        rescheduleButton.setOnAction(this::handleReschedule);
        clearButton.setOnAction(this::handleClear); // Link clear button to the handler
        clearRescheduleButton.setOnAction(this::handleClearReschedule);
    }

    // Event handler for the 'Load Providers' button
    @FXML
    private void handleLoadProviders(ActionEvent event) {
        outputArea.appendText("Providers loaded successfully.\n");
    }

    // Event handler for 'Schedule' button
    @FXML
    private void handleSchedule(ActionEvent event) {
        String patient = patientName.getText();
        String provider = providerCombo.getValue();
        String time = timeslotCombo.getValue();

        if (patient.isEmpty() || provider == null || time == null) {
            outputArea.appendText("Please fill all the required fields.\n");
        } else {
            outputArea.appendText("Scheduled appointment for " + patient + " with " + provider + " at " + time + ".\n");
        }
    }

    // Event handler for 'Cancel' button
    @FXML
    private void handleCancel(ActionEvent event) {
        // Clear appointment-specific fields only
        patientName.clear();
        providerName.clear();
        appointmentDate.setValue(null);
        followUpDate.setValue(null);
        outputArea.appendText("Appointment canceled.\n");
    }

    // Event handler for 'Reschedule' button
    @FXML
    private void handleReschedule(ActionEvent event) {
        String appointment = appointmentComboBox.getValue();
        String newDate = newDatePicker.getValue() != null ? newDatePicker.getValue().toString() : null;
        String newTime = newTimeComboBox.getValue();

        if (appointment == null || newDate == null || newTime == null) {
            statusLabel.setText("Please select a valid appointment, date, and time.");
        } else {
            statusLabel.setText("Appointment rescheduled.");
            outputArea.appendText("Appointment rescheduled to " + newDate + " at " + newTime + ".\n");
        }
    }

    // Event handler for 'Clear' button
    @FXML
    private void handleClear(ActionEvent event) {
        // Clear text fields
        patientName.clear();
        providerName.clear();
        // Clear date pickers
        appointmentDate.setValue(null);
        followUpDate.setValue(null);
        // Clear combo boxes
        timeslotCombo.getSelectionModel().clearSelection();
        providerCombo.getSelectionModel().clearSelection();
        // Clear output area and status label
        outputArea.clear();
        statusLabel.setText("");

        firstNameField.clear(); // Clear first name
        lastNameField.clear(); // Clear last name
        dobPicker.setValue(null); // Clear Date of Birth
    }

    // New handler for clearing reschedule-specific fields
    @FXML
    private void handleClearReschedule(ActionEvent event) {
        // Clear fields in the Reschedule tab
        firstNameField.clear(); // Clear first name
        lastNameField.clear(); // Clear last name
        dobPicker.setValue(null); // Clear Date of Birth
        newDatePicker.setValue(null);
        newTimeComboBox.getSelectionModel().clearSelection();
    }
}
