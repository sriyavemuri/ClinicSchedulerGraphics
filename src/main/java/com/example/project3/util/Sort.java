package com.example.project3.util;
import com.example.project3.clinicscheduler.Appointment;
import com.example.project3.clinicscheduler.Person;
import com.example.project3.clinicscheduler.Profile;

/**
 * Class for sorting appointments based on specified criteria.
 *
 * @author Sriya Vemuri, Zeel Patel
 */
public class Sort {

    /**
     * Sorts a list of appointments based on the specified key.
     *
     * @param list The list of appointments to be sorted.
     * @param key  The key to sort by ('d' for date, 'p' for provider).
     */
    public static void appointment(Appointment[] list, char key) {
        for (int i = 0; i < list.length - 1; i++) {
            for (int j = 0; j < list.length - i - 1; j++) {
                if (compareAppointments(list[j], list[j + 1], key) > 0) {
                    // Swap
                    Appointment temp = list[j];
                    list[j] = list[j + 1];
                    list[j + 1] = temp;
                }
            }
        }
    }

    /**
     * Compares two appointments based on the specified key.
     *
     * @param a1  The first appointment.
     * @param a2  The second appointment.
     * @param key The key to compare by ('d' for date, 'p' for provider).
     * @return A negative integer, zero, or a positive integer as the first
     *         argument is less than, equal to, or greater than the second.
     * @throws IllegalArgumentException if the sort key is invalid.
     */
    private static int compareAppointments(Appointment a1, Appointment a2, char key) {
        switch (key) {
            case 'd':
                return a1.getDate().compareTo(a2.getDate());
            case 'p':
                return getProviderFullName(a1.getProvider()).compareTo(getProviderFullName(a2.getProvider()));
            default:
                throw new IllegalArgumentException("Invalid sort key: " + key);
        }
    }

    /**
     * Retrieves the full name of the provider.
     *
     * @param provider The provider whose full name is to be retrieved.
     * @return The full name of the provider.
     */
    private static String getProviderFullName(Person provider) {
        Profile profile = provider.getProfile(); // Assuming Person has a getProfile() method
        return profile.getFname() + " " + profile.getLname();
    }
}
