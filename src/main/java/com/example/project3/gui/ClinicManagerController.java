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
    @FXML private TextField patientFirstName;
    @FXML private TextField patientLastName;
    @FXML private DatePicker dobPicker;
    @FXML private DatePicker dobPickerReschedule; // For rescheduling


    private ObservableList<Location> locations;


    // ObservableLists for ComboBox (example)

    private ObservableList<String> providers = FXCollections.observableArrayList("Dr. Patel", "Dr. Lim", "Dr. Harper");

    @FXML
    public void initialize() {
        populateTimeslotComboBoxes();

        // Initialize provider list
        String[] providers = {"Dr. Patel", "Dr. Lim", "Dr. Harper"};
        providerCombo.setItems(FXCollections.observableArrayList(providers));

        // Initialize locations for the clinic table
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

        // Disable the provider dropdown initially
        providerCombo.setDisable(true);
    }

    // Event handler for the 'Load Providers' button
    @FXML
    private void handleLoadProviders(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Provider File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));

        File selectedFile = fileChooser.showOpenDialog(loadProvidersButton.getScene().getWindow());

        if (selectedFile != null) {
            ObservableList<String> providerNames = readProviderFile(selectedFile);
            if (providerNames != null) {
                providerCombo.setItems(providerNames); // Populate the dropdown
                providerCombo.setDisable(false); // Enable the dropdown
                outputArea.appendText("Providers loaded successfully.\n");
            } else {
                outputArea.appendText("Failed to load providers from the file.\n");
            }
        } else {
            outputArea.appendText("No file selected.\n");
        }
    }

    // Helper method to read the provider file and return an ObservableList of providers
    private ObservableList<String> readProviderFile(File file) {
        ObservableList<String> providers = FXCollections.observableArrayList();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    providers.add(line.trim()); // Add non-empty lines as provider names
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return providers;
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

    // Event handler for 'Clear' button in Schedule/Cancel tab
    @FXML
    private void handleClear(ActionEvent event) {
        // Clear text fields
        patientFirstName.clear();
        patientLastName.clear();
        // Clear date pickers
        appointmentDate.setValue(null);
        dobPicker.setValue(null);
        // Clear combo boxes
        timeslotCombo.getSelectionModel().clearSelection();
        providerCombo.getSelectionModel().clearSelection();
    }

    // Event handler for 'Clear' button in Reschedule tab
    @FXML
    private void handleClearReschedule(ActionEvent event) {
        // Clear text fields
        firstNameField.clear();
        lastNameField.clear();
        // Clear date pickers
        dobPickerReschedule.setValue(null); // Use different ID for the reschedule DOB picker
        newDatePicker.setValue(null);
        // Clear combo box
        newTimeComboBox.getSelectionModel().clearSelection();
    }


    // Event handler for scheduling an appointment
    @FXML
    private void handleScheduleAction(ActionEvent event) {
        outputArea.appendText("Navigated to Schedule Appointment.\n");
    }

    // Event handler for rescheduling an appointment
    @FXML
    private void handleRescheduleAction(ActionEvent event) {
        outputArea.appendText("Navigated to Reschedule Appointment.\n");
    }

    // Event handler for canceling an appointment
    @FXML
    private void handleCancelAction(ActionEvent event) {
        outputArea.appendText("Navigated to Cancel Appointment.\n");
    }

    // Event handler for listing appointments by date, time, or provider
    @FXML
    private void handleListByDateTime(ActionEvent event) {
        outputArea.appendText("Listing appointments by Date/Time/Provider.\n");
    }

    // Event handler for listing appointments by location
    @FXML
    private void handleListByLocation(ActionEvent event) {
        outputArea.appendText("Listing appointments by Location.\n");
    }

    // Event handler for listing appointments by patient
    @FXML
    private void handleListByPatient(ActionEvent event) {
        outputArea.appendText("Listing appointments by Patient.\n");
    }

    // Event handler for listing office visits only
    @FXML
    private void handleListOfficeVisits(ActionEvent event) {
        outputArea.appendText("Listing Office Visits Only.\n");
    }

    // Event handler for listing imaging visits only
    @FXML
    private void handleListImagingVisits(ActionEvent event) {
        outputArea.appendText("Listing Imaging Visits Only.\n");
    }

    // Event handler for generating a patient statement
    @FXML
    private void handlePatientStatement(ActionEvent event) {
        outputArea.appendText("Generating Patient Statement.\n");
    }

    // Event handler for generating a provider statement
    @FXML
    private void handleProviderStatement(ActionEvent event) {
        outputArea.appendText("Generating Provider Statement.\n");
    }

}
