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
import com.example.project3.util.Date;
import javafx.stage.FileChooser;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import javafx.scene.control.TabPane;

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
    @FXML private TabPane tabPane; // Declare the TabPane variable


    private ObservableList<Location> locations;


    // ObservableLists for ComboBox (example)

    private ObservableList<String> providers = FXCollections.observableArrayList("Dr. Patel", "Dr. Lim", "Dr. Harper");

    @FXML
    public void initialize() {
        populateTimeslotComboBoxes();

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


    @FXML
    private void handleSchedule(ActionEvent event) {
        // Collect input data
        String patientFirstName = this.patientFirstName.getText();
        String patientLastName = this.patientLastName.getText();
        LocalDate appointmentDate = this.appointmentDate.getValue();
        String dob = (this.dobPicker.getValue() != null) ? this.dobPicker.getValue().toString() : "";
        String provider = providerCombo.getValue();
        String timeslot = timeslotCombo.getValue();

        // Collect appointment type
        String appointmentType = officeVisitRadio.isSelected() ? "Office Visit" : imagingServiceRadio.isSelected() ? "Imaging Service" : "";

        // Validate inputs
        if (patientFirstName.isEmpty() || patientLastName.isEmpty() || appointmentDate == null || provider == null || timeslot == null || appointmentType.isEmpty()) {
            outputArea.appendText("Please fill all the required fields.\n");
            return;
        }

        // Display the appointment details in the output area
        outputArea.appendText("Appointment Scheduled:\n");
        outputArea.appendText("Patient Name: " + patientFirstName + " " + patientLastName + "\n");
        outputArea.appendText("Date of Birth: " + dob + "\n");
        outputArea.appendText("Appointment Date: " + appointmentDate.toString() + "\n");
        outputArea.appendText("Provider: " + provider + "\n");
        outputArea.appendText("Timeslot: " + timeslot + "\n");
        outputArea.appendText("Type of Visit: " + appointmentType + "\n\n");
    }

    @FXML
    private void handleCancel(ActionEvent event) {
        // Collect current appointment details before clearing
        String patientFirstNameValue = patientFirstName.getText();
        String patientLastNameValue = patientLastName.getText();
        LocalDate appointmentDateValue = appointmentDate.getValue();
        String dobValue = (dobPicker.getValue() != null) ? dobPicker.getValue().toString() : "";
        String providerValue = providerCombo.getValue();
        String timeslotValue = timeslotCombo.getValue();

        // Display cancellation details in the output area
        if (!patientFirstNameValue.isEmpty() && !patientLastNameValue.isEmpty()) {
            outputArea.appendText("Appointment Canceled:\n");
            outputArea.appendText("Patient Name: " + patientFirstNameValue + " " + patientLastNameValue + "\n");
            outputArea.appendText("Date of Birth: " + dobValue + "\n");
            outputArea.appendText("Appointment Date: " + (appointmentDateValue != null ? appointmentDateValue.toString() : "Not Scheduled") + "\n");
            outputArea.appendText("Provider: " + (providerValue != null ? providerValue : "Not Selected") + "\n");
            outputArea.appendText("Timeslot: " + (timeslotValue != null ? timeslotValue : "Not Selected") + "\n\n");
        } else {
            outputArea.appendText("No appointment details available to cancel.\n");
        }

        // Clear appointment-specific fields
        patientFirstName.clear();
        patientLastName.clear();
        appointmentDate.setValue(null);
        dobPicker.setValue(null);
        timeslotCombo.getSelectionModel().clearSelection();
        providerCombo.getSelectionModel().clearSelection();
        officeVisitRadio.setSelected(false);
        imagingServiceRadio.setSelected(false);
    }
    // Event handler for 'Reschedule' button
    @FXML
    private void handleReschedule(ActionEvent event) {
        // Get the new appointment details from the UI components
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        LocalDate newDate = newDatePicker.getValue();
        String newTime = newTimeComboBox.getValue();

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || newDate == null || newTime == null) {
            statusLabel.setText("Please fill in all fields: First Name, Last Name, New Date, and New Time.");
            return;
        }

        // Display rescheduling details in the output area
        outputArea.appendText("Appointment Rescheduled:\n");
        outputArea.appendText("Patient Name: " + firstName + " " + lastName + "\n");
        outputArea.appendText("New Appointment Date: " + newDate.toString() + "\n");
        outputArea.appendText("New Time: " + newTime + "\n\n");

        // Update status label
        statusLabel.setText("Appointment rescheduled successfully.");

        // Clear the input fields after rescheduling
        handleClearReschedule(event);
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


    @FXML
    private void handleScheduleAction(ActionEvent event) {
        outputArea.appendText("Navigated to Schedule Appointment.\n");
        tabPane.getSelectionModel().select(tabPane.getTabs().get(0)); // Select the first tab
    }

    @FXML
    private void handleRescheduleAction(ActionEvent event) {
        outputArea.appendText("Navigated to Reschedule Appointment.\n");
        tabPane.getSelectionModel().select(tabPane.getTabs().get(1)); // Select the second tab
    }

    @FXML
    private void handleCancelAction(ActionEvent event) {
        outputArea.appendText("Navigated to Cancel Appointment.\n");
        tabPane.getSelectionModel().select(tabPane.getTabs().get(0)); // Select the first tab again
    }



    // Event handler for listing appointments by date, time, or provider
    @FXML
    private void handleListByDateTime(ActionEvent event) {
        outputArea.appendText("Listing appointments by Date/Time/Provider.\n");
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

    // Event handler for listing appointments by location
    @FXML
    private void handleListByLocation(ActionEvent event) {
        outputArea.appendText("Listing appointments by location.\n");
        for (Location location : locations) {
            outputArea.appendText(location.toString() + "\n");
        }
    }

    // Method to validate if a timeslot is selected
    private boolean isTimeslotSelected(ComboBox<String> timeslotCombo) {
        return timeslotCombo.getSelectionModel().getSelectedItem() != null;
    }

    // Utility method to handle input validations
    private boolean validateInput(String patient, String provider, LocalDate date, String time) {
        if (patient.isEmpty() || provider == null || date == null || time == null) {
            outputArea.appendText("Please fill all required fields.\n");
            return false;
        }
        return true;
    }

    // Event handler for exiting the application (if needed)
    @FXML
    private void handleExit(ActionEvent event) {
        Alert exitAlert = new Alert(Alert.AlertType.CONFIRMATION);
        exitAlert.setTitle("Exit");
        exitAlert.setHeaderText("Are you sure you want to exit?");
        exitAlert.setContentText("Unsaved changes will be lost.");

        exitAlert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                System.exit(0);
            }
        });
    }

    // Example to handle saving appointment data (if required)
    @FXML
    private void handleSaveAppointments(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Appointments");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File saveFile = fileChooser.showSaveDialog(scheduleButton.getScene().getWindow());

        if (saveFile != null) {
            try {
                // Assuming you have logic to retrieve scheduled appointments as a string
                String appointmentData = retrieveScheduledAppointments();
                Files.writeString(saveFile.toPath(), appointmentData);
                outputArea.appendText("Appointments saved successfully.\n");
            } catch (IOException e) {
                outputArea.appendText("Failed to save appointments.\n");
            }
        }
    }

    // Mock method to retrieve scheduled appointments (replace with actual logic)
    private String retrieveScheduledAppointments() {
        return "Sample appointment data\n";
    }
}