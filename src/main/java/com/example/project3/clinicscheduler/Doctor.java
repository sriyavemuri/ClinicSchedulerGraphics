package clinicscheduler;

import util.Date;

/**
 * Represents a Doctor who is a provider with a specialty and NPI.
 * Author: Zeel Patel, Sriya Vemuri
 */
public class Doctor extends Provider {
    private Specialty specialty; // Encapsulated specialty
    private String npi; // National Provider Identifier

    /**
     * Constructor for Doctor object.
     *
     * @param profile   Profile of the doctor (contains first name, last name, and DOB).
     * @param location  Location where the doctor practices.
     * @param specialty Specialty of the doctor.
     * @param npi       National Provider Identifier of the doctor.
     */
    public Doctor(Profile profile, Location location, Specialty specialty, String npi) {
        super(profile, location);  // Pass Profile and Location to Provider constructor
        this.specialty = specialty;
        this.npi = npi;
    }

    /**
     * Rates the doctor based on their specialty.
     *
     * @return Rate of the doctor.
     */
    @Override
    public int rate() {
        switch (specialty) {
            case FAMILY:
                return 100;
            case PEDIATRICIAN:
                return 150;
            case ALLERGIST:
                return 200;
            default:
                return 0;
        }
    }

    @Override
    public double getServiceCost() {
        // Return service cost based on the doctor's specialty
        switch (specialty) {
            case FAMILY:
                return 100;
            case PEDIATRICIAN:
                return 150;
            case ALLERGIST:
                return 200;
            default:
                return 0;
        }
    }

    /**
     * Provides a textual representation of the Doctor object.
     *
     * @return String representation of the doctor.
     */
    @Override
    public String toString() {
        return String.format("[%s %s %s, %s, %s %s][%s, #%s]",
                getProfile().getFname().toUpperCase(),          // First name in uppercase
                getProfile().getLname().toUpperCase(),          // Last name in uppercase
                getProfile().getDob().toString(),               // Date of birth
                getLocation().name(),                           // Location name (e.g., EDISON)
                getLocation().getCounty().replace(" County", ""),  // County without "County"
                getLocation().getZip(),                     // Zip code
                specialty,                                      // Doctor's specialty
                npi);                                           // NPI with "#" prefix
    }

    /**
     * Gets the specialty of the doctor.
     *
     * @return Specialty of the doctor.
     */
    public Specialty getSpecialty() {
        return specialty;
    }

    /**
     * Gets the NPI of the doctor.
     *
     * @return NPI of the doctor.
     */
    public String getNpi() {
        return npi;
    }
}