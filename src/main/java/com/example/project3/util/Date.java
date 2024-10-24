package com.example.project3.util;

import java.util.Calendar;

/**
 Date object. Includes ways to validate date, see if the date falls on a weekend, and compare the date.
 @author Zeel Patel, Sriya Vemuri
 */
public class Date implements Comparable<Date> {
    public static final int QUADRENNIAL = 4;
    public static final int CENTENNIAL = 100;
    public static final int QUATERCENTENNIAL = 400;
    private int year;
    private int month;
    private int day;

    /**
     * Default/no-argument constructor. Default date is Jan. 1st, 2000.
     */
    public Date() {
        this.year = 2000;
        this.month = 1;
        this.day = 1;
    }

    /**
     Parameterized Constructor. Creates a Date object.
     Overloading constructor.
     @param year year of date as an integer
     @param month month of date as an integer
     @param day day of date as an integer
     */
    public Date(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
    }

    /**
     * Helper method to get the maximum number of days in a given month and year
     * @param month Month as an integer (1-12)
     * @param year Year as an integer
     * @return Maximum days in the specified month
     */
    private int getMaxDaysInMonth(int month, int year) {
        int daysInMonth = 31;
        if (month == 4 || month == 6 || month == 9 || month == 11) {
            daysInMonth = 30;
        } else if (month == 2) {
            if (isLeapYear(year)) {
                daysInMonth = 29; // Leap year
            } else {
                daysInMonth = 28; // Non-leap year
            }
        }

        return daysInMonth; // Return the maximum days for the month
    }

    /**
     * Helper method. Checks if a given year is a leap year.
     * @param year as an integer
     * @return true if the year is a leap year, false otherwise.
     */
    private boolean isLeapYear(int year) {
        if (year % QUADRENNIAL == 0) {
            if (year % CENTENNIAL == 0) {
                return year % QUATERCENTENNIAL == 0;
            }
            return true;
        }
        return false;
    }

    /**
     * Tests to see if the date is a valid date
     * @return true if date is a valid date, false otherwise.
     */
    public boolean isValid() {
        if (this.year < 1900) {
            return false;
        }
        if (this.month < 1 || this.month > 12) {
            return false;
        }
        int maxDaysInMonth = getMaxDaysInMonth(this.month, this.year);
        return this.day >= 1 && this.day <= maxDaysInMonth;
    }

    /**
     * Checks if a date falls on the weekend
     * @return true if date is on a weekend, false otherwise.
     */
    public boolean isWeekend(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(this.year, this.month - 1, this.day);
        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        return (weekday == Calendar.SATURDAY || weekday == Calendar.SUNDAY);
    }

    /**
     * Checks if a date is today or before today
     * @return true if date is today or before today, false otherwise.
     */
    public boolean isTodayOrPast(){
        Calendar today = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.set(this.year, (this.month - 1), this.day);
        return date.after(today);
    }

    /**
     * Checks if a date falls within six months of today.
     * @return true if date does fall within six months of today, false otherwise.
     */
    public boolean withinSixMonths() {
        Calendar today = Calendar.getInstance();
        Calendar sixMonths = Calendar.getInstance();
        sixMonths.add(Calendar.MONTH, 6);
        Calendar date = Calendar.getInstance();
        date.set(this.year, this.month - 1, this.day);
        return !date.after(sixMonths) && !date.before(today);
    }

    /**
     * Checks if a date is today or in the future.
     * @return true if it is today or in the future, false otherwise.
     */
    public boolean isTodayOrFuture() {
        Calendar today = Calendar.getInstance();
        Calendar date = Calendar.getInstance();
        date.set(this.year, this.month - 1, this.day);
        return !date.before(today);
    }

    /**
     Obtains year from a Date object.
     @return year as an integer
     */
    public int getYear() {
        return year;
    }

    /**
     Changes the year to the given year
     @param year as an integer
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     Obtains month from Date object
     @return month as an integer
     */
    public int getMonth() {
        return month;
    }

    /**
     Changes the month to the given month
     @param month as an integer
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     Obtains day from Date object.
     @return day as an integer
     */
    public int getDay() {
        return day;
    }

