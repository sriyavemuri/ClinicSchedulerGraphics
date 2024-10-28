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
import com.example.project3.clinicscheduler.Timeslot;
import javafx.stage.FileChooser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

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

    private ObservableList<String> providers = FXCollections.observableArrayList("Dr. Patel", "Dr. Lim", "Dr. Harper");

    @FXML
    public void initialize() {
        populateTimeslotComboBoxes();

        // Initialize other ComboBox items and setup the TableView as before
        String[] providers = {"Dr. Patel", "Dr. Lim", "Dr. Harper"};
        providerCombo.setItems(FXCollections.observableArrayList(providers));

        locations = FXCollections.observableArrayList(Location.values());
        clinicTable.setItems(locations);

        // Setup columns for the clinic table
        locationColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLocation()));
        countyColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getCounty()));
        zipColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getZip()));

        // Set button actions
        loadProvidersButton.setOnAction(this::handleLoadProviders);
        scheduleButton.setOnAction(this::handleSchedule);
        cancelButton.setOnAction(this::handleCancel);
        rescheduleButton.setOnAction(this::handleReschedule);
        clearButton.setOnAction(this::handleClear);
        clearRescheduleButton.setOnAction(this::handleClearReschedule);
    }


    @FXML
    private void handleLoadProviders(ActionEvent event) {
        // Create a FileChooser
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Provider File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        // Show the open file dialog
        File selectedFile = fileChooser.showOpenDialog(loadProvidersButton.getScene().getWindow());

        if (selectedFile != null) {
            // Read the file and load provider names
            loadProvidersFromFile(selectedFile);
        } else {
            outputArea.appendText("No file selected.\n");
        }
    }

    // Helper method to read provider names from the file and update the ComboBox
    private void loadProvidersFromFile(File file) {
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            ObservableList<String> providerList = FXCollections.observableArrayList();

            while ((line = br.readLine()) != null) {
                providerList.add(line.trim()); // Add each provider name to the list
            }

            providerCombo.setItems(providerList); // Update the ComboBox with the loaded provider names
            outputArea.appendText("Providers loaded successfully.\n");
        } catch (IOException e) {
            outputArea.appendText("Error reading the provider file: " + e.getMessage() + "\n");
        }
    }

    private void populateTimeslotComboBoxes() {
        // Generate available timeslots and populate ComboBoxes
        Timeslot[] availableSlots = generateAvailableSlots();
        String[] timeslotStrings = new String[availableSlots.length];
        for (int i = 0; i < availableSlots.length; i++) {
            timeslotStrings[i] = availableSlots[i].toString();
        }

        // Set items for timeslotCombo and newTimeComboBox
        timeslotCombo.setItems(FXCollections.observableArrayList(timeslotStrings));
        newTimeComboBox.setItems(FXCollections.observableArrayList(timeslotStrings));
    }


    // Method to generate available time slots
    private Timeslot[] generateAvailableSlots() {
        Timeslot[] allSlots = {
                new Timeslot(9, 0),   // 9:00 AM
                new Timeslot(9, 30),  // 9:30 AM
                new Timeslot(10, 0),  // 10:00 AM
                new Timeslot(10, 30), // 10:30 AM
                new Timeslot(11, 0),  // 11:00 AM
                new Timeslot(11, 30), // 11:30 AM
                new Timeslot(14, 0),  // 2:00 PM
                new Timeslot(14, 30), // 2:30 PM
                new Timeslot(15, 0),  // 3:00 PM
                new Timeslot(15, 30), // 3:30 PM
                new Timeslot(16, 0),  // 4:00 PM
                new Timeslot(16, 30)  // 4:30 PM
        };

        // Filter out occupied slots - Assume you have a method to check availability
        return filterAvailableSlots(allSlots);
    }

    // Method to filter available slots (replace this with your actual logic)
    private Timeslot[] filterAvailableSlots(Timeslot[] allSlots) {
        // Assuming you have some way to check if a slot is available
        // Here you should implement your own logic to filter the occupied slots
        // For the sake of this example, returning all slots
        return allSlots; // Modify this based on actual availability logic
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
