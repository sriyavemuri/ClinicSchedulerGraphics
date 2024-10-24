package com.example.project3.clinicscheduler;

public class Person implements Comparable<Person> {
    protected Profile profile;

    public Person(Profile profile) {
        this.profile = profile;
    }

    public Profile getProfile() {
        return profile;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Person) {
            Person person = (Person) obj;
            return this.profile.equals(person.getProfile());
        }
        return false;
    }

    @Override
    public int compareTo(Person other) {
        return this.profile.compareTo(other.getProfile());
    }

    @Override
    public String toString() {
        return profile.toString();
    }
}
