package clinicscheduler;

/**
 * Defines the locations of the providers with the county and zip code.
 * @author Sriya Vemuri, Zeel Patel
 */
public enum Location {
    /**
     * List of Locations with their county and zip codes
     */
    BRIDGEWATER("Bridgewater", "Somerset County", "08807"),
    EDISON("Edison", "Middlesex County", "08817"),
    PISCATAWAY("Piscataway", "Middlesex County", "08854"),
    PRINCETON("Princeton", "Mercer County", "08542"),
    MORRISTOWN("Morristown", "Morris County", "07960"),
    CLARK("Clark", "Union County", "07066");

    /**
     * Parameters for a Location.
     */
    private final String location;
    private final String county;
    private final String zip;

    /**
     * Constructor. Location information
     * @param location Location as a string
     * @param county County that location is in as a string
     * @param zip zip code of location as a string
     */
    Location(String location, String county, String zip){
        this.location = location;
        this.county = county;
        this.zip = zip;
    }

    /**
     * Obtain the location as a String
     * @return location, as a String
     */
    public String getLocation() {
        return location;
    }

    /**
     * Obtain county as a String
     * @return county, as a String
     */
    public String getCounty() {
        return county;
    }

    /**
     * Obtain zip code as a String
     * @return zip code, a String
     */
    public String getZip() {
        return zip;
    }
}