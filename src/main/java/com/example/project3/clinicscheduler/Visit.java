package com.example.project3.clinicscheduler;

import com.example.project3.util.Date;

/**
 * Defines a node in a singly linked list that maintains the list of completed appointments aka visits.
 * @author Zeel Patel, Sriya Vemuri
 */
public class Visit {
    private Appointment appointment;
    private Visit next;

    /**
     * Creates a Visit object.
     * @param appointment completed appointment object
     * @param next next Visit Node in linked list
     */
    public Visit(Appointment appointment, Visit next) {
        this.appointment = appointment;
        this.next = next;
    }

    /**
     * Obtains Appointment from Visit Object
     * @return Appointment Object
     */
    public Appointment getAppointment() {
        return appointment;
    }

    /**
     * Changes appointment to given appointment
     * @param appointment given appointment
     */
    public void setAppointment(Appointment appointment) {
        this.appointment = appointment;
    }

    /**
     * Gets the next Visit node in the linked list of Visit nodes.
     * @return returns the next Visit node. null if there is none.
     */
    public Visit getNext() {
        return next;
    }

    /**
     * Changes the next Visit node in the linked list to the given Visit node
     * @param next the Visit node to be set as the next node in the linked list
     */
    public void setNext(Visit next) {
        this.next = next;
    }
}