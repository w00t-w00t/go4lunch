package com.openclassrooms.go4lunch.viewmodel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.annotation.SuppressLint;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.go4lunch.MainApplication;
import com.openclassrooms.go4lunch.model.bo.Lunch;
import com.openclassrooms.go4lunch.model.bo.Restaurant;
import com.openclassrooms.go4lunch.model.bo.Workmate;
import com.openclassrooms.go4lunch.model.bo.location.GPSStatus;
import com.openclassrooms.go4lunch.model.repository.LocationRepository;
import com.openclassrooms.go4lunch.model.repository.LunchRepository;

/**
 * ViewModel that includes GPS LiveData
 */
public class TestMainViewModel extends ViewModel {

    /**
     * LocationRepository
     */
    @NonNull
    private final LocationRepository locationRepository;

    /**
     * LunchRepository
     */
    private final LunchRepository lunchRepository = LunchRepository.getInstance();

    /**
     * LiveData that indicates if the app has GPS permission
     * MutableLiveData is a subclass of LiveData thats exposes the setValue and postValue methods
     * (the second one is thread safe), so you can dispatch a value to any active observers.
     */
    private final MutableLiveData<Boolean> hasGpsPermissionLiveData = new MutableLiveData<>();

    /**
     * Mediator LiveData that combines GPS location and permission
     * java.lang.Object
     *   ↳ android.arch.lifecycle.LiveData<T>
     *       ↳ android.arch.lifecycle.MutableLiveData<T>
     *           ↳ android.arch.lifecycle.MediatorLiveData<T>
     * Mediator LiveData can observe multiples LiveData objects (sources) and react to their
     * onChange events, this will give you control on when you want to propagate the event,
     * or do something in particular.
     */
    private final MediatorLiveData<GPSStatus> gpsMessageLiveData = new MediatorLiveData<>();

    /**
     * Constructor
     * @param locationRepository LocationRepository instance to get GPS location
     */
    public TestMainViewModel(@NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;

        // get the Location LiveData from the LocationRepository
        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();

        // add the locationLiveData and hasGpsPermissionLiveData to the MediatorLiveData
        gpsMessageLiveData.addSource(locationLiveData, location ->
                combine(location, hasGpsPermissionLiveData.getValue())
        );
        gpsMessageLiveData.addSource(hasGpsPermissionLiveData, hasGpsPermission ->
                combine(locationLiveData.getValue(), hasGpsPermission)
        );
    }

    /**
     * Refresh the GPS location
     * This method is called when the user wants to refresh the GPS location
     * or when the app has GPS permission
     */
    @SuppressLint("MissingPermission")
    public void refresh() {

        // check if the app has GPS permission
        boolean hasGpsPermission = ContextCompat.checkSelfPermission(
                MainApplication.getApplication(), ACCESS_FINE_LOCATION
        ) == PERMISSION_GRANTED;

        hasGpsPermissionLiveData.setValue(hasGpsPermission);

        // if the app has GPS permission, start the location request
        if (hasGpsPermission) {
            locationRepository.startLocationRequest();
        } else {
            // or else.. stop the location request
            locationRepository.stopLocationRequest();
        }
    }

    /**
     * Get the GPS status LiveData
     * @return LiveData<String> GPS message
     */
    public LiveData<GPSStatus> getGPSStatus() {
        return gpsMessageLiveData;
    }

    /**
     * Combine the GPS location and permission
     * @param location GPS location
     * @param hasGpsPermission GPS permission
     */
    private void combine(@Nullable Location location, @Nullable Boolean hasGpsPermission) {
        if (location == null) {
            if (hasGpsPermission == null || !hasGpsPermission) {
                gpsMessageLiveData.setValue(new GPSStatus(false, false));
            } else {
                gpsMessageLiveData.setValue(new GPSStatus(false, true));
            }
        } else {
            gpsMessageLiveData.setValue(new GPSStatus(location.getLongitude(),location.getLatitude()));
        }
    }

    /**
     * Allow lunch insert
     */
    public void insert(String dateLunch, Restaurant restaurantChoosed, Workmate workmate){
        lunchRepository.createLunch(dateLunch, restaurantChoosed, workmate);
    }

}
