package com.example.project3.clinicscheduler;
import com.example.project3.util.Date;

/**
 * Maintains a place to store a patient's profile, a list of there visits,
 * and the total amount of money a patient has been charged since being a patient.
 * @author Sriya Vemuri, Zeel Patel
 */
public class Patient extends Person {
    private Visit visits; // A linked list of visits (completed appointments)

    /**
     * Creating a new Patient object, given a patient's profile, and a list of their visits.
     * @param profile profile of patient
     * @param visits visits linked list associated with patient
     */
    public Patient(Profile profile, Visit visits) {
        super(profile);
        this.visits = visits;
    }

    /**
     * Obtains patient profile from Patient Object.
     * @return patient profile as Profile object.
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * Changes the patient's profile to the given profile.
     * @param profile given Profile
     */
    public void setProfile(Profile profile) {
        this.profile = profile;
    }

    /**
     * Obtains the Visits linked list for a specific Patient
     * @return Visit Linked list
     */
    public Visit getVisits() {
        return visits;
    }

    /**
     * Changes the Visits linked list to the given Visits linked list
     * @param visits given Visit linked list
     */
    public void setVisits(Visit visits) {
        this.visits = visits;
    }

    /**
     * Adds Visit node to Visit linked list, given an appointment.
     * @param appointment appointment information
     */
    public void addVisit(Appointment appointment) {
        Visit newVisit = new Visit(appointment, null);
        if (visits == null) {
            visits = newVisit;
        } else {
            Visit current = visits;
            while (current.getNext() != null) {
                current = current.getNext();
            }
            current.setNext(newVisit);
        }
    }

    /**
     * Removes visit from visit linked list when appointment is cancelled.
     * @param appointmentToCancel information on appointment that is cancelled.
     */
    public void removeVisit(Appointment appointmentToCancel) {
        if (visits == null) {
            return;
        }

        if (visits.getAppointment().equals(appointmentToCancel)) {
            visits = visits.getNext();
            return;
        }
        Visit current = visits;
        Visit previous = null;
        while (current != null) {
            if (current.getAppointment().equals(appointmentToCancel)) {
                previous.setNext(current.getNext());
                return;
            }
            previous = current;
            current = current.getNext();
        }
    }

    /**
     * Checks if two patient objects are duplicates of each other
     * @param obj The object to be compared.
     * @return return true if two patient objects are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Patient) {
            Patient patient = (Patient) obj;
            return this.profile.equals(patient.profile);
        }
        return false;
    }

    /**
     * Print's patients name, given patient object.
     * @return patient's full name as a string.
     */
    @Override
    public String toString() {
        return "Patient: " + profile.getFname() + " " + profile.getLname();
    }

//    /**
//     * Compares two patient objects based on their profile.
//     *
//     * @param patient the patient to be compared.
//     * @return an integer value indicating the order of comparison
//     */
//    @Override
//    public int compareTo(Patient patient) {
//        return this.profile.compareTo(patient.profile); // Assuming Profile class implements Comparable
//    }

    /**
     * Testbed main() to test the methods within this class alone.
     * @param args command line arguments.
     */
//    public static void main(String[] args) {
//        Date dob1 = new Date(1989, 12, 13);
//        Date dob2 = new Date(1990, 5, 10);
//        Profile profile1 = new Profile("Lalain", "Javaid", dob1);
//        Profile profile2 = new Profile("Parshva", "Mehta", dob2);
//
//        // Create Appointments for the visits
//        Appointment appt1 = new Appointment(new Date(2024, 7, 13), Timeslot.SLOT1, profile1, Provider.PATEL);
//        Appointment appt2 = new Appointment(new Date(2024, 7, 14), Timeslot.SLOT2, profile2, Provider.TAYLOR);
//
//        // Create Visits with the appointments
//        Visit visit1 = new Visit(appt1, null);
//        Visit visit2 = new Visit(appt2, null);
//
//        // Create Patients with profiles and their first visit
//        Patient patient1 = new Patient(profile1, visit1);
//        Patient patient2 = new Patient(profile2, visit2);
//
//        // Test cases
//        System.out.println("Charge for patient1: " + patient1.charge()); // This will calculate based on the single visit added in the constructor
//        System.out.println("Charge for patient2: " + patient2.charge());
//
//        // Edge Case: Patient with no visits (creating a patient with a null visit)
//        Patient patient3 = new Patient(new Profile("Rohit", "Boga", new Date(1992, 1, 1)), null);
//        System.out.println("Charge for patient3 (no visits): " + patient3.charge()); // Expected: 0
//
//        // Edge Case: Equal patients (since both have the same profile)
//        Patient patient4 = new Patient(profile1, visit1);
//        System.out.println("patient1 equals patient4: " + patient1.equals(patient4)); // Expected: true
//
//        // Edge Case: Compare patients based on profiles
//        System.out.println("Comparing patient1 and patient2: " + patient1.compareTo(patient2)); // Expected: < 0 based on profile comparison
//    }
}
