package com.openclassrooms.go4lunch.model.repository;

import com.openclassrooms.go4lunch.model.bo.maps.ListRestaurant;
import com.openclassrooms.go4lunch.model.service.RetrofitMapsApi;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/** The Restaurant repository, backed by retrofit browsing to Google Maps API */
public class RestaurantRepository {

    /** The singleton instance */
    private static volatile RestaurantRepository instance;

    /** The empty constructor */
    public RestaurantRepository() {
    }

    /**
     * Gets the singleton instance
     * @return the singleton instance
     */
    public static RestaurantRepository getInstance() {
        if(instance==null)
        {
            instance=new RestaurantRepository();
        }
        return instance;
    }

    /** Gets all the restaurants from the Google Maps API
     * @param url the prefix url of the Google Maps API
     * @param location the location to search (latitude,longitude)
     * @param radius the radius of the search
     * @param type the type of the search (restaurant, bar, etc.)
     * @param key the API key of the Google Maps API
     * @return the asynchronous initiated call of the list of restaurants
     */
    public Call<ListRestaurant> getAllRestaurant(String url, String location, int radius, String type, String key) {

        // Build retrofit HTTP client
        Retrofit retrofit=new Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

        // Convert json data to model class object, generated with jsonschema2pojo
        RetrofitMapsApi api=retrofit.create(RetrofitMapsApi.class);

        // Create call of model class and enqueue for processing
        Call<ListRestaurant> call=api.getAllRestaurant(location,radius,type,key);

        // Return the call
        return call;
    }
}