    /**
     Changes the day to the given day
     @param day as an integer
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     Tests if two date objects are equal.
     @param obj The object to be compared.
     @return return true if two date objects are equal; return false otherwise.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Date) {
            Date date = (Date) obj;
            return date.year == (this.year) && date.month == (this.month) && date.day == (this.day);
        }
        return false;
    }

    /**
     Return textual representation of Date object as month/day/year.
     @return a string with date.
     */
    @Override
    public String toString() {
        return String.format("%d/%d/%04d", month, day, year);
    }

    /**
     Compares two date objects based on which occurs first
     @param date the object to be compared.
     @return return 1 is date object is later than "date", return -1 if it is earlier;
     return 0 if they are equal.
     */
    @Override
    public int compareTo(Date date) {
        if (date.year == this.year){
            if (date.month > this.month) {
                return -1;
            } else if (date.month < this.month) {
                return 1;
            } else {
                return Integer.compare(this.day, date.day);
            }
        } else if (date.year < this.year) {
            return 1;
        } else {
            return -1;
        }
    }

    /**
     * Testbed main() to test code just specifically within this class.
     * @param args command line arguments.
     */

    public static void main(String[] args) {
        testDaysInFeb_Leap();         // Test case 1: Leap year
        testDaysEndOfYear();          // Test case 2: End of the year
        testDaysInFeb_Nonleap();      // Test case 3: Non-leap year
        testDaysInApril();            // Test case 4: April has 30 days
        testMonth_OutOfRange();       // Test case 5: Invalid month
        testDaysWithInvalidDay();     // Test case 6: Invalid day (0 day)
    }

    /**
     * Test case #1
     */
    private static void testDaysInFeb_Leap() {
        Date validDate1 = new Date(2024, 02, 29); // Leap year
        boolean expectedOutput = true; // Expected output
        boolean actualOutput = validDate1.isValid(); // Actual output
        testResult(validDate1, expectedOutput, actualOutput); // Test the result
    }

    /**
     * Test case #2
     */
    private static void testDaysEndOfYear() {
        Date validDate2 = new Date(2021, 12, 31); // End of the year
        boolean expectedOutput = true; // Expected output
        boolean actualOutput = validDate2.isValid(); // Actual output
        testResult(validDate2, expectedOutput, actualOutput); // Test the result
    }

    /**
     * Test case #3
     */
    private static void testDaysInFeb_Nonleap() {
        Date invalidDate3 = new Date(2023, 02, 29); // Non-leap year
        boolean expectedOutput = false; // Expected output
        boolean actualOutput = invalidDate3.isValid(); // Actual output
        testResult(invalidDate3, expectedOutput, actualOutput); // Test the result
    }

    /**
     * Test case #4
     */
    private static void testDaysInApril() {
        Date invalidDate4 = new Date(2019, 04, 31); // April has 30 days
        boolean expectedOutput = false; // Expected output
        boolean actualOutput = invalidDate4.isValid(); // Actual output
        testResult(invalidDate4, expectedOutput, actualOutput); // Test the result
    }

    /**
     * Test case #5
     */
    private static void testMonth_OutOfRange() {
        Date invalidDate5 = new Date(2022, 13, 01); // Invalid month
        boolean expectedOutput = false; // Expected output
        boolean actualOutput = invalidDate5.isValid(); // Actual output
        testResult(invalidDate5, expectedOutput, actualOutput); // Test the result
    }

    /**
     * Test case #6
     */
    private static void testDaysWithInvalidDay() {
        Date invalidDate6 = new Date(2017, 06, 0); // Invalid day (0 day)
        boolean expectedOutput = false; // Expected output
        boolean actualOutput = invalidDate6.isValid(); // Actual output
        testResult(invalidDate6, expectedOutput, actualOutput); // Test the result
    }

    /**
     * Method to compare the expected and actual output and print the result.
     * @param date the Date object being tested
     * @param expected the expected output
     * @param actual the actual output
     */
    private static void testResult(Date date, boolean expected, boolean actual) {
        if (expected == actual) {
            System.out.println("Test passed for date: " + date + "\nExpected: " + expected + ", Actual: " + actual + "\n");
        } else {
            System.out.println("Test failed for date: " + date + ". Expected: " + expected + ", but got: " + actual + "\n");
        }
    }
    }