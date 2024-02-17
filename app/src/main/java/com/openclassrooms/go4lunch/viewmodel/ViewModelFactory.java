package com.openclassrooms.go4lunch.viewmodel;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.LocationServices;
import com.openclassrooms.go4lunch.MainApplication;
import com.openclassrooms.go4lunch.model.repository.LocationRepository;

public class ViewModelFactory implements ViewModelProvider.Factory {

    private static ViewModelFactory factory;

    private static Context context;

    public  static ViewModelFactory getInstance(Context context) {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();
                    ViewModelFactory.context=context;

                }
            }
        }
        return factory;
    }
    public static ViewModelFactory getInstance() {
        if (factory == null) {
            synchronized (ViewModelFactory.class) {
                if (factory == null) {
                    factory = new ViewModelFactory();

                }
            }
        }
        return factory;
    }


    private ViewModelFactory() {
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(ListRestoViewModel.class)) {
            return (T) new ListRestoViewModel(context);
        }
        if(modelClass.isAssignableFrom(TestMainViewModel.class)) {
            Application application = MainApplication.getApplication();
            Log.d("ViewModelFactory", "create: " + application);
            LocationRepository loc = new LocationRepository(
                    LocationServices.getFusedLocationProviderClient(
                            application
                    )
            );

            return (T) new TestMainViewModel(loc);
        }
        throw new IllegalArgumentException("Unknown ViewModel class");
    }

}