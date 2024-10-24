package clinicscheduler;

import util.Date;

/**
 * Medical Record class. Keeps track of all objects made with the project that stores information.
 * @author Sriya Vemuri, Zeel Patel
 */
public class MedicalRecord {
    private Patient[] patients;
    private int size; // number of patient objects in the array

    /**
     * Constructor to initialize the patients array with an initial capacity of 4
     */
    public MedicalRecord() {
        patients = new Patient[4];
        size = 0;
    }

    /**
     * Helper method to increase the capacity of the array by 4
     */
    private void grow() {
        Patient[] newPatients = new Patient[patients.length + 4];
        for (int i = 0; i < size; i++) {
            newPatients[i] = patients[i];
        }
        patients = newPatients;
    }

    /**
     * Method to add a new patient to the record
     * @param newPatient new patient information as a Patient object
     */
    public void add(Patient newPatient) {
        if (size == patients.length) {
            grow(); // Resize the array if it's full
        }
        patients[size++] = newPatient; // Add the new patient and increment size
    }

    /**
     * Method to get the number of patients in the medical record
     * @return the size of the medical record
     */
    public int getSize() {
        return size;
    }

    /**
     * Obtains an isolated Patient object, given its index in the medical record.
     * @param index index of patient in medical record
     * @return Patient object
     */
    public Patient getPatient(int index) {
        if (index >= 0 && index < size) {
            return patients[index];
        }
        return null; // index is out of bounds
    }

    /**
     * Method to check if the medical record contains a specific patient
     * @param patient Patient object that user wants to see is in the medical record.
     * @return true if it's in the medical record, false otherwise.
     */
    public boolean contains(Patient patient) {
        for (int i = 0; i < size; i++) {
            if (patients[i].equals(patient)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Method to print all patients in the record.
     * This was used for debugging purposes.
     */
    public void printAllPatients() {
        for (int i = 0; i < size; i++) {
            System.out.println(patients[i]);
        }
    }

    /**
     * Given a patient's information as a Profile object, this will return the associated Patient object.
     * @param patientProfile patient's information as a Profile object.
     * @return associated Patient object.
     */
    public Patient findOrCreatePatient(Profile patientProfile) {
        for (int i = 0; i < size; i++) {
            if (patients[i].getProfile().equals(patientProfile)) {
                return patients[i];
            }
        }
        // If no patient is found, create a new Patient with the given profile
        Patient newPatient = new Patient(patientProfile, null);
        add(newPatient); // adds to the medical record
        return newPatient;
    }

    /**
     * Testbed main() to test the methods in this class.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        MedicalRecord record = new MedicalRecord();

        Patient p1 = new Patient(new Profile("Kernik", "Dave", new Date(2005, 4, 15)), null);
        Patient p2 = new Patient(new Profile("Sahil", "Sharma", new Date(2003, 7, 23)), null);
        Patient p3 = new Patient(new Profile("Rushi", "Patel", new Date(2001, 12, 9)), null);
        Patient p4 = new Patient(new Profile("Aryan", "Saxena", new Date(1980, 3, 18)), null);

        // Add patients to the record
        record.add(p1);
        record.add(p2);
        record.add(p3);
        record.add(p4);

        // Print all patients
        System.out.println("All patients in the record:");
        record.printAllPatients();

        // Check if a patient exists
        System.out.println("Contains Kernik Dave: " + record.contains(p2));

        // Get a patient by index
        System.out.println("Patient at index 1: " + record.getPatient(1));

        // Adding more patients to test array resizing
        Patient p5 = new Patient(new Profile("Sanjana", "Suresh", new Date(1995, 5, 30)), null);
        record.add(p5);
        System.out.println("Added another patient, current size: " + record.getSize());
        System.out.println("Patient at index 4: " + record.getPatient(4));
    }
}