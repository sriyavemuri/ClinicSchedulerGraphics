package com.example.project3.clinicscheduler;

/**
 * Enum class representing types of imaging services (X-ray, Ultrasound, CAT scan).
 * Author: Sriya Vemuri, Zeel Patel
 */
public enum Radiology {
    XRAY, ULTRASOUND, CATSCAN;

    /**
     * Retyrns String representation of radiology/type of imaging.
     * @return type of imaging as a string in all caps.
     */
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