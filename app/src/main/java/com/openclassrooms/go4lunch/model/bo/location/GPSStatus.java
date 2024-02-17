package com.openclassrooms.go4lunch.model.bo.location;

import androidx.annotation.NonNull;

/**
 * GPSStatus utility domain class
 */
public class GPSStatus {

    /**
     * Position longitude
     */
    private final Double longitude;

    /**
     * Position latitude
     */
    private final Double latitude;

    /**
     * GPS permission status
     */
    private final Boolean hasGpsPermission;

    /**
     * GPS querying status
     */
    private final Boolean querying;

    /**
     * Constructor
     * @param longitude Position longitude
     * @param latitude Position latitude
     */
    public GPSStatus(Double longitude, Double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
        this.hasGpsPermission = true;
        this.querying = false;
    }

    /**
     * Constructor
     * @param hasGpsPermission GPS permission status
     * @param querying GPS querying status
     */
    public GPSStatus(Boolean hasGpsPermission, Boolean querying) {
        this.hasGpsPermission = hasGpsPermission;
        this.querying = querying;
        this.longitude = null;
        this.latitude = null;
    }

    /** Getters */
    public Double getLongitude() {
        return longitude;
    }

    public Double getLatitude() {
        return latitude;
    }

    @NonNull
    public Boolean getHasGpsPermission() {
        return hasGpsPermission;
    }

    @NonNull
    public Boolean getQuerying() {
        return querying;
    }

    /** To String */
    @NonNull
    @Override
    public String toString() {
        return "GPSStatus{" +
                "longitude=" + longitude +
                ", latitude=" + latitude +
                ", hasGpsPermission=" + hasGpsPermission +
                ", querying=" + querying +
                '}';
    }
}
