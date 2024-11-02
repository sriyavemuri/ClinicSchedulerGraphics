package com.example.project3.clinicscheduler;

import com.example.project3.util.Date;

/**
 * Abstract class representing a healthcare provider.
 * Inherits from the Person class and includes provider-specific information and methods.
 * Author: Sriya Vemuri, Zeel Patel
 */
public abstract class Provider extends Person {
    private Location location; // The location of the provider

    /**
     * Constructor for creating a Provider.
     *
     * @param profile Profile information for the provider.
     * @param location Location of the provider.
     */
    public Provider(Profile profile, Location location) {
        super(profile);
        this.location = location;
    }

    /**
     * Retrieves the location of the provider.
     *
     * @return The location of the provider.
     */
    public Location getLocation() {
        return this.location;
    }

    /**
     * Abstract method to rate the provider.
     *
     * @return An integer rating for the provider.
     */
    public abstract int rate();

    /**
     * Abstract method to get the service cost for the provider.
     *
     * @return The cost for the service provided by this provider.
     */
    public abstract double getServiceCost();

    /**
     * Checks if the provider is available at a given date and timeslot.
     *
     * @param date        The date to check for availability.
     * @param timeslot    The timeslot to check for availability.
     * @param appointments Array of existing appointments.
     * @return true if the provider is available; false otherwise.
     */
    public boolean isAvailable(Date date, Timeslot timeslot, Appointment[] appointments) {
        if (appointments == null || appointments.length == 0) {
            return true; // Provider is available if there are no appointments
        }
        for (Appointment appointment : appointments) {
            if (appointment == null) {
                continue; // Skip null entries in the appointments array
            }
            // Check if the appointment conflicts with the given date and timeslot
            if (appointment.getProvider().equals(this)
                    && appointment.getDate().equals(date)
                    && appointment.getTimeslot().equals(timeslot)) {
                return false; // Conflicting appointment found
            }
        }
        return true; // The provider is available
    }
}
