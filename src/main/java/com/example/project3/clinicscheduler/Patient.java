package com.example.project3.clinicscheduler;

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
}
