package com.example.project3.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;


import java.time.LocalDate;

public class ClinicManagerController {

    // Existing FXML fields
    @FXML
    private ComboBox<String> appointmentComboBox; // ComboBox for selecting existing appointments
    @FXML
    private ToggleGroup visitTypeToggleGroup;

    @FXML
    private RadioButton officeVisitRadio;

    @FXML
    private RadioButton imagingServiceRadio;
    @FXML
    private DatePicker newDatePicker; // DatePicker for selecting the new date
    @FXML
    private ComboBox<String> newTimeComboBox; // ComboBox for selecting the new time
    @FXML
    private Button rescheduleButton; // Button to confirm rescheduling
    @FXML
    private Label statusLabel; // Label for displaying status messages

    @FXML
    private Button loadProvidersButton;

    @FXML
    public void initialize() {
        // Initialize the ToggleGroup and assign it to the RadioButtons
        visitTypeToggleGroup = new ToggleGroup();
        officeVisitRadio.setToggleGroup(visitTypeToggleGroup);
        imagingServiceRadio.setToggleGroup(visitTypeToggleGroup);

        // Add listener to the selectedToggleProperty of the ToggleGroup
        visitTypeToggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle != null) {
                boolean isOfficeVisitSelected = newToggle == officeVisitRadio;
                loadProvidersButton.setDisable(isOfficeVisitSelected);
            }
        });
    }


    private void loadAppointments() {
        // Load existing appointments into appointmentComboBox
        // This might involve fetching from a database or a data structure that holds appointments
    }

    private void handleReschedule() {
        String selectedAppointment = appointmentComboBox.getValue();
        LocalDate newDate = newDatePicker.getValue();
        String newTime = newTimeComboBox.getValue();

        // Validate inputs
        if (selectedAppointment == null || newDate == null || newTime == null) {
            statusLabel.setText("Please select an appointment and enter new details.");
            return;
        }

        // Implement the logic to update the appointment (this will depend on your data structure)
        boolean success = rescheduleAppointment(selectedAppointment, newDate, newTime);

        // Provide feedback
        if (success) {
            statusLabel.setText("Appointment rescheduled successfully.");
            loadAppointments(); // Optionally reload appointments
        } else {
            statusLabel.setText("Failed to reschedule appointment. Please try again.");
        }
    }

    private boolean rescheduleAppointment(String appointment, LocalDate newDate, String newTime) {
        // Implement your logic to update the appointment in your data model
        // Return true if successful, false otherwise
        return true; // Placeholder
    }
}
