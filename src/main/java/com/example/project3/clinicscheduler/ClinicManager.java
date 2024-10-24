package clinicscheduler;

import java.text.DecimalFormat;
import java.util.Scanner;
import java.util.StringTokenizer;
import util.List;
import util.Date;
import util.Sort;
import java.util.Calendar;

/**
 * ClinicManager class to process command lines and manage clinic appointments.
 * Replaces the Scheduler class from Project 1.
 * Author: Sriya Vemuri, Zeel Patel
 */
public class ClinicManager {

    private List<Provider> providers;  // List of providers (doctors and technicians)
    private List<Appointment> appointmentList;  // List of office and imaging appointments
    private List<Technician> technicianList;  // Circular list of technicians for imaging

    /**
     * Constructor to initialize the ClinicManager and load providers from file.
     */
    public ClinicManager() {
        providers = new List<>();
        appointmentList = new List<>();
        technicianList = new List<>();
        loadProvidersFromFile("providers.txt");
        createTechnicianRotation();
        System.out.println("Clinic Manager is running...");
    }

    /**
     * Helper method to run() and executeCommand(). Prints out "Invalid command!"
     */
    private void invalidCommand() {
        System.out.println("Invalid command!");
    }

    /**
     * Helper method to run(). Checks if the command is a valid command.
     * This checks if the command is uppercase.
     *
     * @param command String that has the command input
     * @return true if valid command, false otherwise.
     */
    private boolean isValidCommand(String command) {
        return command.equals(command.toUpperCase()) && command.matches("[A-Z]+");
    }

    /**
     * Helper method to run(). Takes command and executes respective operation.
     *
     * @param command   the command
     * @param tokenizer everything else the user inputted for the command to work
     * @param scanner   the Scanner object reading the command line.
     */
    private void executeCommand(String command, StringTokenizer tokenizer, Scanner scanner) {
        switch (command) {
            case "D":
                scheduleDoctorAppointment(tokenizer);
                break;
            case "T":
                scheduleImagingAppointment(tokenizer);
                break;
            case "R":
                rescheduleAppointment(tokenizer);
                break;
            case "C":
                cancelAppointment(tokenizer);
                break;
            case "PA":
                printAllAppointments();
                break;
            case "PP":
                printAppointmentsByPatient();
                break;
            case "PL":
                printAppointmentsByLocation();
                break;
            case "PS":
                printBillingStatements(convertAppointmentListToArray());
                break;
            case "PO":
                printOfficeAppointments();
                break;
            case "PI":
                printImagingAppointments();
                break;
            case "PC":
                printCreditAmounts(convertAppointmentListToArray());
                break;
            case "Q":
                endScheduler(scanner);
                break;
            default:
                invalidCommand();
                break;
        }
    }

    /**
     * Helper method to run(). Ends the scheduling system.
     * @param scanner the scanner object.
     */
    private void endScheduler(Scanner scanner) {
        System.out.println("Clinic Manager terminated.");
        scanner.close(); // Close the scanner
        System.exit(0); // Properly exit the system
    }

