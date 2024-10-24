package com.example.project3.clinicscheduler;

import com.example.project3.util.Date;

/**
 * Technician class. Represents a technician who works in a medical facility.
 * Author: Sriya Vemuri, Zeel Patel
 */
public class Technician extends Provider {
    private int ratePerVisit; // Charging rate per visit

    /**
     * Creates a Technician object.
     *
     * @param profile      Profile of the technician.
     * @param location     Work location of the technician.
     * @param ratePerVisit Charging rate per visit.
     */
    public Technician(Profile profile, Location location, int ratePerVisit) {
        super(profile, location);  // Pass Profile and Location to Provider constructor
        this.ratePerVisit = ratePerVisit; // Set the rate per visit
    }

    /**
     * Returns the rate for the technician's services.
     *
     * @return the rate as an int.
     */
    @Override
    public int rate() {
        return ratePerVisit; // Return the technician's specific rate
    }

    @Override
    public double getServiceCost() {
        // Return the rate per visit as the service cost for the technician
        return ratePerVisit;
    }

    public boolean isAvailable(Date date, Timeslot timeslot, Appointment[] appointments) {
        if (appointments == null) {
            return true; // Technician is available if there are no appointments
        }
        for (int i = 0; i < appointments.length; i++) {
            Appointment appointment = appointments[i];
            if (appointment != null &&
                    appointment.getProvider().equals(this) &&
                    appointment.getDate().equals(date) &&
                    appointment.getTimeslot().equals(timeslot)) {
                return false; // Technician is not available if they already have an appointment at the same timeslot
            }
        }
        return true; // Technician is available
    }


    /**
     * Returns a string representation of the technician object.
     *
     * @return Technician's full name, location, and rate.
     */
    @Override
    public String toString() {
        return String.format("[%s %s %s, %s, %s %s][rate: $%.2f]",
                getProfile().getFname().toUpperCase(),
                getProfile().getLname().toUpperCase(),
                getProfile().getDob().toString(),  // Assuming Date has a proper toString() method
                getLocation().name(),              // Location name (e.g., BRIDGEWATER)
                getLocation().getCounty().replace(" County", ""),         // County (e.g., Somerset)
                getLocation().getZip(),        // Zip code (e.g., 08807)
                (double) ratePerVisit);            // Rate formatted to two decimal places
    }
}