package com.example.project3.gui;
import com.example.project3.util.*;
import com.example.project3.clinicscheduler.*;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import java.io.File;
import java.time.LocalDate;
import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * Controller Class for JavaFx Project. Essentially the backend for the FXML front-end UI.
 * Carries out all operations in maintaining clinic manager system.
 * @author Sriya Vemuri, Zeel Patel
 */
public class ClinicManagerController {
    /**
     * FXML Fields
     */
    @FXML private DatePicker appointmentDate, followUpDate, newDatePicker, dobPicker, dobPickerReschedule;
    @FXML private TextField patientName, providerName, firstNameField, lastNameField, patientFirstName, patientLastName, npiTextField;
    @FXML private ComboBox<Timeslot> timeslotCombo, newTimeComboBox, existingTimeComboBox;
    @FXML private ComboBox<String> providerCombo;
    @FXML private ComboBox<Radiology> imagingTypeCombo;
    @FXML private RadioButton officeVisitRadio, imagingServiceRadio;
    @FXML private Button loadProvidersButton, scheduleButton, cancelButton, clearButton, rescheduleButton, clearButton1, clearRescheduleButton;
    @FXML private TextArea outputArea;
    @FXML private TableView<Location> clinicTable;
    @FXML private TableColumn<Location, String> locationColumn, countyColumn, zipColumn;
    @FXML private Label statusLabel, imagingTypeLabel, npiLabel;
    @FXML private TabPane tabPane;

    /**
     * Holds various configuration and selection options for the clinic management GUI.
     */
    private ObservableList<Location> locations;
    private ToggleGroup visitTypeGroup;
    private ObservableList<Radiology> imagingTypes;

    /**
     * List to store all providers, appointments, and technicians
     */
    private List<Provider> providers = new List<>();
    private List<Appointment> appointmentList = new List<>();
    private List<Technician> technicianList = new List<>(); // circular list of technicians

    /**
     * Starts up the JavaFx GUI for the program.
     */
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

    /**
     * Handles if a file is eligible to be loaded and read for a providers text file.
     * @param event Javafx action
     */
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

    /**
     * Updates provider dropdown based on provider list.
     */
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

    /**
     * Alters GUI depending on whether user is scheduling Doctor or Technician Appointment.
     */
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

    /**
     * Reads a loaded textfile and organizes the providers on the textfile.
     * @param filename text file loaded in by user.
     */
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

    /**
     * Handles timeslot dropdown menu.
     */
    private void populateTimeslotComboBoxes() {
        Timeslot[] availableSlots = generateAvailableSlots();
        ObservableList<Timeslot> timeslotList = FXCollections.observableArrayList(availableSlots);
        timeslotCombo.setItems(timeslotList);
        newTimeComboBox.setItems(timeslotList);
        existingTimeComboBox.setItems(timeslotList);
    }

