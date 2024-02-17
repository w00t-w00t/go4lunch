package com.openclassrooms.go4lunch.model.repository;
import android.location.Location;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresPermission;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;

/**
 * Location repository to request gps updates and provide the current location.
 */
public class LocationRepository {

    /**
     * Location request interval in milliseconds.
     */
    private static final int LOCATION_REQUEST_INTERVAL_MS = 20_000;

    /**
     * Smallest displacement threshold in meters.
     */
    private static final float SMALLEST_DISPLACEMENT_METER = 50;

    /**
     * Fused location provider client, used to request location updates.
     */
    @NonNull
    private final FusedLocationProviderClient fusedClient;

    /**
     * Location live data, used to observe the current location.
     */
    @NonNull
    private final MutableLiveData<Location> locationMutableLiveData = new MutableLiveData<>(null);

    /**
     * Location callback, used to receive location updates.
     */
    private LocationCallback locationCallback;

    /**
     * Constructor.
     * @param fusedClient Fused location provider client.
     */
    public LocationRepository(@NonNull FusedLocationProviderClient fusedClient) {
        this.fusedClient = fusedClient;
    }

    /**
     * Get the location live data.
     * @return Location live data.
     */
    public LiveData<Location> getLocationLiveData() {
        return locationMutableLiveData;
    }

    /**
     * Start location request.
     * This method requires the ACCESS_COARSE_LOCATION or ACCESS_FINE_LOCATION permission.
     */
    @RequiresPermission(anyOf = {"android.permission.ACCESS_COARSE_LOCATION", "android.permission.ACCESS_FINE_LOCATION"})
    public void startLocationRequest() {

        // create a location callback
        armLocationCallback();

        // remove location updates if already requested
        fusedClient.removeLocationUpdates(locationCallback);

        // request location updates
        fusedClient.requestLocationUpdates(
                // create a location request
                LocationRequest.create()
                    // set the priority to high accuracy
                    .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                    // set the smallest displacement threshold
                    .setSmallestDisplacement(SMALLEST_DISPLACEMENT_METER)
                    // set the interval between location updates
                    // have an impact of power consumption / battery life
                    .setInterval(LOCATION_REQUEST_INTERVAL_MS),
                // provide the location callback
                locationCallback,
                // use the main looper
                Looper.getMainLooper()
        );
    }

    /**
     * Stop location request.
     */
    public void stopLocationRequest() {
        if (locationCallback != null) {
            // remove location updates
            fusedClient.removeLocationUpdates(locationCallback);
        }
    }

    /**
     * Arm the Location callback
     */
    private void armLocationCallback(){
        // arm the location callback only once
        if (locationCallback == null) {
            locationCallback = new LocationCallback() {
                @Override
                // onLocationResult will be called when a new location is received
                public void onLocationResult(@NonNull LocationResult locationResult) {
                    // get the last location
                    Location location = locationResult.getLastLocation();
                    // update the location live data
                    locationMutableLiveData.setValue(location);
                }
            };
        }
    }

}
