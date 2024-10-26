package com.example.project3.gui;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.time.LocalDate;

public class ClinicManagerController {

    // Existing FXML fields for Appointment Management
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
    private Button loadProvidersButton; // Button to load providers

    // FXML fields for Clinic Locations
    @FXML
    private TableView<ClinicLocation> clinicTable; // TableView for clinic locations
    @FXML
    private TableColumn<ClinicLocation, String> cityColumn; // City column
    @FXML
    private TableColumn<ClinicLocation, String> countyColumn; // County column
    @FXML
    private TableColumn<ClinicLocation, String> zipColumn; // Zip column

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

        // Initialize clinic locations table
        initClinicTable();
    }

    private void initClinicTable() {
        // Initialize columns
        cityColumn.setCellValueFactory(new PropertyValueFactory<>("city"));
        countyColumn.setCellValueFactory(new PropertyValueFactory<>("county"));
        zipColumn.setCellValueFactory(new PropertyValueFactory<>("zip"));

        // Populate table with clinic locations
        ObservableList<ClinicLocation> locations = FXCollections.observableArrayList(
                new ClinicLocation("Bridgewater", "Somerset County", "08807"),
                new ClinicLocation("Edison", "Middlesex County", "08817"),
                new ClinicLocation("Piscataway", "Middlesex County", "08854"),
                new ClinicLocation("Princeton", "Mercer County", "08542"),
                new ClinicLocation("Morristown", "Morris County", "07960"),
                new ClinicLocation("Clark", "Union County", "07066")
        );

        clinicTable.setItems(locations);
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

    // Create a simple model class for ClinicLocation
    public static class ClinicLocation {
        private final String city;
        private final String county;
        private final String zip;

        public ClinicLocation(String city, String county, String zip) {
            this.city = city;
            this.county = county;
            this.zip = zip;
        }

        public String getCity() {
            return city;
        }

        public String getCounty() {
            return county;
        }

        public String getZip() {
            return zip;
        }
    }
}
