package com.openclassrooms.go4lunch.model.service;

import com.openclassrooms.go4lunch.model.bo.maps.ListRestaurant;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Retrofit API Call to the Google Maps API
 */
public interface RetrofitMapsApi
{

    /** API Call to the Google Maps API Near By Search */
    @GET("nearbysearch/json")
    Call<ListRestaurant> getAllRestaurant(
        // latitude and longitude
        @Query("location") String Location,
        // radius in meters
        @Query("radius") int  radius,
        // type of place (restaurant)
        @Query("type") String Type,
        // API key
        @Query("key") String KeyMap
    );

}
