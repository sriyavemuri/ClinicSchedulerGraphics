package com.example.project3.clinicscheduler;
import com.example.project3.util.Date;

/**
 * Imaging class. Represents an imaging appointment that extends the Appointment class.
 * It includes a Radiology room (X-ray, Ultrasound, CAT scan).
 * @author Sriya Vemuri, Zeel Patel
 */
public class Imaging extends Appointment {
    private Radiology room;

    /**
     * Constructor for Imaging appointment.
     *
     * @param date Date of the appointment.
     * @param timeslot Timeslot of the appointment.
     * @param patient Person object representing the patient.
     * @param provider Person object representing the technician.
     * @param room Type of imaging service (X-ray, Ultrasound, CAT scan).
     */
    public Imaging(Date date, Timeslot timeslot, Person patient, Person provider, Radiology room) {
        super(date, timeslot, patient, provider);  // Calls the Appointment constructor
        this.room = room;
    }

    /**
     * Gets the imaging room type (X-ray, Ultrasound, CAT scan).
     *
     * @return room type.
     */
    public Radiology getRoom() {
        return room;
    }

    /**
     * Returns a string representation of the Imaging appointment.
     *
     * @return a string in the format of "Imaging Appointment: mm/dd/yyyy timeslot Patient [Provider] Room".
     */
    @Override
    public String toString() {
        return super.toString() + "[" + room.toString() + "]";
    }

    /**
     * Checks if two Imaging appointments are equal.
     *
     * @param obj the object to compare.
     * @return true if both Imaging appointments are equal, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Imaging) {
            Imaging other = (Imaging) obj;
            return super.equals(other) && this.room == other.room;
        }
        return false;
    }
}

