package clinicscheduler;

/**
 * Enum class representing types of imaging services (X-ray, Ultrasound, CAT scan).
 * Author: Sriya Vemuri, Zeel Patel
 */
public enum Radiology {
    XRAY, ULTRASOUND, CATSCAN;

    @Override
    public String toString() {
        switch (this) {
            case XRAY:
                return "XRAY";
            case ULTRASOUND:
                return "ULTRASOUND";
            case CATSCAN:
                return "CATSCAN";
            default:
                return super.toString();
        }
    }
}