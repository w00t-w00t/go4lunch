package com.openclassrooms.go4lunch.model.repository;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.go4lunch.model.bo.Lunch;
import com.openclassrooms.go4lunch.model.bo.Restaurant;
import com.openclassrooms.go4lunch.model.bo.Workmate;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;

/**
 * LunchRepository is a Singleton class that provides methods to interact with the Lunch collection in Firestore
 */
public class LunchRepository {

    /**
     * The tag for the log messages
     */
    private static final String TAG = LunchRepository.class.getSimpleName();

    /**
     * The name of the collection in Firestore
     */
    private static final String COLLECTION_NAME = "lunch";

    /**
     * The path from the Lunch to the workmate id
     */
    private static final String LUNCH_WORKMATE_ID_FIELD = "workmates.idWorkmate";
    /**
     * The name of the field date lunch
     */
    private static final String LUNCH_DATE_LUNCH_FIELD = "dateLunch";
    /**
     * The name of the field in the document restaurantChosen
     */
    private static final String LUNCH_RESTAURANT_CHOSEN_NAME_FIELD = "restaurantChoosed.name";

    /**
     * Get the list of workmates who have already chosen a restaurant for today's lunch as LiveData
     */
    private final MutableLiveData<ArrayList<Workmate>> workmatesAlreadyChooseRestaurantForTodayLunch = new MutableLiveData<>();

    /**
     * Check if the current workmates has chosen a particular Restaurant for today
     */
    private final MutableLiveData<Boolean> thatRestaurantIsChosenForToday = new MutableLiveData<>();

    /**
     * Empty constructor
     */
    public LunchRepository() {
        super();
    }

    /**
     * Repository singleton
     */
    private static volatile LunchRepository instance;

    public static LunchRepository getInstance() {
        if (instance == null) {
            instance = new LunchRepository();
        }
        return instance;
    }

    /**
     * Get the Lunch collection
     */
    public static CollectionReference getLunchCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    /** Today */
    private static String toDay(){
        return Instant.now().truncatedTo(ChronoUnit.DAYS).toString();
    }

    /** Create a lunch */
    public void createLunch(Restaurant restaurantChoosed, Workmate workmate) {
        Lunch lunch = new Lunch(toDay(), restaurantChoosed, workmate);
        getLunchCollection().add(lunch);
    }

    /** Get THE today lunch for a given workmate, if it exists. As Task (for ALARM purpose) */
    public static Task<QuerySnapshot> getTodayLunchByWorkmateTask(String id_workmate) {
        return getLunchCollection()
                .whereEqualTo(LUNCH_WORKMATE_ID_FIELD, id_workmate)
                .whereEqualTo(LUNCH_DATE_LUNCH_FIELD, toDay())
                .get();
    }

    /** Get ALL the today lunch for a given restaurant, if it exists. As Task.  */
    public static Task<QuerySnapshot> getTodayLunchByRestaurantTask(Restaurant restaurant) {
        return getLunchCollection()
                .whereEqualTo(LUNCH_RESTAURANT_CHOSEN_NAME_FIELD, restaurant.getName())
                .whereEqualTo(LUNCH_DATE_LUNCH_FIELD, toDay())
                .get();
    }

    /** Check if the current workmate has chosen a particular Restaurant for today. As Task */
    public static Task<QuerySnapshot> getTodayLunchByRestaurantAndWorkmate(Restaurant restaurant, String user_id) {
        return getLunchCollection()
                .whereEqualTo(LUNCH_RESTAURANT_CHOSEN_NAME_FIELD, restaurant.getName())
                .whereEqualTo(LUNCH_WORKMATE_ID_FIELD, user_id)
                .get();
    }

    /** Get the today lunch for a given workmate, if it exists. As LiveData */
    public LiveData<Lunch> getTodayLunch(String id_workmate) {
        MutableLiveData<Lunch> todayLunch = new MutableLiveData<>();
        getTodayLunchByWorkmateTask(id_workmate)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    QuerySnapshot querySnapshot = task.getResult();
                    if (task.getResult().size() != 0) {
                        Log.e(TAG, "getTodayLunch: " + id_workmate + " booked for Lunch this restaurant " + querySnapshot.toObjects(Lunch.class).get(0).getChosenRestaurant().getName());
                        todayLunch.postValue(querySnapshot.toObjects(Lunch.class).get(0));
                    } else {
                        Log.e(TAG, "getTodayLunch: " + id_workmate + " hasn't book a restaurant for today lunch !");
                    }
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            });
        return todayLunch;
    }

    /** Get ALL the today lunch for a given restaurant, if it exists */
    public LiveData<ArrayList<Workmate>> getWorkmatesThatAlreadyChooseRestaurantForTodayLunchForThatRestaurant(Restaurant restaurant) {
        getTodayLunchByRestaurantTask(restaurant)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<Workmate> workmates = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        workmates.add(document.toObject(Lunch.class).getWorkmate());
                        Log.e(TAG, "documents " + document.toObject(Lunch.class).getWorkmate());
                    }
                    workmatesAlreadyChooseRestaurantForTodayLunch.setValue(workmates);
                } else {
                    Log.e(TAG, "Error getting documents: ", task.getException());
                }
            })
            .addOnFailureListener(e -> workmatesAlreadyChooseRestaurantForTodayLunch.postValue(null));
        return workmatesAlreadyChooseRestaurantForTodayLunch;
    }

    /** Check if the current workmates has chosen a particular Restaurant for today */
    public MutableLiveData<Boolean> checkIfCurrentWorkmateChoseThisRestaurantForLunch(Restaurant restaurant, String user_id) {
        getTodayLunchByRestaurantAndWorkmate(restaurant, user_id)
            .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        thatRestaurantIsChosenForToday.postValue(false);
                        if (task.getResult().size() > 0) {
                            thatRestaurantIsChosenForToday.postValue(true);
                        }

                    }
                }
            );
        return thatRestaurantIsChosenForToday;
    }

    /** Delete a lunch, by specifying the restaurant and the user_id */
    public void deleteLunch(Restaurant restaurant, String user_id) {
        getTodayLunchByRestaurantAndWorkmate(restaurant, user_id)
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                        Log.e(TAG, "lunch: " + document.toObject(Lunch.class).getChosenRestaurant().getName());
                    }
                } else {
                    Log.e(TAG, "Error delete documents: ", task.getException());
                }
            });
    }
}
