package com.openclassrooms.go4lunch;

import android.app.Application;

/**
 * Main application class.
 * Used to provide the application instance.
 */
public class MainApplication extends Application {

    /**
     * Application instance.
     */
    private static Application sApplication;

    /**
     * Called when the application is starting, before any activity, service, or receiver objects (excluding content providers) have been created.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        sApplication = this;
    }

    /**
     * Get the application instance.
     * @return Application instance.
     */
    public static Application getApplication() {
        return sApplication;
    }
}