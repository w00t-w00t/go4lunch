package com.openclassrooms.go4lunch.model.repository;

import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.openclassrooms.go4lunch.model.bo.Lunch;
import com.openclassrooms.go4lunch.model.bo.Restaurant;
import com.openclassrooms.go4lunch.model.bo.Workmate;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class LunchRepository {
    private static final String COLLECTION_NAME = "lunch";
    private static final String LUNCH_WORKMATE_ID_FIELD ="workmates.idWorkmate";
    private static final String LUNCH_DATE_LUNCH_FIELD="dateLunch";
    private static final String LUNCH_RESTAURANT_CHOOSED_NAME_FIELD="restaurantChoosed.name";

    private static volatile LunchRepository instance;
    private final MutableLiveData<ArrayList<Workmate>> workmatesAlreadyChooseRestaurantForTodayLunch= new MutableLiveData<>();
    private final MutableLiveData<Boolean> todayLunchIschoosed=new MutableLiveData<>();

    public LunchRepository() {
    }

    public static LunchRepository getInstance() {
        if(instance==null)
        {
            instance=new LunchRepository();
        }
        return instance;
    }
    // Get the Collection Reference
    public static CollectionReference getLunchCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    public void createLunch(String dateLunch, Restaurant restaurantChoosed, Workmate workmate) {
        // Create the Lunch object
        Lunch lunch = new Lunch(dateLunch, restaurantChoosed, workmate);
        // Store Lunch to Firestore
        getLunchCollection().add(lunch);
    }

    public MutableLiveData<Lunch> getTodayLunch(String idw) {
        MutableLiveData<Lunch> todayLunch = new MutableLiveData<>();
        Date today= Calendar.getInstance().getTime();
        today.setHours(13);
        today.setMinutes(0);
        today.setSeconds(0);
        getLunchCollection()
              .whereEqualTo(LUNCH_WORKMATE_ID_FIELD,idw)
              .whereEqualTo(LUNCH_DATE_LUNCH_FIELD, today.toString())
        .get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                QuerySnapshot querySnapshot = task.getResult();
                if(task.getResult().size()!=0) {
                    Log.e("TAG_GET_TODAY_LUNCH", "getTodayLunch: "+ idw+" booked for Lunch this restaurant "+querySnapshot.toObjects(Lunch.class).get(0).getChosenRestaurant().getName());
                    todayLunch.postValue(querySnapshot.toObjects(Lunch.class).get(0));
                }
                else{  Log.e("TAG_GET_TODAY_LUNCH", "getTodayLunch: "+ idw+" hasn't book a restaurant for today lunch !");
                }
            } else {
                Log.e("Error", "Error getting documents: ", task.getException());
            }
        });
        return todayLunch;
    }
    public static Task<QuerySnapshot> getTodayLunch2(String idw) {
        Date today= Calendar.getInstance().getTime();
        today.setHours(13);
        today.setMinutes(0);
        today.setSeconds(0);
       return  getLunchCollection()
                .whereEqualTo(LUNCH_WORKMATE_ID_FIELD,idw)
                .whereEqualTo(LUNCH_DATE_LUNCH_FIELD, today.toString())
                .get();

    }
    public static Task<QuerySnapshot> getTodayLunchByRestaurant(Restaurant restaurant){
        Date today= Calendar.getInstance().getTime();
        today.setHours(13);
        today.setMinutes(0);
        today.setSeconds(0);
       return getLunchCollection().whereEqualTo(LUNCH_RESTAURANT_CHOOSED_NAME_FIELD,restaurant.getName())
              .whereEqualTo(LUNCH_DATE_LUNCH_FIELD,today.toString())
                .get();
    }

    public MutableLiveData<ArrayList<Workmate>> getWorkmatesThatAlreadyChooseRestaurantForTodayLunch(Restaurant restaurant){
       Date today= Calendar.getInstance().getTime();
        today.setHours(13);
        today.setMinutes(0);
        today.setSeconds(0);
        getLunchCollection()
                .whereEqualTo(LUNCH_RESTAURANT_CHOOSED_NAME_FIELD,restaurant.getName())
               .whereEqualTo(LUNCH_DATE_LUNCH_FIELD,today.toString())
                .get()
                .addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ArrayList<Workmate> workmates = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    workmates.add(document.toObject(Lunch.class).getWorkmate());
                    Log.e("Error", "documents "+document.toObject(Lunch.class).getWorkmate());
                }

                workmatesAlreadyChooseRestaurantForTodayLunch.setValue(workmates);
            } else {
                Log.e("Error", "Error getting documents: ", task.getException());
            }
        })
        .addOnFailureListener(e -> workmatesAlreadyChooseRestaurantForTodayLunch.postValue(null));
        return workmatesAlreadyChooseRestaurantForTodayLunch;
    }
    public MutableLiveData<Boolean> checkIfCurrentWorkmateChoseThisRestaurantForLunch(Restaurant restaurant,String uid) {
        getLunchCollection()
                .whereEqualTo(LUNCH_RESTAURANT_CHOOSED_NAME_FIELD, restaurant.getName())
              .whereEqualTo(LUNCH_WORKMATE_ID_FIELD,uid)
                .get()
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                todayLunchIschoosed.postValue(false);
                                if (task.getResult().size() > 0) {
                                    todayLunchIschoosed.postValue(true);
                                }

                            }
                        }
                );
        return todayLunchIschoosed;
    }
    public static Task<QuerySnapshot> checkIfCurrentWorkmateChoseThisRestaurantForLunch2(Restaurant restaurant, String uid) {
        return getLunchCollection()
                .whereEqualTo(LUNCH_RESTAURANT_CHOOSED_NAME_FIELD, restaurant.getName())
                .whereEqualTo(LUNCH_WORKMATE_ID_FIELD,uid)
                .get();
    }


    public void deleteLunch(Restaurant restaurant, String currentUid) {
        getLunchCollection()
                .whereEqualTo(LUNCH_RESTAURANT_CHOOSED_NAME_FIELD, restaurant.getName())
                .whereEqualTo(LUNCH_WORKMATE_ID_FIELD,currentUid)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                            Log.e("delete ", "lunch: "+ document.toObject(Lunch.class).getChosenRestaurant().getName());
                        }
                    }
                    else {
                        Log.e("Error", "Error delete documents: ", task.getException());
                    }
                });
    }
}
