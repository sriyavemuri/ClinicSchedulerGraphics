package clinicscheduler;

/**
 * Defines the specialties, and the cost of each visit based on specialty.
 * @author Sriya Vemuri, Zeel Patel
 */
public enum Specialty {
    /**
     * List of specialties and their respective costs.
     */
    FAMILY("Family", 250),
    PEDIATRICIAN("Pediatrician", 300),
    ALLERGIST("Allergist", 350);

    /**
     * Parameters for a specialty
     */
    private final String specialty;
    private final int charge;

    /**
     * Specialty Information
     * @param specialty specialty of provider
     * @param charge cost per visit
     */
    Specialty(String specialty, int charge){
        this.specialty = specialty;
        this.charge = charge;
    }

    /**
     * Obtain specialty.
     * @return specialty as a String.
     */
    public String getSpecialty() {
        return specialty;
    }

    /**
     * Obtain cost per visit for a specialty.
     * @return charge as integer.
     */
    public int getCharge() {
        return charge;
    }
}
