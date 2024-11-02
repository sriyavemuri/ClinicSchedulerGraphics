package com.example.project3.clinicscheduler;
import com.example.project3.util.Date;

/**
 * Appointment object. Keeps track of date of appointment, the time of appointment, the patient,
 * and the provider responding to the appointment.
 * @author Sriya Vemuri, Zeel Patel
 */
public class Appointment implements Comparable<Appointment> {
    private Date date;
    private Timeslot timeslot;
    private Person patient;   // Changed from Profile to Person
    private Person provider;  // Changed from Provider to Person

    /**
     * Default constructor, also known as the no-argument constructor.
     * The default appointment is on January 1st, 2000 at 9 AM for Olivia Benson, born Feb 2nd, 1968, with Dr. Patel.
     */
    public Appointment() {
        this.date = new Date(2000, 01, 01);
        this.timeslot = new Timeslot(9,0);
        this.patient = new Patient(new Profile("Olivia", "Benson", new Date(1968, 2, 7)), null);
        this.provider = new Doctor(new Profile("Patel", "Patel", new Date(1968, 3, 7)), Location.EDISON, Specialty.FAMILY, "123456789");
    }

    /**
     * Parameterized Constructor of Appointment Object.
     * Overloading constructor.
     * @param date date in the format of a Date object.
     * @param timeslot time of appointment, based on preinputted Timeslots list.
     * @param patient patient of type Person (subclass Patient or others).
     * @param provider provider of type Person (subclass Provider).
     */
    public Appointment(Date date, Timeslot timeslot, Person patient, Person provider) {
        this.date = date;
        this.timeslot = timeslot;
        this.patient = patient;
        this.provider = provider;
    }

    /**
     * Obtains date from appointment object.
     * @return date object.
     */
    public Date getDate() {
        return date;
    }

    /**
     * Changes the date to the given date.
     * @param date new date of appointment.
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Obtains time of appointment.
     * @return timeslot of appointment.
     */
    public Timeslot getTimeslot() {
        return timeslot;
    }

    /**
     * Changes the timeslot to the given timeslot.
     * @param timeslot new time of appointment.
     */
    public void setTimeslot(Timeslot timeslot) {
        this.timeslot = timeslot;
    }

    /**
     * Obtains the patient for whom the appointment is for.
     * @return Patient object of type Person.
     */
    public Person getPatient() {
        return patient;
    }

    /**
     * Changes the patient for the appointment with the given Person.
     * @param patient new patient.
     */
    public void setPatient(Person patient) {
        this.patient = patient;
    }

    /**
     * Obtains the provider responding to the appointment.
     * @return provider object of type Person.
     */
    public Person getProvider() {
        return provider;
    }

    /**
     * Changes the provider responding to the appointment with given Person.
     * @param provider new provider responding to appointment.
     */
    public void setProvider(Person provider) {
        this.provider = provider;
    }

    /**
     * Tests if two appointments are duplicates of each other.
     * @param obj The object to be compared.
     * @return true if they are duplicates, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Appointment) {
            Appointment appointment = (Appointment) obj;
            return ((appointment.date.equals(this.date))
                    && (appointment.timeslot.equals(this.timeslot))
                    && (appointment.patient.equals(this.patient))
                    && (appointment.provider.equals(this.provider)));
        }
        return false;
    }

    /**
     * Returns a textual representation of the Appointment object.
     * @return a string in the format of mm/dd/yyyy timeslot Patient [Provider].
     */
    @Override
    public String toString() {
        // Format the output to match the expected structure
        return date.toString() + " " + timeslot.toString() + " " + patient.getProfile().toString() + " " + provider.toString();
    }

    /**
     * Compare two Appointment objects based on their "key" value.
     * @param appointment the appointment object to be compared.
     * @return return 1 if this appointment occurs after "appointment", return -1 if it occurs earlier;
     * 0 if they occur at the same time.
     */
    @Override
    public int compareTo(Appointment appointment) {
        // Compare dates first
        int dateComparison = this.date.compareTo(appointment.date);
        if (dateComparison != 0) return dateComparison;

        // Compare timeslots
        int timeslotComparison = this.timeslot.compareTo(appointment.timeslot);
        if (timeslotComparison != 0) return timeslotComparison;

        // Compare providers (using the profile comparison)
        int providerComparison = this.provider.getProfile().compareTo(appointment.provider.getProfile());
        if (providerComparison != 0) return providerComparison;

        // Compare patients' profiles (last name, first name, DOB) if providers are the same
        return this.patient.getProfile().compareTo(appointment.patient.getProfile());
    }

    /**
     * Obtains the service cost of the provider for the appointment
     * @return service cost as a double
     */
    public double getServiceCost() {
        if (provider instanceof Doctor) {
            Doctor doctor = (Doctor) provider;
            return doctor.getSpecialty().getCharge();  // Assuming `getCost()` returns the cost for the specialty.
        } else if (provider instanceof Technician) {
            Technician technician = (Technician) provider;
            return technician.getServiceCost();
        }
        return 0.0;  // If provider type is neither Doctor nor Technician.
    }

//    /**
//     * Testbed main() to test code just specifically within this class.
//     * @param args command line arguments.
//     */
//    public static void main(String[] args) {
//        Date date1 = new Date(2024, 7, 13);
//        Date date2 = new Date(2024, 12, 13);
//        Date date3 = new Date(2024, 12, 2);
//
//        Profile nehaProfile = new Profile("Neha", "Vemuri", new Date(2005, 1, 24));
//        Profile yashProfile = new Profile("Yash", "Krishnan", new Date(2003, 1, 29));
//
//        Patient nehaVemuri = new Patient(nehaProfile, null);
//        Patient yashKrishnan = new Patient(yashProfile, null);
//
//        Provider drPatel = new Doctor(new Profile("John", "Patel", new Date(1975, 5, 20)), Location.EDISON, Specialty.FAMILY, "123456789");
//        Provider drLim = new Doctor(new Profile("Rachael", "Lim", new Date(1980, 4, 10)), Location.BRIDGEWATER, Specialty.PEDIATRICIAN, "987654321");
//
//        Appointment appt1 = new Appointment(date1, new Timeslot(9, 0), nehaVemuri, drPatel);
//        Appointment appt2 = new Appointment(date2, new Timeslot(10, 0), yashKrishnan, drLim);
//
//        System.out.println("Appointment 1: " + appt1);
//        System.out.println("Appointment 2: " + appt2);
//    }
}
