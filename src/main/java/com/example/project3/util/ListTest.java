import org.junit.Test;
import static org.junit.Assert.*;
import util.List;
import util.Date;
import clinicscheduler.Provider;
import clinicscheduler.Doctor;
import clinicscheduler.Technician;
import clinicscheduler.Profile;
import clinicscheduler.Location;
import clinicscheduler.Specialty;

public class ListTest {

    @Test
    public void testAddDoctor() {
        List<Provider> providerList = new List<>();
        Profile profile = new Profile("Aakaash", "Salain", new Date(2003, 6, 19));
        Doctor doctor = new Doctor(profile, Location.EDISON, Specialty.FAMILY, "1234567890");
        providerList.add(doctor);
        assertEquals(1, providerList.size());
        assertTrue(providerList.contains(doctor));
    }

    @Test
    public void testAddTechnician() {
        List<Provider> providerList = new List<>();
        Profile profile = new Profile("Priyanshi", "Shah", new Date(2003, 2, 18));
        Technician technician = new Technician(profile, Location.BRIDGEWATER, 75);
        providerList.add(technician);
        assertEquals(1, providerList.size());
        assertTrue(providerList.contains(technician));
    }

    @Test
    public void testRemoveDoctor() {
        List<Provider> providerList = new List<>();
        Profile profile = new Profile("Yash", "Krishnan", new Date(2003, 1, 29));
        Doctor doctor = new Doctor(profile, Location.PRINCETON, Specialty.PEDIATRICIAN, "1234567890");
        providerList.add(doctor);
        providerList.remove(doctor);
        assertEquals(0, providerList.size());
        assertFalse(providerList.contains(doctor));
    }

    @Test
    public void testRemoveTechnician() {
        List<Provider> providerList = new List<>();
        Profile profile = new Profile("Rushi", "Patel", new Date(2003, 4, 17));
        Technician technician = new Technician(profile, Location.PISCATAWAY, 75);
        providerList.add(technician);
        providerList.remove(technician);
        assertEquals(0, providerList.size());
        assertFalse(providerList.contains(technician));
    }
}
