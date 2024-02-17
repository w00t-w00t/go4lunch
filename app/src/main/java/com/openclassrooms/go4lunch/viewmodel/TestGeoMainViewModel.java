package com.openclassrooms.go4lunch.viewmodel;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.openclassrooms.go4lunch.MainApplication;
import com.openclassrooms.go4lunch.model.repository.LocationRepository;

public class TestGeoMainViewModel extends ViewModel {

    @NonNull
    private LocationRepository locationRepository;

    private final MediatorLiveData<String> gpsMessageLiveData = new MediatorLiveData<>();

    private final MutableLiveData<Boolean> hasGpsPermissionLiveData = new MutableLiveData<>();

    public TestGeoMainViewModel(Context context, @NonNull LocationRepository locationRepository) {
        this.locationRepository = locationRepository;

        LiveData<Location> locationLiveData = locationRepository.getLocationLiveData();

        gpsMessageLiveData.addSource(locationLiveData, location ->
                combine(location, hasGpsPermissionLiveData.getValue())
        );

        gpsMessageLiveData.addSource(hasGpsPermissionLiveData, hasGpsPermission ->
                combine(locationLiveData.getValue(), hasGpsPermission)
        );
    }

    private void combine(@Nullable Location location, @Nullable Boolean hasGpsPermission) {
        if (location == null) {
            if (hasGpsPermission == null || !hasGpsPermission) {
                gpsMessageLiveData.setValue("I am lost... Should I click the permission button ?!");
            } else {
                gpsMessageLiveData.setValue("Querying location, please wait for a few seconds...");
            }
        } else {
            gpsMessageLiveData.setValue("I am at coordinates (lat:" + location.getLatitude() + ", long:" + location.getLongitude() + ")");
        }
    }

    @SuppressLint("MissingPermission")
    public void refresh() {
        boolean hasGpsPermission = ContextCompat.checkSelfPermission(MainApplication.getApplication(), ACCESS_FINE_LOCATION) == PERMISSION_GRANTED;
        hasGpsPermissionLiveData.setValue(hasGpsPermission);

        if (hasGpsPermission) {
            locationRepository.startLocationRequest();
        } else {
            locationRepository.stopLocationRequest();
        }
    }

    public LiveData<String> getGpsMessageLiveData() {
        return gpsMessageLiveData;
    }

}
