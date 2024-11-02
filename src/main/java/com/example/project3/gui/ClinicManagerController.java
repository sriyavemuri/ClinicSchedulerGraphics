package com.example.project3.gui;

import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import com.example.project3.clinicscheduler.Timeslot;
import javafx.stage.FileChooser;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDate;
import javafx.scene.control.TabPane;
import com.example.project3.util.*;
import com.example.project3.clinicscheduler.*;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ClinicManagerController {

    // FXML Fields
    @FXML private DatePicker appointmentDate, followUpDate, newDatePicker;
    @FXML private TextField patientName, providerName;
    @FXML private ComboBox<Timeslot> timeslotCombo;
    @FXML private ComboBox<String> providerCombo;
    @FXML private ComboBox<Timeslot> newTimeComboBox;
    @FXML private ComboBox<Timeslot> existingTimeComboBox;
    @FXML private ComboBox<Radiology> imagingTypeCombo;
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
    @FXML private Label imagingTypeLabel;
    @FXML private Label npiLabel;
    @FXML private TextField npiTextField;

    private ObservableList<Location> locations;
    private ToggleGroup visitTypeGroup;
    private ObservableList<Radiology> imagingTypes;

    // List to store all providers, appointments, and technicians
    private List<Provider> providers = new List<>();
    private List<Appointment> appointmentList = new List<>();
    private List<Technician> technicianList = new List<>(); // circular list of technicians

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

        visitTypeGroup = new ToggleGroup();
        officeVisitRadio.setToggleGroup(visitTypeGroup);
        imagingServiceRadio.setToggleGroup(visitTypeGroup);
        visitTypeGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            updateProviderCombo();
            updateVisitType();
        });

        // Initialize imaging types
        imagingTypes = FXCollections.observableArrayList(Radiology.values());
        imagingTypeCombo.setItems(imagingTypes);
        imagingTypeCombo.setVisible(false); // Initially hidden

        // Disable the provider dropdown initially
        providerCombo.setDisable(true);
    }

    /**
     * Converts LocalDate object from fxml to Date object, coded in util package
     * @param localDate LocalDate object from fxml
     * @return Date object from xml file
     */
    private Date convertLocalDateToDate(LocalDate localDate) {
        int year = localDate.getYear();
        int month = localDate.getMonthValue();
        int day = localDate.getDayOfMonth();
        return new Date(year, month, day);
    }

    @FXML
    private void handleLoadProviders(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Provider File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Text Files", "*.txt"));
        File selectedFile = fileChooser.showOpenDialog(loadProvidersButton.getScene().getWindow());

        if (selectedFile != null) {
            loadProvidersFromFile(selectedFile);
            createTechnicianRotation();
        } else {
            outputArea.appendText("No file selected.\n");
        }
    }

    private void updateProviderCombo() {
        ObservableList<String> selectedProviders = FXCollections.observableArrayList();
        for (int i = 0; i < providers.size(); i++) {
            Provider provider = providers.get(i);
            String providerName = provider.getProfile().getFname() + " " + provider.getProfile().getLname();
            if (officeVisitRadio.isSelected() && provider instanceof Doctor) {
                selectedProviders.add(providerName);
            }
            if (imagingServiceRadio.isSelected() && provider instanceof Technician) {
                selectedProviders.add(providerName);
            }
        }
        providerCombo.setDisable(selectedProviders.isEmpty()); // Enables if there are items to select
        providerCombo.setItems(selectedProviders);
    }

    private void updateVisitType() {
        boolean isOfficeVisitSelected = officeVisitRadio.isSelected();
        boolean isImagingServiceSelected = imagingServiceRadio.isSelected();

        // Show NPI TextField for Office Visits
        npiLabel.setVisible(isOfficeVisitSelected);
        npiLabel.setManaged(isOfficeVisitSelected);
        npiTextField.setVisible(isOfficeVisitSelected);
        npiTextField.setManaged(isOfficeVisitSelected);

        // Show Imaging Type ComboBox for Imaging Services
        imagingTypeLabel.setVisible(isImagingServiceSelected);
        imagingTypeLabel.setManaged(isImagingServiceSelected);
        imagingTypeCombo.setVisible(isImagingServiceSelected);
        imagingTypeCombo.setManaged(isImagingServiceSelected);
    }

    private void loadProvidersFromFile(File filename) {
        Scanner sc = null;
        try {
            sc = new Scanner(filename);
            outputArea.appendText("Providers loaded to the list.\n");
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                StringTokenizer tokenizer = new StringTokenizer(line, " ");
                char providerType = tokenizer.nextToken().charAt(0);

                String firstName = tokenizer.nextToken();
                String lastName = tokenizer.nextToken();
                String dobString = tokenizer.nextToken();
                Date dob = dateFromToken(dobString);
                String locationstring = tokenizer.nextToken();
                Location providerLocation = Location.valueOf(locationstring.toUpperCase());

                if (providerType == 'D') {
                    String specialtyString = tokenizer.nextToken();
                    Specialty specialty = Specialty.valueOf(specialtyString.toUpperCase());
                    String npi = tokenizer.nextToken();
                    Profile doctorProfile = new Profile(firstName, lastName, dob);
                    Doctor doctor = new Doctor(doctorProfile, providerLocation, specialty, npi);
                    providers.add(doctor);
                } else if (providerType == 'T') {
                    String ratePerVisit = tokenizer.nextToken();
                    int technicianRate = Integer.parseInt(ratePerVisit);
                    Profile technicianProfile = new Profile(firstName, lastName, dob);
                    Technician technician = new Technician(technicianProfile, providerLocation, technicianRate);
                    providers.add(technician);
                }
            }
            printAllProviders();
        } catch (Exception e) {
            outputArea.appendText("Error loading providers from file.\n");
            e.printStackTrace();
        } finally {
            if (sc != null) sc.close();
        }
    }

    /**
     * Helper method. Reverses the order in which the technician list is added so that it matches with test cases.
     */
    private void reverseTechnicianList() {
        int n = technicianList.size();
        for (int i = 0; i < n / 2; i++) {
            Technician temp = technicianList.get(i);
            technicianList.set(i, technicianList.get(n - i - 1));
            technicianList.set(n - i - 1, temp);
        }
    }

    /**
     * Creates the circular list of technicians for imaging appointments.
     */
    private void createTechnicianRotation() {
        outputArea.appendText("\nRotation list for the technicians.\n");
        for (int i = 0; i < providers.size(); i++) {
            if (providers.get(i) instanceof Technician) {
                technicianList.add((Technician) providers.get(i));
            }
        }
        reverseTechnicianList();
        for (int i = 0; i < technicianList.size(); i++) {
            Technician tech = technicianList.get(i);
            outputArea.appendText(tech.getProfile().getFname().toUpperCase() + " "
                    + tech.getProfile().getLname().toUpperCase()
                    + " (" + tech.getLocation().name() + ")");
            if (i < technicianList.size() - 1) {
                outputArea.appendText(" --> ");
            } else {
                outputArea.appendText("\n");
            }
        }
    }

    /**
     * Prints all providers in the correct format in output Area.
     */
    private void printAllProviders() {
        Provider[] providerArray = new Provider[providers.size()];
        for (int i = 0; i < providers.size(); i++) {
            providerArray[i] = providers.get(i);  // Fill the array with providers
        }
        sortProvidersByName(providerArray);
        for (Provider provider : providerArray) {
            String prov = provider.toString();
            outputArea.appendText(prov+ "\n");
        }
    }

    /**
     * Helper method to sort providers by their profile's last name.
     *
     * @param providersArray The array of providers to be sorted.
     */
    private void sortProvidersByName(Provider[] providersArray) {
        for (int i = 0; i < providersArray.length - 1; i++) {
            for (int j = 0; j < providersArray.length - i - 1; j++) {
                String lastName1 = providersArray[j].getProfile().getLname();
                String lastName2 = providersArray[j + 1].getProfile().getLname();

                if (lastName1.compareTo(lastName2) > 0) {
                    Provider temp = providersArray[j];
                    providersArray[j] = providersArray[j + 1];
                    providersArray[j + 1] = temp;
                }
            }
        }
    }

    /**
     * Given a string, converts it to Date object.
     * @param dateString Date as a string.
     * @return date as a Date object.
     */
    private Date dateFromToken(String dateString) {
        String[] dateParts = dateString.split("/");
        try {
            int month = Integer.parseInt(dateParts[0]);
            int day = Integer.parseInt(dateParts[1]);
            int year = Integer.parseInt(dateParts[2]);
            return new Date(year, month, day);
        } catch (Exception e) {
            return null;
        }
    }

    private void populateTimeslotComboBoxes() {
        Timeslot[] availableSlots = generateAvailableSlots();
        ObservableList<Timeslot> timeslotList = FXCollections.observableArrayList(availableSlots);
        timeslotCombo.setItems(timeslotList);
        newTimeComboBox.setItems(timeslotList);
        existingTimeComboBox.setItems(timeslotList);
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

    private void scheduleImagingAppointment(Date apptDate, Profile patientProfile, Radiology imagingType, Timeslot timeslot) {
        if (hasExistingAppointment(apptDate, timeslot, patientProfile)) {
            outputArea.appendText(formatPatientName(patientProfile) + " has an existing appointment at the same time slot.");
            return;
        }
        Technician availableTechnician = findAvailableTechnician(apptDate, timeslot, imagingType);
        if (availableTechnician == null) {
            outputArea.appendText("Cannot find an available technician at all locations for " + imagingType + " at slot " + timeslot + ".");
            return;
        }
        bookImagingAppointment(apptDate, timeslot, patientProfile, availableTechnician, imagingType);
    }

    /**
     * Books a new imaging appointment by adding it to the appointment list.
     * @param appointmentDate The appointment date.
     * @param appointmentTimeslot The appointment timeslot.
     * @param patientProfile The patient's profile.
     * @param availableTechnician The available technician.
     * @param requestedRoom The requested imaging room.
     */
    private void bookImagingAppointment(Date appointmentDate, Timeslot appointmentTimeslot, Profile patientProfile, Technician availableTechnician, Radiology requestedRoom) {
        Imaging newImagingAppointment = new Imaging(appointmentDate, appointmentTimeslot, null, availableTechnician, requestedRoom);
        Visit imagingVisit = new Visit(newImagingAppointment, null);
        Patient patient = new Patient(patientProfile, imagingVisit);
        newImagingAppointment.setPatient(patient);
        appointmentList.add(newImagingAppointment);
        outputArea.appendText(newImagingAppointment + " booked.");
    }

    /**
     * Helper to rotate technicians and check room availability.
     */
    private int currentTechnicianIndex = 0; // Keep track of technician rotation

    /**
     * Finds an available technician
     * @param date date requested
     * @param timeslot time requested
     * @param room room requested
     * @return Technician available
     */
    private Technician findAvailableTechnician(Date date, Timeslot timeslot, Radiology room) {
        int initialIndex = currentTechnicianIndex;

        do {
            Technician tech = technicianList.get(currentTechnicianIndex);

            // Gather all existing appointments for this technician
            Appointment[] technicianAppointments = getTechnicianAppointments(tech);

            // Check technician availability for the date and timeslot across all rooms, not just the requested one
            if (isTechnicianAvailableForTimeslot(tech, date, timeslot, technicianAppointments) &&
                    isRoomAvailable(date, timeslot, room, tech.getLocation())) {

                // Technician is available, update the index for the next rotation
                Technician availableTechnician = tech;
                currentTechnicianIndex = (currentTechnicianIndex + 1) % technicianList.size();
                return availableTechnician;  // Return the first available technician
            }

            // Move to the next technician without updating the global currentTechnicianIndex
            currentTechnicianIndex = (currentTechnicianIndex + 1) % technicianList.size();

        } while (currentTechnicianIndex != initialIndex);

        // No available technician found
        return null;
    }

    /**
     * Checks if a room is available for a given date, timeslot, room type, and location.
     */
    private boolean isRoomAvailable(Date date, Timeslot timeslot, Radiology room, Location location) {
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment appointment = appointmentList.get(i);
            if (appointment instanceof Imaging) {
                Imaging imagingAppointment = (Imaging) appointment;

                // Make sure the provider is of type Technician
                if (imagingAppointment.getProvider() instanceof Technician) {
                    Technician technician = (Technician) imagingAppointment.getProvider();

                    // Check if the date, timeslot, room type, and location match
                    if (imagingAppointment.getDate().equals(date) &&
                            imagingAppointment.getTimeslot().equals(timeslot) &&
                            imagingAppointment.getRoom().equals(room) &&
                            technician.getLocation().equals(location)) {
                        return false; // Room is already booked at this location for the given date and timeslot
                    }
                }
            }
        }
        return true; // Room is available at this location for the given date and timeslot
    }

    /**
     * Helper method. Sees if a Technician is available at requested time and date
     * @param technician technician requested
     * @param date date requested
     * @param timeslot timeslot requested
     * @param appointments array of appointments
     * @return true if Technician is available. false otherwise.
     */
    private boolean isTechnicianAvailableForTimeslot(Technician technician, Date date, Timeslot timeslot, Appointment[] appointments) {
        if (appointments == null) {
            return true; // Technician is available if there are no appointments
        }

        // Iterate through each appointment to check if the technician is already booked at this date and timeslot
        for (int i = 0; i < appointments.length; i++) {
            Appointment appointment = appointments[i];
            if (appointment != null &&
                    appointment.getProvider().equals(technician) &&
                    appointment.getDate().equals(date) &&
                    appointment.getTimeslot().equals(timeslot)) {
                // The technician is already booked at this timeslot for any room
                return false;
            }
        }

        return true; // Technician is available for the timeslot
    }

    /**
     * Helper method to gather all appointments for a given technician and radiology service.
     */
    private Appointment[] getTechnicianAppointments(Technician technician) {
        List<Appointment> relevantAppointments = new List<>();
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment appointment = appointmentList.get(i);
            if (appointment instanceof Imaging) {
                Imaging imagingAppointment = (Imaging) appointment;
                if (imagingAppointment.getProvider().equals(technician)) {
                    relevantAppointments.add(imagingAppointment);
                }
            }
        }
        Appointment[] technicianAppointments = new Appointment[relevantAppointments.size()];
        for (int i = 0; i < relevantAppointments.size(); i++) {
            technicianAppointments[i] = relevantAppointments.get(i);
        }
        return technicianAppointments;
    }

    /**
     * Checks if there is an existing appointment for the same patient, date, and timeslot.
     *
     * @param appointmentDate     The appointment date.
     * @param appointmentTimeslot The appointment timeslot.
     * @param patientProfile      The patient's profile.
     * @return True if a duplicate appointment exists, otherwise false.
     */
    private boolean hasExistingAppointment(Date appointmentDate, Timeslot appointmentTimeslot, Profile patientProfile) {
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment existingAppointment = appointmentList.get(i);
            if (existingAppointment.getPatient().getProfile().equals(patientProfile) &&
                    existingAppointment.getDate().equals(appointmentDate) &&
                    existingAppointment.getTimeslot().equals(appointmentTimeslot)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Finds a provider by their NPI.
     */
    private Provider findProviderByNPI(String npi) {
        for (int i = 0; i < providers.size(); i++) {
            Provider provider = providers.get(i);
            if (provider instanceof Doctor doctor) {
                if (doctor.getNpi().equals(npi)) {
                    return doctor;
                }
            }
        }
        return null; // No provider with the given NPI found
    }

    private void scheduleDoctorAppointment(Date apptDate, Profile patientProfile, String providerString, Timeslot timeslot, Provider providerByNPI) {
        if (isDuplicateAppointment(apptDate, timeslot, patientProfile)) {
            outputArea.appendText(formatPatientName(patientProfile) + " has an existing appointment at the same time slot.");
            return;
        }
        if (!isProviderAvailable(providerByNPI, apptDate, timeslot)) {
            outputArea.appendText(providerString + " is not available at slot " + timeslot);
            return;
        }
        bookAppointment(apptDate, timeslot, patientProfile, providerByNPI);
    }

    /**
     * Books an appointment by adding it to the appointment list.
     * @param appointmentDate The appointment date.
     * @param appointmentTimeslot The appointment timeslot.
     * @param patientProfile The patient's profile.
     * @param provider The provider for the appointment.
     */
    private void bookAppointment(Date appointmentDate, Timeslot appointmentTimeslot, Profile patientProfile, Provider provider) {
        Patient patient = new Patient(patientProfile, new Visit(null, null));
        Appointment appointment = new Appointment(appointmentDate, appointmentTimeslot, patient, provider);
        appointmentList.add(appointment);
        outputArea.appendText(appointment + " booked.");
    }

    /**
     * Checks if there is an existing appointment for the same patient, date, and timeslot.
     *
     * @param appointmentDate     The appointment date.
     * @param appointmentTimeslot The appointment timeslot.
     * @param patientProfile      The patient's profile.
     * @return True if a duplicate appointment exists, otherwise false.
     */
    private boolean isDuplicateAppointment(Date appointmentDate, Timeslot appointmentTimeslot, Profile patientProfile) {
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment existingAppointment = appointmentList.get(i);
            if (existingAppointment.getPatient().getProfile().equals(patientProfile) &&
                    existingAppointment.getDate().equals(appointmentDate) &&
                    existingAppointment.getTimeslot().equals(appointmentTimeslot)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Formats the patient's name and date of birth into a readable string.
     * @param patientProfile The patient's profile.
     * @return The formatted patient name string.
     */
    private String formatPatientName(Profile patientProfile) {
        String firstName = patientProfile.getFname();
        String lastName = patientProfile.getLname();
        return firstName.substring(0, 1).toUpperCase() + firstName.substring(1).toLowerCase() + " " +
                lastName.substring(0, 1).toUpperCase() + lastName.substring(1).toLowerCase() + " " +
                patientProfile.getDob();
    }

    /**
     * Checks if the provider is available for a given date and timeslot.
     * @param provider The provider to check.
     * @param appointmentDate The appointment date.
     * @param appointmentTimeslot The appointment timeslot.
     * @return True if the provider is available, otherwise false.
     */
    private boolean isProviderAvailable(Provider provider, Date appointmentDate, Timeslot appointmentTimeslot) {
        Appointment[] appointmentsArray = convertAppointmentListToArray();
        return provider.isAvailable(appointmentDate, appointmentTimeslot, appointmentsArray);
    }

    /**
     * Helper method to convert the appointment list to an array.
     */
    private Appointment[] convertAppointmentListToArray() {
        Appointment[] array = new Appointment[appointmentList.size()];
        for (int i = 0; i < appointmentList.size(); i++) {
            array[i] = appointmentList.get(i);
        }
        return array;
    }

    @FXML
    private void handleSchedule(ActionEvent event) {
        // Collect input data
        Date apptDate = convertLocalDateToDate(this.appointmentDate.getValue());
        String patientFirstName = this.patientFirstName.getText();
        String patientLastName = this.patientLastName.getText();
        Date dob = convertLocalDateToDate(dobPicker.getValue());
        String providerString = providerCombo.getValue();
        Timeslot timeslot = timeslotCombo.getValue();

        // Make sure all inputs were given
        if (apptDate == null || patientFirstName.isEmpty() || patientLastName.isEmpty() || dob == null
                || providerString.isEmpty() || timeslot == null) {
            outputArea.appendText("Please fill all the required fields.\n");
            return;
        }

        Profile patientProfile = new Profile(patientFirstName,patientLastName, dob);

        // Check which radio button is selected and handle accordingly
        if (imagingServiceRadio.isSelected()) {
            // Check if imaging type is selected
            Radiology imagingType = imagingTypeCombo.getValue(); // Get selected imaging type
            if (imagingType == null) {
                outputArea.appendText("Please select the type of imaging appointment.\n");
                return;
            }

            // Call scheduleImagingAppointment method
            scheduleImagingAppointment(apptDate, patientProfile, imagingType, timeslot);

        } else if (officeVisitRadio.isSelected()) {
            String npi = npiTextField.getText();
            Provider providerByNPI = findProviderByNPI(npi);
            if (providerByNPI == null) {
                outputArea.appendText(npi + " - provider doesn't exist.");
                return;
            }
            // Call scheduleDoctorAppointment method
            scheduleDoctorAppointment(apptDate, patientProfile, providerString, timeslot, providerByNPI);
        } else {
            outputArea.appendText("Please select a type of visit.\n");
        }

    }

    @FXML
    private void handleCancel(ActionEvent event) {
        // Collect input data
        Date apptDate = convertLocalDateToDate(this.appointmentDate.getValue());
        String patientFirstName = this.patientFirstName.getText();
        String patientLastName = this.patientLastName.getText();
        Date dob = convertLocalDateToDate(dobPicker.getValue());
        String providerString = providerCombo.getValue();
        Timeslot timeslot = timeslotCombo.getValue();

        // Make sure all inputs were given
        if (apptDate == null || patientFirstName.isEmpty() || patientLastName.isEmpty() || dob == null || timeslot == null) {
            outputArea.appendText("Please fill all the required fields.\n");
            return;
        }
        Profile patientProfile = new Profile(patientFirstName,patientLastName, dob);
        Appointment appointmentToCancel = null;
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment currentAppointment = appointmentList.get(i);
            if (currentAppointment.getDate().equals(apptDate) &&
                    currentAppointment.getTimeslot().equals(timeslot) &&
                    currentAppointment.getPatient().getProfile().equals(patientProfile)) {
                appointmentToCancel = currentAppointment;
                break;
            }
        }
        if (appointmentToCancel != null) {
            appointmentList.remove(appointmentToCancel);
            outputArea.appendText(apptDate + " " + timeslot + " " +
                    patientFirstName + " " + patientLastName + " " + dob + " - appointment has been canceled.\n");
        } else {
            outputArea.appendText(apptDate + " " + timeslot + " " +
                    patientFirstName + " " + patientLastName + " " + dob +  " - appointment does not exist.\n");
        }
    }
    // Event handler for 'Reschedule' button
    @FXML
    private void handleReschedule(ActionEvent event) {
        // Get the new appointment details from the UI components
        String firstName = firstNameField.getText();
        String lastName = lastNameField.getText();
        Date oldDate = convertLocalDateToDate(newDatePicker.getValue());
        Date dob = convertLocalDateToDate(dobPickerReschedule.getValue());
        Timeslot newAppointmentTimeslot = existingTimeComboBox.getValue();
        Timeslot oldAppointmentTimeslot = newTimeComboBox.getValue();

        // Validate inputs
        if (firstName.isEmpty() || lastName.isEmpty() || newAppointmentTimeslot == null ||
                oldAppointmentTimeslot == null || oldDate == null || dob == null) {
            statusLabel.setText("Please fill in all fields.");
            return;
        }

        Profile patientProfile = new Profile (firstName, lastName, dob);
        Appointment appointmentToReschedule = null;
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment currentAppointment = appointmentList.get(i);
            if (currentAppointment.getDate().equals(oldDate) &&
                    currentAppointment.getTimeslot().equals(oldAppointmentTimeslot) &&
                    currentAppointment.getPatient().getProfile().equals(patientProfile)) {
                appointmentToReschedule = currentAppointment;
                break;
            }
        }
        if (appointmentToReschedule == null) {
            outputArea.appendText(oldDate + " " + oldAppointmentTimeslot + " " +
                    patientProfile + " does not exist.\n");
            return;
        }
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment currentAppointment = appointmentList.get(i);
            if (currentAppointment.getDate().equals(oldDate) &&
                    currentAppointment.getTimeslot().equals(newAppointmentTimeslot) &&
                    currentAppointment.getPatient().getProfile().equals(patientProfile)) {
                outputArea.appendText(patientProfile + " has an existing appointment at " +
                        oldDate + " " + newAppointmentTimeslot + "\n");
                return;
            }
        }
        boolean newTimeAvailable = true;
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment currentAppointment = appointmentList.get(i);
            if (currentAppointment.getDate().equals(oldDate) &&
                    currentAppointment.getTimeslot().equals(newAppointmentTimeslot) &&
                    !currentAppointment.getPatient().getProfile().equals(patientProfile)) {
                newTimeAvailable = false;
                outputArea.appendText("The new timeslot " + oldDate + " " + newAppointmentTimeslot +
                        " is already booked by another patient.\n");
                return;
            }
        }
        if (newTimeAvailable) {
            appointmentToReschedule.setTimeslot(newAppointmentTimeslot);
            String providerInfo = appointmentToReschedule.getProvider().toString();
            outputArea.appendText("Rescheduled to " + oldDate + " " +
                    newAppointmentTimeslot + " " +
                    patientProfile + " " +
                    providerInfo + "\n");
        }

        // DO WE NEED BELOW LINES? \\
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