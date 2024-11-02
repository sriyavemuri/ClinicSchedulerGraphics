package com.example.project3.clinicscheduler;

import com.example.project3.util.Date;

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
}