    /**
     * Loads providers from the specified file.
     * @param filename the name of the file to be read.
     */
    private void loadProvidersFromFile(String filename) {
        Scanner sc = null;
        try {
            sc = new Scanner(new java.io.File(filename));
            System.out.println("Providers loaded to the list.");
            while (sc.hasNextLine()) {
                String line = sc.nextLine().trim();
                if (line.isEmpty()) continue;

                StringTokenizer tokenizer = new StringTokenizer(line, " ");
                char providerType = tokenizer.nextToken().charAt(0);

                String firstName = tokenizer.nextToken();
                String lastName = tokenizer.nextToken();
                String dobString = tokenizer.nextToken();
                Date dob = dateFromToken(dobString);
                String location = tokenizer.nextToken();

                if (providerType == 'D') {
                    String specialty = tokenizer.nextToken();
                    String npi = tokenizer.nextToken();
                    Profile doctorProfile = new Profile(firstName, lastName, dob);
                    providers.add(new Doctor(doctorProfile, Location.valueOf(location.toUpperCase()),
                            Specialty.valueOf(specialty.toUpperCase()), npi));
                } else if (providerType == 'T') {
                    String ratePerVisit = tokenizer.nextToken();
                    Profile technicianProfile = new Profile(firstName, lastName, dob);
                    providers.add(new Technician(technicianProfile, Location.valueOf(location.toUpperCase()),
                            Integer.parseInt(ratePerVisit)));
                }
            }
            printAllProviders();
        } catch (Exception e) {
            System.out.println("Error loading providers from file.");
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
        System.out.print("\nRotation list for the technicians.\n");
        for (int i = 0; i < providers.size(); i++) {
            if (providers.get(i) instanceof Technician) {
                technicianList.add((Technician) providers.get(i));
            }
        }
        reverseTechnicianList();
        for (int i = 0; i < technicianList.size(); i++) {
            Technician tech = technicianList.get(i);
            System.out.print(tech.getProfile().getFname().toUpperCase() + " " + tech.getProfile().getLname().toUpperCase() + " (" + tech.getLocation().name() + ")");
            if (i < technicianList.size() - 1) {
                System.out.print(" --> ");
            } else {
                System.out.println("\n");
            }
        }
    }

    /**
     * Schedules a doctor appointment based on tokens from the input.
     * @param tokenizer The StringTokenizer containing appointment details.
     */
    private void scheduleDoctorAppointment(StringTokenizer tokenizer) {
        if (tokenizer.countTokens() < 6) {
            System.out.println("Missing data tokens.");
            return;
        }
        // Parse inputs
        Date appointmentDate = parseAppointmentDate(tokenizer);
        if (appointmentDate == null) return;
        String hourString = tokenizer.nextToken();
        Timeslot timeslot = parseTimeslot(hourString);
        if (timeslot == null) return;
        Profile patientProfile = parsePatientProfile(tokenizer);
        if (patientProfile == null) return;
        String npi = tokenizer.nextToken();
        // Check for existing appointment
        if (isDuplicateAppointment(appointmentDate, timeslot, patientProfile)) {
            System.out.println(formatPatientName(patientProfile) + " has an existing appointment at the same time slot.");
            return;
        }
        // Find provider and check availability
        Provider provider = findProviderByNPI(npi);
        if (provider == null) {
            System.out.println(npi + " - provider doesn't exist.");
            return;
        }
        if (!isProviderAvailable(provider, appointmentDate, timeslot)) {
            System.out.println(provider + " is not available at slot " + hourString);
            return;
        }
        // Book the appointment
        bookAppointment(appointmentDate, timeslot, patientProfile, provider);
    }

    /**
     * Parses the appointment date from the tokenizer.
     * @param tokenizer The StringTokenizer containing appointment details.
     * @return The parsed appointment date, or null if the date is invalid.
     */
    private Date parseAppointmentDate(StringTokenizer tokenizer) {
        String dateString = tokenizer.nextToken();
        Date appointmentDate = dateFromToken(dateString);
        if (!isValidAppointmentDate(appointmentDate, dateString)) {
            return null;
        }
        return appointmentDate;
    }

    /**
     * Parses the patient profile from the tokenizer.
     * @param tokenizer The StringTokenizer containing patient details.
     * @return The parsed patient profile, or null if the DOB is invalid.
     */
    private Profile parsePatientProfile(StringTokenizer tokenizer) {
        String firstName = tokenizer.nextToken().toLowerCase();
        String lastName = tokenizer.nextToken().toLowerCase();
        String DOBString = tokenizer.nextToken();
        Date DOB = dateFromToken(DOBString);
        if (!isValidDOB(DOB, DOBString)) {
            return null;
        }
        return new Profile(firstName, lastName, DOB);
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
        System.out.println(appointment + " booked.");
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
     * Helper method to parse a timeslot number (1 to 12) into a Timeslot object with the correct time.
     */
    private Timeslot parseTimeslot(String timeString) {
        try {
            int slot = Integer.parseInt(timeString);
            if (slot < 1 || slot > 12) {
                System.out.println(slot + " is not a valid time slot.");
                return null;
            }
            switch (slot) {
                case 1:
                    return new Timeslot(9, 0);  // 9:00 AM
                case 2:
                    return new Timeslot(9, 30);  // 9:30 AM
                case 3:
                    return new Timeslot(10, 0);  // 10:00 AM
                case 4:
                    return new Timeslot(10, 30);  // 10:30 AM
                case 5:
                    return new Timeslot(11, 0);  // 11:00 AM
                case 6:
                    return new Timeslot(11, 30);  // 11:30 AM
                case 7:
                    return new Timeslot(14, 0);  // 2:00 PM
                case 8:
                    return new Timeslot(14, 30);  // 2:30 PM
                case 9:
                    return new Timeslot(15, 0);  // 3:00 PM
                case 10:
                    return new Timeslot(15, 30);  // 3:30 PM
                case 11:
                    return new Timeslot(16, 0);  // 4:00 PM
                case 12:
                    return new Timeslot(16, 30);  // 4:30 PM
                default:
                    System.out.println(slot + " is not a valid time slot.");
                    return null;
            }
        } catch (NumberFormatException e) {
            System.out.println(timeString + " is not a valid time slot.");
            return null;
        }
    }

    /**
     * Schedules a new imaging appointment based on the T command input.
     *
     * @param tokenizer The tokenizer for command input.
     */
    private void scheduleImagingAppointment(StringTokenizer tokenizer) {
        // Ensure that there are enough tokens before accessing them
        if (tokenizer.countTokens() < 6) {
            System.out.println("Missing data tokens.");
            return;
        }

        // Parse inputs and validate them
        Date appointmentDate = extractAppointmentDate(tokenizer);
        if (appointmentDate == null) return;

        String hourString = tokenizer.nextToken();
        Timeslot appointmentTimeslot = parseTimeslot(hourString);
        if (appointmentTimeslot == null) return;

        Profile patientProfile = extractPatientProfile(tokenizer);
        if (patientProfile == null) return;

        Radiology requestedRoom = extractImagingService(tokenizer);
        if (requestedRoom == null) return;

        // Check for duplicate appointment
        if (hasExistingAppointment(appointmentDate, appointmentTimeslot, patientProfile)) {
            System.out.println(formatPatientName(patientProfile) + " has an existing appointment at the same time slot.");
            return;
        }

        // Find technician and book the appointment
        Technician availableTechnician = findAvailableTechnician(appointmentDate, appointmentTimeslot, requestedRoom);
        if (availableTechnician == null) {
            System.out.println("Cannot find an available technician at all locations for " + requestedRoom + " at slot " + hourString + ".");
            return;
        }

        bookImagingAppointment(appointmentDate, appointmentTimeslot, patientProfile, availableTechnician, requestedRoom);
    }

    /**
     * Extracts and validates the appointment date from the tokenizer.
     * @param tokenizer The StringTokenizer containing command details.
     * @return The valid appointment date, or null if invalid.
     */
    private Date extractAppointmentDate(StringTokenizer tokenizer) {
        String dateString = tokenizer.nextToken();
        Date appointmentDate = dateFromToken(dateString);
        if (!isValidAppointmentDate(appointmentDate, dateString)) {
            return null;
        }
        return appointmentDate;
    }

    /**
     * Extracts and validates the patient's profile from the tokenizer.
     * @param tokenizer The StringTokenizer containing command details.
     * @return The valid patient profile, or null if invalid.
     */
    private Profile extractPatientProfile(StringTokenizer tokenizer) {
        String firstName = tokenizer.nextToken().toLowerCase();
        String lastName = tokenizer.nextToken().toLowerCase();
        String DOBString = tokenizer.nextToken();
        Date DOB = dateFromToken(DOBString);
        if (!isValidDOB(DOB, DOBString)) {
            return null;
        }
        return new Profile(firstName, lastName, DOB);
    }

    /**
     * Extracts and validates the imaging service from the tokenizer.
     * @param tokenizer The StringTokenizer containing command details.
     * @return The valid Radiology service, or null if invalid.
     */
    private Radiology extractImagingService(StringTokenizer tokenizer) {
        String imagingService = tokenizer.nextToken().toLowerCase();
        try {
            return Radiology.valueOf(imagingService.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println(imagingService + " - imaging service not provided.");
            return null;
        }
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
        System.out.println(newImagingAppointment + " booked.");
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
     * Cancels an existing appointment.
     *
     * @param tokenizer The tokenizer for command input.
     */
    private void cancelAppointment(StringTokenizer tokenizer) {
        if (tokenizer.countTokens() < 5) {
            System.out.println("Missing data tokens.");
            return;
        }
        String dateString = tokenizer.nextToken();
        String hourString = tokenizer.nextToken();
        String firstName = tokenizer.nextToken();
        String lastName = tokenizer.nextToken();
        String dobString = tokenizer.nextToken();
        Date appointmentDate = dateFromToken(dateString);
        Timeslot appointmentTimeslot = parseTimeslot(hourString);
        if (appointmentTimeslot == null) return;
        Date dob = dateFromToken(dobString);
        Profile patientProfile = new Profile(firstName, lastName, dob);
        Appointment appointmentToCancel = null;
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment currentAppointment = appointmentList.get(i);
            if (currentAppointment.getDate().equals(appointmentDate) &&
                    currentAppointment.getTimeslot().equals(appointmentTimeslot) &&
                    currentAppointment.getPatient().getProfile().equals(patientProfile)) {
                appointmentToCancel = currentAppointment;
                break;
            }
        }
        if (appointmentToCancel != null) {
            appointmentList.remove(appointmentToCancel);
            System.out.println(appointmentDate + " " + appointmentTimeslot + " " +
                    firstName + " " + lastName + " " + dobString + " - appointment has been canceled.");
        } else {
            System.out.println(appointmentDate + " " + appointmentTimeslot + " " +
                    firstName + " " + lastName + " " + dobString +  " - appointment does not exist.");
        }
    }

    /**
     * Reschedules an existing appointment based on the given input from the StringTokenizer.
     *
     * @param tokenizer The tokenizer containing the command input.
     */
    private void rescheduleAppointment(StringTokenizer tokenizer) {
        if (tokenizer.countTokens() < 6) {
            System.out.println("Missing data tokens.");
            return;
        }
        String oldDateString = tokenizer.nextToken();
        String oldHourString = tokenizer.nextToken();
        String firstName = tokenizer.nextToken();
        String lastName = tokenizer.nextToken();
        String dobString = tokenizer.nextToken();
        String newHourString = tokenizer.nextToken();
        Date oldDate = dateFromToken(oldDateString);
        Timeslot oldAppointmentTimeslot = parseTimeslot(oldHourString);
        if (oldAppointmentTimeslot == null) {
            System.out.println(oldHourString + " is not a valid timeslot.");
            return;
        }
        Timeslot newAppointmentTimeslot = parseTimeslot(newHourString);
        if (newAppointmentTimeslot == null) {
            System.out.println(newHourString + " is not a valid timeslot.");
            return;
        }
        Profile patientProfile = new Profile(firstName, lastName, dateFromToken(dobString));
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
            System.out.println(oldDate + " " + oldAppointmentTimeslot + " " +
                    patientProfile + " does not exist.");
            return;
        }
        for (int i = 0; i < appointmentList.size(); i++) {
            Appointment currentAppointment = appointmentList.get(i);
            if (currentAppointment.getDate().equals(oldDate) &&
                    currentAppointment.getTimeslot().equals(newAppointmentTimeslot) &&
                    currentAppointment.getPatient().getProfile().equals(patientProfile)) {
                System.out.println(patientProfile + " has an existing appointment at " +
                        oldDate + " " + newAppointmentTimeslot);
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
                System.out.println("The new timeslot " + oldDate + " " + newAppointmentTimeslot +
                        " is already booked by another patient.");
                return;
            }
        }
        if (newTimeAvailable) {
            appointmentToReschedule.setTimeslot(newAppointmentTimeslot);
            String providerInfo = appointmentToReschedule.getProvider().toString();
            System.out.println("Rescheduled to " + oldDate + " " +
                    newAppointmentTimeslot + " " +
                    patientProfile + " " +
                    providerInfo);
        }
    }

    /**
     * Prints all office appointments sorted by location.
     */
    private void printOfficeAppointments() {
        if (appointmentList.isEmpty()) {
            System.out.println("Schedule calendar is empty.");
            return;
        }
        sortByLocation();
        System.out.println("\n** List of office appointments ordered by county/date/time.");
        for (int i = 0; i < appointmentList.size(); i++) {
            if (!(appointmentList.get(i) instanceof Imaging)) {
                System.out.println(appointmentList.get(i));
            }
        }
        System.out.println("** end of list **");
    }

    /**
     * Prints all imaging appointments sorted by location.
     */
    private void printImagingAppointments() {
        if (appointmentList.isEmpty()) {
            System.out.println("Schedule calendar is empty.");
            return;
        }
        sortByLocation();
        System.out.println("\n** List of radiology appointments ordered by county/date/time.");
        for (int i = 0; i < appointmentList.size(); i++) {
            if ((appointmentList.get(i) instanceof Imaging)) {
                System.out.println(appointmentList.get(i));
            }
        }
        System.out.println("** end of list **");
    }

    /**
     * Prints credit amounts for each provider in the appointment list.
     *
     * @param appointments The list of appointments.
     */
    public void printCreditAmounts(Appointment[] appointments) {
        if (appointmentList.isEmpty()) {
            System.out.println("Schedule calendar is empty.");
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
     * Collects the unique providers and calculates their respective credit amounts.
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
     * Finds the index of the provider in the uniqueProviders array.
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
     * Sorts the providers based on last name, first name, and date of birth.
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
     * Swaps the providers and their respective credit amounts.
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
     * Prints the sorted list of credit amounts for each provider.
     *
     * @param uniqueProviders The array of unique providers.
     * @param creditAmounts   The array of credit amounts for each provider.
     * @param providerCount   The count of unique providers.
     */
    private void printSortedCreditAmounts(Provider[] uniqueProviders, double[] creditAmounts, int providerCount) {
        System.out.println("\n** Credit amount ordered by provider. **");
        for (int i = 0; i < providerCount; i++) {
            System.out.println("(" + (i + 1) + ") " +
                    uniqueProviders[i].getProfile().getFname().toUpperCase() + " " +
                    uniqueProviders[i].getProfile().getLname().toUpperCase() + " " +
                    uniqueProviders[i].getProfile().getDob() + " [" +
                    "credit amount: $" + String.format("%,.2f", creditAmounts[i]) + "]");
        }
        System.out.println("** end of list **");
    }

    /**
     * Prints all appointments.
     */
    private void printAllAppointments() {
        if (appointmentList.isEmpty()) {
            System.out.println("Schedule calendar is empty.");
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
            System.out.println("\n** List of appointments, ordered by date/time/provider.");
            for (Appointment appointment : appointmentsArray) {
                System.out.println(appointment);
            }
            System.out.println("** end of list **");
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
     * Prints all providers in the correct format.
     */
    private void printAllProviders() {
        Provider[] providerArray = new Provider[providers.size()];
        for (int i = 0; i < providers.size(); i++) {
            providerArray[i] = providers.get(i);  // Fill the array with providers
        }
        sortProvidersByName(providerArray);
        for (Provider provider : providerArray) {
            System.out.println(provider);
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
     * Print the list of appointments ordered by county (location), then date/timeslot.
     */
    private void printAppointmentsByLocation() {
        if (appointmentList.isEmpty()) {
            System.out.println("Schedule calendar is empty.");
            return;
        }
        sortByLocationLengthVersion();
        System.out.println("\n** List of appointments, ordered by county/date/time.");
        for (int i = 0; i < appointmentList.size(); i++) {
            System.out.println(appointmentList.get(i).toString());
        }
        System.out.println("** end of list **");
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
     * Helper method to parse a date from a string.
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
     * Helper method. Checks if the date is a date where there can be an appointment.
     * @param appointmentDate Requested appointment as a Date object
     * @param dateString Requested appointment as user inputted string
     * @return false and prints reason if not a valid date for appointment, true otherwise.
     */
    private boolean isValidAppointmentDate(Date appointmentDate, String dateString) {
        if (!appointmentDate.isValid()) {
            System.out.println("Appointment date: " + dateString + " is not a valid calendar date ");
            return false;
        }
        if (!appointmentDate.isTodayOrPast()) {
            System.out.println("Appointment date: " + dateString + " is today or a date before today.");
            return false;
        }
        if (!appointmentDate.withinSixMonths()) {
            System.out.println("Appointment date: " + dateString + " is not within six months.");
            return false;
        }
        if (appointmentDate.isWeekend()) {
            System.out.println("Appointment date: " + dateString + " is Saturday or Sunday.");
            return false;
        }
        return true;
    }

    /**
     * Checks if a DOB is a valid date to be a date of birth.
     * @param DOB Date of Birth as a Date object
     * @param DOBString Date of Birth as a String input from the user
     * @return true if it is a valid date to be a date of birth, false otherwise.
     */
    private boolean isValidDOB(Date DOB, String DOBString){
        if (!DOB.isValid()){
            System.out.println("Patient dob: " + DOBString + " is not a valid calendar date ");
            return false;
        }
        if (DOB.isTodayOrFuture()){
            System.out.println("Patient dob: " + DOBString + " is today or a date after today.");
            return false;
        }
        return true;
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
     * Prints the billing statements for each patient in the appointment list.
     *
     * @param appointments The list of appointments.
     */
    public void printBillingStatements(Appointment[] appointments) {
        if (appointmentList.isEmpty()) {
            System.out.println("Schedule calendar is empty.");
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
     * Collects unique patients and calculates their billing amounts.
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
     * Finds the index of the patient in the uniquePatients array.
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
     * Sorts the patients based on last name, first name, and date of birth.
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
     * Swaps the patients and their respective billing amounts.
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
     * Prints the sorted billing statements for each patient.
     *
     * @param uniquePatients The array of unique patients.
     * @param billingAmounts The array of billing amounts for each patient.
     * @param patientCount   The count of unique patients.
     */
    private void printSortedBillingStatements(Person[] uniquePatients, double[] billingAmounts, int patientCount) {
        DecimalFormat formatter = new DecimalFormat("#,##0.00");

        System.out.println("\n** Billing statement ordered by patient. **");
        for (int i = 0; i < patientCount; i++) {
            System.out.println("(" + (i + 1) + ") " + uniquePatients[i].getProfile() +
                    " [due: $" + formatter.format(billingAmounts[i]) + "]");
        }
        System.out.println("** end of list **");
    }

    /**
     * Clears appointment list after printing PS statement.
     */
    private void clearAppointmentList() {
        // Iterate over the appointmentList and remove each appointment
        while (appointmentList.size() > 0) {
            Appointment appointment = appointmentList.get(0);  // Always get the first element
            appointmentList.remove(appointment);  // Remove it by reference
        }
    }

    /**
     * Print the list of appointments ordered by patient profile, then date/timeslot.
     */
    private void printAppointmentsByPatient() {
        if (appointmentList.isEmpty()) {
            System.out.println("Schedule calendar is empty.");
            return;
        }
        Appointment[] appointmentsArray = new Appointment[appointmentList.size()];
        for (int i = 0; i < appointmentList.size(); i++) {
            appointmentsArray[i] = appointmentList.get(i);
        }
        sortAppointmentsByPatient(appointmentsArray);
        System.out.println("\n** Appointments ordered by patient/date/time **");
        for (int i = 0; i < appointmentsArray.length; i++) {
            System.out.println(appointmentsArray[i].toString());
        }
        System.out.println("** end of list **");
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
     * Method to run the ClinicManager. Reads and processes command lines.
     */
    public void run() {
        Scanner sc = new Scanner(System.in);
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String commandLine = scanner.nextLine().trim();
            if (commandLine.isEmpty()) {
                continue;
            }
            StringTokenizer tokenizer = new StringTokenizer(commandLine, ",");
            String command = tokenizer.nextToken();

            if (!isValidCommand(command)) {
                invalidCommand();
                continue;
            }

            command = command.toUpperCase();
            executeCommand(command, tokenizer, scanner);
        }
    }

}