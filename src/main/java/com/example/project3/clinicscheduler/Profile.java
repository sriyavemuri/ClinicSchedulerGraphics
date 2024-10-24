package clinicscheduler;

import util.Date;

/**
 * Represents the profile of a person, including their first name, last name, and date of birth.
 *
 * @author Sriya Vemuri, Zeel Patel
 */
public class Profile implements Comparable<Profile>{
    private String fname;
    private String lname;
    private Date dob;

    /**
     * Constructs a new Profile with the given first name, last name, and date of birth.
     *
     * @param fname   The first name of the person.
     * @param lname    The last name of the person.
     * @param dob The date of birth of the person.
     */
    public Profile(String fname, String lname, Date dob) {
        this.fname = fname;
        this.lname = lname;
        this.dob = dob;
    }

    /**
     * Gets the first name of the person.
     *
     * @return The first name of the person.
     */
    public String getFname() {
        return fname;
    }

    /**
     * Gets the last name of the person.
     *
     * @return The last name of the person.
     */
    public String getLname() {
        return lname;
    }

    /**
     * Gets the date of birth of the person.
     *
     * @return The date of birth of the person.
     */
    public Date getDob() {
        return dob;
    }

    /**
     * Changes the date of birth (DOB) to a given DOB
     *
     * @param dob DOB as a Date object
     */
    public void setDob(Date dob) {
        this.dob = dob;
    }

    /**
     * Tests to see if two profiles are the exact same/duplicates of each other.
     * @param obj The object to be compared.
     * @return return true if two profiles are the same; false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Profile) {
            Profile profile = (Profile) obj;
            return profile.fname.toLowerCase().equals(this.fname.toLowerCase()) &&
                    profile.lname.toLowerCase().equals(this.lname.toLowerCase()) &&
                    profile.dob.equals(this.dob);
        }
        return false;
    }

    /**
     * Return textual representation of Profile object as first_name last_name mm/dd/yyyy.
     * @return a string
     */
    @Override
    public String toString() {
        return (this.fname.substring(0, 1).toUpperCase() + this.fname.substring(1).toLowerCase()) + " " +
                (this.lname.substring(0, 1).toUpperCase() + this.lname.substring(1).toLowerCase()) + " " +
                this.dob.toString();
    }

    /**
     * Compares two profile objects based on the "key" valye.
     * @param profile the object to be compared.
     * @return return 1 if the profile if this profile object is greater than "profile", return -1 if smaller;
     * return 0 if they are equal.
     */
    @Override
    public int compareTo(Profile profile) {
        // Last names
        int lastNameComparison = this.lname.compareToIgnoreCase(profile.lname);
        if (lastNameComparison != 0) return Integer.signum(lastNameComparison);
        // First names
        int firstNameComparison = this.fname.compareToIgnoreCase(profile.fname);
        if (firstNameComparison != 0) return Integer.signum(firstNameComparison);
        // DOBs
        return Integer.signum(this.dob.compareTo(profile.dob));
    }
}