    /**
     * Timeslot dropdown options.
     * @return none
     */
    private Timeslot[] generateAvailableSlots() {
        return new Timeslot[]{
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
    }

    /**
     * T - Schedules technician appointment.
     * @param apptDate appointment Date as a Date object
     * @param patientProfile patient basic profile (name, dob)
     * @param imagingType radiology room (xray, catscan, ultrasound)
     * @param timeslot time for requested appointment
     */
    private void scheduleImagingAppointment(Date apptDate, Profile patientProfile, Radiology imagingType, Timeslot timeslot) {
        if (hasExistingAppointment(apptDate, timeslot, patientProfile)) {
            outputArea.appendText(formatPatientName(patientProfile) + " has an existing appointment at the same time slot.\n");
            return;
        }
        Technician availableTechnician = findAvailableTechnician(apptDate, timeslot, imagingType);
        if (availableTechnician == null) {
            outputArea.appendText("Cannot find an available technician at all locations for " + imagingType + " at slot " + timeslot + ".\n");
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
        outputArea.appendText(newImagingAppointment + " booked.\n");
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

    /**
     * D - Schedules doctor appointment
     * @param apptDate appointment Date
     * @param patientProfile patient basic info (name and DOB)
     * @param providerString requested provider as a string.
     * @param timeslot time of requested appointment.
     * @param providerByNPI NPI of requested doctor.
     */
    private void scheduleDoctorAppointment(Date apptDate, Profile patientProfile, String providerString, Timeslot timeslot, Provider providerByNPI) {
        if (isDuplicateAppointment(apptDate, timeslot, patientProfile)) {
            outputArea.appendText(formatPatientName(patientProfile) + " has an existing appointment at the same time slot.\n");
            return;
        }
        if (!isProviderAvailable(providerByNPI, apptDate, timeslot)) {
            outputArea.appendText(providerString + " is not available at slot " + timeslot + "\n");
            return;
        }
        bookAppointment(apptDate, timeslot, patientProfile, providerByNPI);
    }

    /**
     * D Helper Method. Books an appointment by adding it to the appointment list.
     * @param appointmentDate The appointment date.
     * @param appointmentTimeslot The appointment timeslot.
     * @param patientProfile The patient's profile.
     * @param provider The provider for the appointment.
     */
    private void bookAppointment(Date appointmentDate, Timeslot appointmentTimeslot, Profile patientProfile, Provider provider) {
        Patient patient = new Patient(patientProfile, new Visit(null, null));
        Appointment appointment = new Appointment(appointmentDate, appointmentTimeslot, patient, provider);
        appointmentList.add(appointment);
        outputArea.appendText(appointment + " booked.\n");
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

    /**
     * D and T - Schedule Doctor and Technician Appointment.
     * @param event JavaFx action.
     */
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
            handleClear(event);

        } else if (officeVisitRadio.isSelected()) {
            String npi = npiTextField.getText();
            Provider providerByNPI = findProviderByNPI(npi);
            if (providerByNPI == null) {
                outputArea.appendText(npi + " - provider doesn't exist.\n");
                return;
            }
            // Call scheduleDoctorAppointment method
            scheduleDoctorAppointment(apptDate, patientProfile, providerString, timeslot, providerByNPI);
            handleClear(event);
        } else {
            outputArea.appendText("Please select a type of visit.\n");
        }

    }

    /**
     * C - Cancel Appointment.
     * @param event JavaFx action.
     */
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

    /**
     * R - Reschedule Appointment.
     * @param event JavaFx action.
     */
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

    /**
     * Clears all input from the front-end on Schedule/Cancel Appointment tab.
     * @param event JavaFx action.
     */
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

    /**
     * Clears all input from the front-end on Reschedule Appointment tab.
     * @param event JavaFx action.
     */
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

    /**
     * Navigates to Schedule Appointment Menu from Demo Bar.
     * @param event JavaFx action.
     */
    @FXML
    private void handleScheduleAction(ActionEvent event) {
        outputArea.appendText("Navigated to Schedule Appointment.\n");
        tabPane.getSelectionModel().select(tabPane.getTabs().get(0)); // Select the first tab
    }

    /**
     * Navigates to Reschedule Appointment Menu from Demo Bar.
     * @param event JavaFx action.
     */
    @FXML
    private void handleRescheduleAction(ActionEvent event) {
        outputArea.appendText("Navigated to Reschedule Appointment.\n");
        tabPane.getSelectionModel().select(tabPane.getTabs().get(1)); // Select the second tab
    }

    /**
     * Navigates to Cancel Appointment Menu from Demo Bar.
     * @param event JavaFx action.
     */
    @FXML
    private void handleCancelAction(ActionEvent event) {
        outputArea.appendText("Navigated to Cancel Appointment.\n");
        tabPane.getSelectionModel().select(tabPane.getTabs().get(0)); // Select the first tab again
    }

    /**
     * PA - List of all appointments, ordered by date/time/provider.
     * @param event JavaFx action.
     */
    @FXML
    private void handleListByDateTime(ActionEvent event) {
        if (appointmentList.isEmpty()) {
            outputArea.appendText("Schedule calendar is empty.\n");
        } else {
            Appointment[] appointmentsArray = new Appointment[appointmentList.size()];
            for (int i = 0; i < appointmentList.size(); i++) {
                appointmentsArray[i] = appointmentList.get(i);
            }
            boolean swapped;
            for (int i = 0; i < appointmentsArray.length - 1; i++) {
                swapped = false;
                for (int j = 0; j < appointmentsArray.length - i - 1; j++) {
                    if (compareAppointmentsByDateTimeProvider(appointmentsArray[j], appointmentsArray[j + 1]) > 0) {
                        Appointment temp = appointmentsArray[j];
                        appointmentsArray[j] = appointmentsArray[j + 1];
                        appointmentsArray[j + 1] = temp;
                        swapped = true;
                    }
                }
                if (!swapped) break;
            }
            outputArea.appendText("\n** List of appointments, ordered by date/time/provider.**\n");
            for (Appointment appointment : appointmentsArray) {
                outputArea.appendText(appointment.toString() + "\n");
            }
            outputArea.appendText("** end of list **");
        }
    }

    /**
     * Compares two appointments by date, time, and provider.
     *
     * @param a1 The first appointment.
     * @param a2 The second appointment.
     * @return A negative integer, zero, or a positive integer as the first argument is less than, equal to, or greater than the second.
     */
    private int compareAppointmentsByDateTimeProvider(Appointment a1, Appointment a2) {
        int dateComparison = a1.getDate().compareTo(a2.getDate());
        if (dateComparison != 0) {
            return dateComparison;
        }
        int timeComparison = a1.getTimeslot().compareTo(a2.getTimeslot());
        if (timeComparison != 0) {
            return timeComparison;
        }
        Profile provider1 = a1.getProvider().getProfile();
        Profile provider2 = a2.getProvider().getProfile();
        int lastNameComparison = provider1.getLname().compareTo(provider2.getLname());
        if (lastNameComparison != 0) {
            return lastNameComparison;
        }
        return provider1.getFname().compareTo(provider2.getFname());
    }

    /**
     * PL - List appointments by county/date/time
     * @param event JavaFx event.
     */
    @FXML
    private void handleListByLocation(ActionEvent event) {
        if (appointmentList.isEmpty()) {
            outputArea.appendText("Schedule calendar is empty.\n");
            return;
        }
        sortByLocationLengthVersion();
        outputArea.appendText("\n** List of appointments, ordered by county/date/time. **\n");
        for (int i = 0; i < appointmentList.size(); i++) {
            System.out.println(appointmentList.get(i).toString());
        }
        outputArea.appendText("** end of list **\n");
    }

    /**
     * PL In-place bubble sort method to order appointments by county (location), then date, and then timeslot.
     */
    private void sortByLocationLengthVersion() {
        boolean swapped;
        for (int i = 0; i < appointmentList.size() - 1; i++) {
            swapped = false;
            for (int j = 0; j < appointmentList.size() - i - 1; j++) {
                if (shouldSwapByLocationLength(j)) {
                    swapAppointments(j);
                    swapped = true;
                }
            }
            if (!swapped) {
                break; // List is already sorted
            }
        }
    }

    /**
     * Helper method to determine if two appointments should be swapped by location, date, timeslot, and patient full name length.
     *
     * @param index The current index of the appointment.
     * @return True if the appointments should be swapped, false otherwise.
     */
    private boolean shouldSwapByLocationLength(int index) {
        Appointment currentAppointment = appointmentList.get(index);
        Appointment nextAppointment = appointmentList.get(index + 1);

        Provider currentProvider = (Provider) currentAppointment.getProvider();
        Provider nextProvider = (Provider) nextAppointment.getProvider();
        Location currentLocation = currentProvider.getLocation();
        Location nextLocation = nextProvider.getLocation();

        // Compare by county
        int countyComparison = currentLocation.getCounty().compareTo(nextLocation.getCounty());
        if (countyComparison > 0) {
            return true;
        } else if (countyComparison == 0) {
            // Compare by date
            int dateComparison = currentAppointment.getDate().compareTo(nextAppointment.getDate());
            if (dateComparison > 0) {
                return true;
            } else if (dateComparison == 0) {
                // Compare by timeslot
                int timeslotComparison = currentAppointment.getTimeslot().compareTo(nextAppointment.getTimeslot());
                if (timeslotComparison > 0) {
                    return true;
                } else if (timeslotComparison == 0) {
                    // Compare by patient's full name length
                    String currentFullName = currentAppointment.getPatient().getProfile().getFname() + " " +
                            currentAppointment.getPatient().getProfile().getLname();
                    String nextFullName = nextAppointment.getPatient().getProfile().getFname() + " " +
                            nextAppointment.getPatient().getProfile().getLname();

                    if (currentFullName.length() > nextFullName.length()) {
                        return true;
                    } else if (currentFullName.length() == nextFullName.length()) {
                        // Compare by patient's full name lexicographically if lengths are equal
                        return currentFullName.compareTo(nextFullName) > 0;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Helper method to swap two appointments in the appointment list.
     *
     * @param index The current index of the appointment to be swapped.
     */
    private void swapAppointments(int index) {
        Appointment currentAppointment = appointmentList.get(index);
        Appointment nextAppointment = appointmentList.get(index + 1);
        appointmentList.set(index, nextAppointment);
        appointmentList.set(index + 1, currentAppointment);
    }

    /**
     * PP - List of all appointments ordered by patient/date/time
     * @param event Javafx action.
     */
    @FXML
    private void handleListByPatient(ActionEvent event) {
        if (appointmentList.isEmpty()) {
            outputArea.appendText("Schedule calendar is empty.\n");
            return;
        }
        Appointment[] appointmentsArray = new Appointment[appointmentList.size()];
        for (int i = 0; i < appointmentList.size(); i++) {
            appointmentsArray[i] = appointmentList.get(i);
        }
        sortAppointmentsByPatient(appointmentsArray);
        outputArea.appendText("\n** Appointments ordered by patient/date/time **\n");
        for (int i = 0; i < appointmentsArray.length; i++) {
            outputArea.appendText(appointmentsArray[i].toString() + "\n");
        }
        outputArea.appendText("** end of list **\n");
    }

    /**
     * Helper method to sort appointments by patient profile, then date, and then timeslot.
     * @param appointmentsArray The array of appointments to be sorted.
     */
    private void sortAppointmentsByPatient(Appointment[] appointmentsArray) {
        // Implementing a simple bubble sort to meet project restrictions
        for (int i = 0; i < appointmentsArray.length - 1; i++) {
            for (int j = 0; j < appointmentsArray.length - i - 1; j++) {
                // Compare by patient profile (last name, first name)
                Profile profile1 = appointmentsArray[j].getPatient().getProfile();
                Profile profile2 = appointmentsArray[j + 1].getPatient().getProfile();
                int profileComparison = profile1.compareTo(profile2);

                if (profileComparison > 0 ||
                        (profileComparison == 0 && appointmentsArray[j].getDate().compareTo(appointmentsArray[j + 1].getDate()) > 0) ||
                        (profileComparison == 0 && appointmentsArray[j].getDate().equals(appointmentsArray[j + 1].getDate()) &&
                                appointmentsArray[j].getTimeslot().compareTo(appointmentsArray[j + 1].getTimeslot()) > 0)) {

                    // Swap the appointments if out of order
                    Appointment temp = appointmentsArray[j];
                    appointmentsArray[j] = appointmentsArray[j + 1];
                    appointmentsArray[j + 1] = temp;
                }
            }
        }
    }

    /**
     * PO - List of office appointments ordered by county/date/time.
     * @param event JavaFx action.
     */
    @FXML
    private void handleListOfficeVisits(ActionEvent event) {
        if (appointmentList.isEmpty()) {
            outputArea.appendText("Schedule calendar is empty.\n");
            return;
        }
        sortByLocation();
        outputArea.appendText("\n** List of office appointments ordered by county/date/time.**\n");
        for (int i = 0; i < appointmentList.size(); i++) {
            if (!(appointmentList.get(i) instanceof Imaging)) {
                outputArea.appendText(appointmentList.get(i) + "\n");
            }
        }
        outputArea.appendText("** end of list **\n");
    }

    /**
     * PI - List of radiology appointments by county/date/time
     * @param event JavaFx action.
     */
    @FXML
    private void handleListImagingVisits(ActionEvent event) {
        if (appointmentList.isEmpty()) {
            outputArea.appendText("Schedule calendar is empty.\n");
            return;
        }
        sortByLocation();
        outputArea.appendText("\n** List of radiology appointments ordered by county/date/time.**n");
        for (int i = 0; i < appointmentList.size(); i++) {
            if ((appointmentList.get(i) instanceof Imaging)) {
                outputArea.appendText(appointmentList.get(i) + "\n");
            }
        }
        outputArea.appendText("** end of list **\n");
    }

    /**
     * PI PO In-place bubble sort method to order appointments by county (location), then date, and then timeslot.
     */
    private void sortByLocation() {
        boolean swapped;
        for (int i = 0; i < appointmentList.size() - 1; i++) {
            swapped = false;
            for (int j = 0; j < appointmentList.size() - i - 1; j++) {
                if (shouldSwapByLocation(j)) {
                    swapAppointments(j);
                    swapped = true;
                }
            }
            if (!swapped) {
                break; // List is already sorted
            }
        }
    }

    /**
     * Helper method to determine if two appointments should be swapped by location, date, timeslot, and provider name.
     *
     * @param index The current index of the appointment.
     * @return True if the appointments should be swapped, false otherwise.
     */
    private boolean shouldSwapByLocation(int index) {
        Appointment currentAppointment = appointmentList.get(index);
        Appointment nextAppointment = appointmentList.get(index + 1);

        Provider currentProvider = (Provider) currentAppointment.getProvider();
        Provider nextProvider = (Provider) nextAppointment.getProvider();
        Location currentLocation = currentProvider.getLocation();
        Location nextLocation = nextProvider.getLocation();

        // Compare by county
        int countyComparison = currentLocation.getCounty().compareTo(nextLocation.getCounty());
        if (countyComparison > 0) {
            return true;
        } else if (countyComparison == 0) {
            // Compare by date
            int dateComparison = currentAppointment.getDate().compareTo(nextAppointment.getDate());
            if (dateComparison > 0) {
                return true;
            } else if (dateComparison == 0) {
                // Compare by timeslot
                int timeslotComparison = currentAppointment.getTimeslot().compareTo(nextAppointment.getTimeslot());
                if (timeslotComparison > 0) {
                    return true;
                } else if (timeslotComparison == 0) {
                    // Compare by provider's first name
                    int providerFirstNameComparison = currentProvider.getProfile().getFname().compareTo(nextProvider.getProfile().getFname());
                    return providerFirstNameComparison > 0;
                }
            }
        }
        return false;
    }

    /**
     * PC - credit amount ordered by provider
     * @param event JavaFx action.
     */
    @FXML
    private void handlePatientStatement(ActionEvent event) {
        Appointment[] appointments = convertAppointmentListToArray();
        if (appointmentList.isEmpty()) {
            outputArea.appendText("Schedule calendar is empty.");
            return;
        }

        // Collect unique providers and their credit amounts
        int providerCount = 0;
        Provider[] uniqueProviders = new Provider[appointments.length];
        double[] creditAmounts = new double[appointments.length];
        providerCount = collectProviderCredits(appointments, uniqueProviders, creditAmounts, providerCount);

        // Sort providers by last name, first name, and DOB
        sortProviders(uniqueProviders, creditAmounts, providerCount);

        // Print the sorted list of credit amounts
        printSortedCreditAmounts(uniqueProviders, creditAmounts, providerCount);
    }

    /**
     * PC Helper Method. Collects the unique providers and calculates their respective credit amounts.
     *
     * @param appointments    The list of appointments.
     * @param uniqueProviders The array to hold unique providers.
     * @param creditAmounts   The array to hold credit amounts for each provider.
     * @param providerCount   The count of unique providers.
     * @return The updated provider count.
     */
    private int collectProviderCredits(Appointment[] appointments, Provider[] uniqueProviders, double[] creditAmounts, int providerCount) {
        for (int i = 0; i < appointments.length; i++) {
            Provider provider = (Provider) appointments[i].getProvider();
            double serviceCost = appointments[i].getServiceCost();
            int index = findProviderIndex(uniqueProviders, provider, providerCount);

            if (index == -1) {
                uniqueProviders[providerCount] = provider;
                creditAmounts[providerCount] = serviceCost;
                providerCount++;
            } else {
                creditAmounts[index] += serviceCost;
            }
        }
        return providerCount;
    }

    /**
     * PC Helper Method. Finds the index of the provider in the uniqueProviders array.
     *
     * @param uniqueProviders The array of unique providers.
     * @param provider        The provider to be searched.
     * @param providerCount   The count of unique providers.
     * @return The index of the provider if found, or -1 otherwise.
     */
    private int findProviderIndex(Provider[] uniqueProviders, Provider provider, int providerCount) {
        for (int j = 0; j < providerCount; j++) {
            if (uniqueProviders[j].equals(provider)) {
                return j;
            }
        }
        return -1;
    }

    /**
     * PC Helper Method. Sorts the providers based on last name, first name, and date of birth.
     *
     * @param uniqueProviders The array of unique providers.
     * @param creditAmounts   The array of credit amounts for each provider.
     * @param providerCount   The count of unique providers.
     */
    private void sortProviders(Provider[] uniqueProviders, double[] creditAmounts, int providerCount) {
        for (int i = 0; i < providerCount - 1; i++) {
            for (int j = i + 1; j < providerCount; j++) {
                Profile profile1 = uniqueProviders[i].getProfile();
                Profile profile2 = uniqueProviders[j].getProfile();

                int lastNameComparison = profile1.getLname().compareTo(profile2.getLname());
                if (lastNameComparison > 0 ||
                        (lastNameComparison == 0 && profile1.getFname().compareTo(profile2.getFname()) > 0) ||
                        (lastNameComparison == 0 && profile1.getFname().compareTo(profile2.getFname()) == 0 && profile1.getDob().compareTo(profile2.getDob()) > 0)) {

                    // Swap providers and credit amounts to keep them aligned
                    swapProviders(uniqueProviders, creditAmounts, i, j);
                }
            }
        }
    }

    /**
     * PC Helper Method. Swaps the providers and their respective credit amounts.
     *
     * @param uniqueProviders The array of unique providers.
     * @param creditAmounts   The array of credit amounts for each provider.
     * @param i               The first index to be swapped.
     * @param j               The second index to be swapped.
     */
    private void swapProviders(Provider[] uniqueProviders, double[] creditAmounts, int i, int j) {
        Provider tempProvider = uniqueProviders[i];
        uniqueProviders[i] = uniqueProviders[j];
        uniqueProviders[j] = tempProvider;

        double tempCredit = creditAmounts[i];
        creditAmounts[i] = creditAmounts[j];
        creditAmounts[j] = tempCredit;
    }

    /**
     * PC Helper Method. Prints the sorted list of credit amounts for each provider.
     *
     * @param uniqueProviders The array of unique providers.
     * @param creditAmounts   The array of credit amounts for each provider.
     * @param providerCount   The count of unique providers.
     */
    private void printSortedCreditAmounts(Provider[] uniqueProviders, double[] creditAmounts, int providerCount) {
        outputArea.appendText("\n** Credit amount ordered by provider. **\n");
        for (int i = 0; i < providerCount; i++) {
            outputArea.appendText("(" + (i + 1) + ") " +
                    uniqueProviders[i].getProfile().getFname().toUpperCase() + " " +
                    uniqueProviders[i].getProfile().getLname().toUpperCase() + " " +
                    uniqueProviders[i].getProfile().getDob() + " [" +
                    "credit amount: $" + String.format("%,.2f", creditAmounts[i]) + "]\n");
        }
        outputArea.appendText("** end of list **\n");
    }

    /**
     * PS - Billing statement ordered by patient.
     * @param event JavaFx action.
     */
    @FXML
    private void handleProviderStatement(ActionEvent event) {
        Appointment[] appointments = convertAppointmentListToArray();
        if (appointmentList.isEmpty()) {
            outputArea.appendText("Schedule calendar is empty.\n");
            return;
        }
        int patientCount = 0;
        Person[] uniquePatients = new Person[appointments.length];
        double[] billingAmounts = new double[appointments.length];
        patientCount = collectPatientBilling(appointments, uniquePatients, billingAmounts, patientCount);
        sortPatients(uniquePatients, billingAmounts, patientCount);
        printSortedBillingStatements(uniquePatients, billingAmounts, patientCount);
        clearAppointmentList();
    }

    /**
     * PS Helper Method. Collects unique patients and calculates their billing amounts.
     *
     * @param appointments    The list of appointments.
     * @param uniquePatients  The array to hold unique patients.
     * @param billingAmounts  The array to hold billing amounts for each patient.
     * @param patientCount    The count of unique patients.
     * @return The updated patient count.
     */
    private int collectPatientBilling(Appointment[] appointments, Person[] uniquePatients, double[] billingAmounts, int patientCount) {
        for (int i = 0; i < appointments.length; i++) {
            Person patient = appointments[i].getPatient();
            double serviceCost = appointments[i].getServiceCost();
            int index = findPatientIndex(uniquePatients, patient, patientCount);

            if (index == -1) {
                uniquePatients[patientCount] = patient;
                billingAmounts[patientCount] = serviceCost;
                patientCount++;
            } else {
                billingAmounts[index] += serviceCost;
            }
        }
        return patientCount;
    }

    /**
     * PS Helper Method. Finds the index of the patient in the uniquePatients array.
     *
     * @param uniquePatients The array of unique patients.
     * @param patient        The patient to be searched.
     * @param patientCount   The count of unique patients.
     * @return The index of the patient if found, or -1 otherwise.
     */
    private int findPatientIndex(Person[] uniquePatients, Person patient, int patientCount) {
        for (int j = 0; j < patientCount; j++) {
            if (uniquePatients[j].equals(patient)) {
                return j;
            }
        }
        return -1;
    }

    /**
     * PS Helper Method. Sorts the patients based on last name, first name, and date of birth.
     *
     * @param uniquePatients The array of unique patients.
     * @param billingAmounts The array of billing amounts for each patient.
     * @param patientCount   The count of unique patients.
     */
    private void sortPatients(Person[] uniquePatients, double[] billingAmounts, int patientCount) {
        for (int i = 0; i < patientCount - 1; i++) {
            for (int j = i + 1; j < patientCount; j++) {
                Profile profileI = uniquePatients[i].getProfile();
                Profile profileJ = uniquePatients[j].getProfile();

                int lastNameComparison = profileI.getLname().compareTo(profileJ.getLname());
                if (lastNameComparison > 0 ||
                        (lastNameComparison == 0 && profileI.getFname().compareTo(profileJ.getFname()) > 0) ||
                        (lastNameComparison == 0 && profileI.getFname().compareTo(profileJ.getFname()) == 0 &&
                                profileI.getDob().compareTo(profileJ.getDob()) > 0)) {
                    swapPatients(uniquePatients, billingAmounts, i, j);
                }
            }
        }
    }

    /**
     * PS Helper Method. Swaps the patients and their respective billing amounts.
     *
     * @param uniquePatients The array of unique patients.
     * @param billingAmounts The array of billing amounts for each patient.
     * @param i              The first index to be swapped.
     * @param j              The second index to be swapped.
     */
    private void swapPatients(Person[] uniquePatients, double[] billingAmounts, int i, int j) {
        Person tempPatient = uniquePatients[i];
        uniquePatients[i] = uniquePatients[j];
        uniquePatients[j] = tempPatient;

        double tempBilling = billingAmounts[i];
        billingAmounts[i] = billingAmounts[j];
        billingAmounts[j] = tempBilling;
    }

    /**
     * PS Helper Method. Prints the sorted billing statements for each patient.
     *
     * @param uniquePatients The array of unique patients.
     * @param billingAmounts The array of billing amounts for each patient.
     * @param patientCount   The count of unique patients.
     */
    private void printSortedBillingStatements(Person[] uniquePatients, double[] billingAmounts, int patientCount) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");

        outputArea.appendText("\n** Billing statement ordered by patient. **\n");
        for (int i = 0; i < patientCount; i++) {
            outputArea.appendText("(" + (i + 1) + ") " + uniquePatients[i].getProfile() +
                    " [due: $" + formatter.format(billingAmounts[i]) + "]\n");
        }
        outputArea.appendText("** end of list **\n");
    }

    /**
     * PS Helper Method. Clears appointment list after printing PS statement.
     */
    private void clearAppointmentList() {
        // Iterate over the appointmentList and remove each appointment
        while (appointmentList.size() > 0) {
            Appointment appointment = appointmentList.get(0);  // Always get the first element
            appointmentList.remove(appointment);  // Remove it by reference
        }
    }

    /**
     * Q - Quits the program
     * @param event JavaFX action.
     */
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
}