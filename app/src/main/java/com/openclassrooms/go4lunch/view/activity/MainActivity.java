package com.openclassrooms.go4lunch.view.activity;

import android.Manifest;
import android.os.Bundle;

import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.View;

import com.openclassrooms.go4lunch.databinding.ActivityMainBinding;

import com.openclassrooms.go4lunch.R;
import com.openclassrooms.go4lunch.model.bo.maps.ListRestaurant;
import com.openclassrooms.go4lunch.model.repository.RestaurantRepository;
import com.openclassrooms.go4lunch.viewmodel.TestGeoMainViewModel;
import com.openclassrooms.go4lunch.viewmodel.ViewModelFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/** MainActivity that includes core "tests" **/
public class MainActivity extends AppCompatActivity {

    /** View Binding **/
    private ActivityMainBinding binding;

    /** Test Google Maps API **/
    private void testGoogleMapsAPI(){

        String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
        String API_KEY = getString(R.string.google_maps_key);
        String CATEGORY = "restaurant";
        final int RADIUS = 1000;
        String GOOGLE_PLEX = "48.1159843,-1.7296427";

        Log.d("testGoogleMapsAPI", "Starting request with API KEY = " + API_KEY);

        RestaurantRepository restaurantRepository = RestaurantRepository.getInstance();
        Call<ListRestaurant> restaurants = restaurantRepository.getAllRestaurant(BASE_URL, GOOGLE_PLEX, RADIUS, CATEGORY, API_KEY);

        restaurants.enqueue(new Callback<ListRestaurant>() {
            @Override
            public void onResponse(Call<ListRestaurant> call, Response<ListRestaurant> response) {
                ListRestaurant listRestaurant = response.body();
                Log.d("testGoogleMapsAPI", listRestaurant.getResults().size() + "");
            }

            @Override
            public void onFailure(Call<ListRestaurant> call, Throwable t) {
                Log.e("testGoogleMapsAPI", "FAILURE !!!" + t.getMessage(), t);
            }
        });
    }

    /** Request GPS position as LiveData (must be inside a ViewModel) **/
    private TestGeoMainViewModel viewModel;

    private void testGPS(View view){

        // iniatialize ViewModel that includes GPS LiveData
        viewModel = new ViewModelProvider(this, ViewModelFactory.getInstance()).get(TestGeoMainViewModel.class);

        // Request GPS position appropriate permissions
        ActivityCompat.requestPermissions(
                this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                0
        );

        // Observe GPS position
        viewModel.getGPSStatus().observe(this, message -> {
            Log.d("testGPS", message.toString());
            binding.textView.setText(message.toString());
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        if(viewModel != null)
            viewModel.refresh();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);

        binding.fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Test Google Maps API
                // https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=48.1159843,-1.7296427&radius=1000&type=restaurant&key=YOUR_API_KEY
                testGoogleMapsAPI();

                // Check if GPS is enabled
                testGPS(view);

                // Display a message
                Snackbar.make(view, "Clicked!", Snackbar.LENGTH_LONG)
                        .setAnchorView(R.id.fab)
                        .setAction("Action", null).show();
            }
        });
    }

}