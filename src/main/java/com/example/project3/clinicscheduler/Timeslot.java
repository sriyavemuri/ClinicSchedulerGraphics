package com.example.project3.clinicscheduler;

/**
 * Timeslot class. Represents the available appointment slots during the day.
 * Slots are in 30-minute intervals, 6 in the morning and 6 in the afternoon.
 * Author: Sriya Vemuri, Zeel Patel
 */
public class Timeslot implements Comparable<Timeslot> {
    private static final int SLOT_DURATION_MINUTES = 30;

    private int hour;
    private int minute;

    /**
     * Constructor for Timeslot.
     *
     * @param hour   The hour of the timeslot.
     * @param minute The minute of the timeslot.
     */
    public Timeslot(int hour, int minute) {
        this.hour = hour;
        this.minute = minute;
    }

    /**
     * Compares this Timeslot with another based on the time (hour and minute).
     *
     * @param other The other Timeslot to compare.
     * @return Negative if this comes before the other, positive if after, 0 if the same.
     */
    @Override
    public int compareTo(Timeslot other) {
        if (this.hour != other.hour) {
            return Integer.compare(this.hour, other.hour);
        }
        return Integer.compare(this.minute, other.minute);
    }

    /**
     * Returns a string representation of the Timeslot in "HH:MM AM/PM" format.
     *
     * @return String representation of the timeslot.
     */
    @Override
    public String toString() {
        String period = (hour >= 12) ? "PM" : "AM";
        int displayHour = (hour > 12) ? (hour - 12) : (hour == 0 ? 12 : hour);

        // Adjust to avoid leading zero for hours
        return String.format("%d:%02d %s", displayHour, minute, period);
    }

    /**
     * Checks equality based on the hour and minute of the timeslot.
     *
     * @param obj The object to compare.
     * @return true if the timeslots have the same time, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Timeslot)) return false;
        Timeslot other = (Timeslot) obj;
        return this.hour == other.hour && this.minute == other.minute;
    }

    /**
     * Testbed main() to test code just specifically within this class.
     *
     * @param args Command line arguments.
     */
    public static void main(String[] args) {
        Timeslot slot1 = new Timeslot(9, 0); // 9:00 AM
        Timeslot slot2 = new Timeslot(11, 30); // 11:30 AM
        Timeslot slot3 = new Timeslot(14, 0); // 2:00 PM

        System.out.println(slot1); // Expected: 09:00 AM
        System.out.println(slot2); // Expected: 11:30 AM
        System.out.println(slot3); // Expected: 02:00 PM
    }
}