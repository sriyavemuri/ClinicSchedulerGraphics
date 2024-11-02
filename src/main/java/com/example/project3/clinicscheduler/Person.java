package com.example.project3.clinicscheduler;

/**
 * Person object. Super Class to Patient and Provider.
 * @author Sriya Vemuri, Zeel Patel
 */
public class Person implements Comparable<Person> {
    protected Profile profile;

    /**
     * Constructor method for Person super class.
     * @param profile Profile of person as profile object.
     */
    public Person(Profile profile) {
        this.profile = profile;
    }

    /**
     * Getter Method for the profile of a person.
     * @return Profile object
     */
    public Profile getProfile() {
        return profile;
    }

    /**
     * Compares if two person objects are equal and represent the same person.
     * @param obj Person object
     * @return true if the same person, false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            Person person = (Person) obj;
            return this.profile.equals(person.getProfile());
        }
        return false;
    }

    /**
     * Compares two person objects.
     * @param other the object to be compared.
     * @return return 1 if this person object is greater than "other", return -1 if smaller; return 0 if they are equal.
     */
    @Override
    public int compareTo(Person other) {
        return this.profile.compareTo(other.getProfile());
    }

    /**
     * Represents the Person as a string.
     * @return person's profile as a string.
     */
    @Override
    public String toString() {
        return profile.toString();
    }
}
