package clinicscheduler;

import org.junit.Test;
import static org.junit.Assert.*;
import util.Date;

public class ProfileTest {

    @Test
    public void testCompareTo() {
        Date dob1 = new Date(1998, 4, 10);
        Date dob2 = new Date(2000, 5, 22);
        Date dob3 = new Date(1999, 11, 15);
        Date dob4 = new Date(2001, 3, 30);
        Date dob5 = new Date(1997, 6, 18);
        Date dob6 = new Date(1998, 12, 9);
        Date dob7 = new Date(1998, 12, 9);

        Profile profile1 = new Profile("John", "Doe", dob1);
        Profile profile2 = new Profile("Jane", "Smith", dob2);
        Profile profile3 = new Profile("Alice", "Brown", dob3);
        Profile profile4 = new Profile("Bob", "Brown", dob4);
        Profile profile5 = new Profile("Charlie", "Davis", dob5);
        Profile profile6 = new Profile("David", "Wilson", dob6);
        Profile profile7 = new Profile("David", "Wilson", dob7);

        // Test cases: profile1 < profile2, profile3 < profile4, profile5 < profile6
        assertCompareTo(profile1, profile2, -1);
        assertCompareTo(profile3, profile4, -1);
        assertCompareTo(profile5, profile6, -1);

        // Test cases: profile2 > profile1, profile4 > profile3, profile6 > profile5
        assertCompareTo(profile2, profile1, 1);
        assertCompareTo(profile4, profile3, 1);
        assertCompareTo(profile6, profile5, 1);

        // Updated case: profile6 > profile7 (same name, later DOB)
        assertCompareTo(profile6, profile7, 0);
    }


    /**
     * Helper method to perform the assertion and print the result.
     *
     * @param p1            First profile object to compare.
     * @param p2            Second profile object to compare.
     * @param expectedResult Expected comparison result: -1, 0, or 1.
     */
    private void assertCompareTo(Profile p1, Profile p2, int expectedResult) {
        int result = p1.compareTo(p2);
        System.out.println("Comparing " + p1 + " to " + p2);
        System.out.println("Expected: " + expectedResult + ", Actual: " + result);

        if (expectedResult < 0) {
            assertTrue("Expected " + p1 + " to be less than " + p2, result < 0);
        } else if (expectedResult > 0) {
            assertTrue("Expected " + p1 + " to be greater than " + p2, result > 0);
        } else {
            assertEquals("Expected " + p1 + " to be equal to " + p2, 0, result);
        }

        System.out.println();
    }
}
