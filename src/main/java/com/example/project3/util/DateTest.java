package util;
import org.junit.Test;
import static org.junit.Assert.*;
import util.Date;

public class DateTest {

    @Test
    public void testIsValid_ValidDates() {
        Date validDate1 = new Date(2024, 2, 29); // Leap year
        Date validDate2 = new Date(2023, 12, 31); // End of the year

        assertTrue(validDate1.isValid());
        assertTrue(validDate2.isValid());
    }

    @Test
    public void testIsValid_InvalidDates() {
        Date invalidDate1 = new Date(2023, 2, 29); // Non-leap year
        Date invalidDate2 = new Date(2023, 4, 31); // April has 30 days
        Date invalidDate3 = new Date(2023, 13, 1); // Invalid month
        Date invalidDate4 = new Date(2023, 6, 0); // Invalid day (0)

        assertFalse(invalidDate1.isValid());
        assertFalse(invalidDate2.isValid());
        assertFalse(invalidDate3.isValid());
        assertFalse(invalidDate4.isValid());
    }
}