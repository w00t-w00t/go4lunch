package com.openclassrooms.go4lunch.model.repository;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.openclassrooms.go4lunch.model.bo.Restaurant;
import com.openclassrooms.go4lunch.model.bo.Workmate;

import java.util.ArrayList;

public class WorkmateRepository {

    private static final String COLLECTION_NAME = "workmates";
    private static final String WORKMATES_LIKED_RESTAURANT_COLLECTION = "liked_restaurant";
    private static final String WORKMATES_LIKED_RESTAURANT_COLLECTION_NAME_FIELD = "name";
    private static volatile WorkmateRepository instance;
    private final MutableLiveData<ArrayList<Workmate>> listOfWorkmates = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLiked = new MutableLiveData<>();
    private MutableLiveData<Boolean> isNotificationActiveOfCurrentWorkmates = new MutableLiveData<>();


    public WorkmateRepository() {
    }

    public static WorkmateRepository getInstance() {

        if (instance == null) {
            instance = new WorkmateRepository();
        }
        return instance;
    }

    public FirebaseUser getCurrentWorkmate() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    // Get the Collection Reference
    public static CollectionReference getWorkmatesCollection() {
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // Create a Workmate in Firestore
    public void initWorkmate() {
        FirebaseUser workmate = getCurrentWorkmate();

        Log.i("WR", "createWorkmates: " + workmate.getDisplayName());

        if (workmate != null) {
            String urlPicture = (workmate.getPhotoUrl() != null) ? workmate.getPhotoUrl().toString() : null;
            String name = workmate.getDisplayName();
            String uid = workmate.getUid();
            String email = workmate.getEmail();
            Workmate workmatesToCreate = new Workmate(uid, name, email, urlPicture);
            this.getWorkmatesCollection().document(uid).set(workmatesToCreate);
        }
    }

    public MutableLiveData<ArrayList<Workmate>> getAllWorkmate() {
        getWorkmatesCollection()
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ArrayList<Workmate> workmates = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            workmates.add(document.toObject(Workmate.class));
                            Log.d("Error", "SIZE LIST RESTAURANT " + document.toObject(Workmate.class).getName());
                        }
                        Log.d("Error", "SIZE LIST RESTAURANT " + workmates.size());
                        listOfWorkmates.setValue(workmates);
                    } else {
                        Log.d("Error", "Error getting documents: ", task.getException());
                    }
                })
                .addOnFailureListener(e -> listOfWorkmates.postValue(null));
        return listOfWorkmates;

    }

    // Create a Like in Firestore
    public void addLikeRestaurant(Restaurant restaurant) {
        FirebaseUser workmates = getCurrentWorkmate();
        String uid = workmates.getUid();
        this.getWorkmatesCollection().document(uid).collection(WORKMATES_LIKED_RESTAURANT_COLLECTION).add(restaurant);
    }

    // Delete a Like in Firestore
    public void deleteLikeRestaurant(Restaurant restaurant) {
        FirebaseUser workmates = getCurrentWorkmate();
        String uid = workmates.getUid();
        this.getWorkmatesCollection().document(uid)
                .collection(WORKMATES_LIKED_RESTAURANT_COLLECTION)
                .whereEqualTo(WORKMATES_LIKED_RESTAURANT_COLLECTION_NAME_FIELD, restaurant.getName())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            document.getReference().delete();
                        }
                    } else {
                        Log.d("Error", "Error delete documents: ", task.getException());
                    }
                });

    }

    public MutableLiveData<Boolean> checkIfCurrentWorkmateLikeThisRestaurant(Restaurant restaurant) {

        FirebaseUser workmates = getCurrentWorkmate();
        String uid = workmates.getUid();
        getWorkmatesCollection()
                .document(uid)
                .collection(WORKMATES_LIKED_RESTAURANT_COLLECTION)
                .whereEqualTo(WORKMATES_LIKED_RESTAURANT_COLLECTION_NAME_FIELD, restaurant.getName())
                .get()
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                isLiked.postValue(false);
                                if (task.getResult().size() > 0) {
                                    isLiked.postValue(true);
                                }

                            }
                        }
                );
        return isLiked;
    }

    public void setIsNotificationActiveOfCurrentWorkmates(Boolean isNotificationActive) {
        FirebaseUser workmates = getCurrentWorkmate();
        if (workmates != null) {
            String urlPicture = (workmates.getPhotoUrl() != null) ? workmates.getPhotoUrl().toString() : null;
            String name = workmates.getDisplayName();
            String uid = workmates.getUid();
            String email = workmates.getEmail();
            System.out.println("email " + email);
            Workmate workmatesToUpdate = new Workmate(uid, name, email, urlPicture, isNotificationActive);
            this.getWorkmatesCollection().document(uid).set(workmatesToUpdate);
        }
    }

    public MutableLiveData<Boolean> getIsNotificationActiveOfCurrentWorkmates() {
        FirebaseUser workmates = getCurrentWorkmate();
        String uid = workmates.getUid();
        getWorkmatesCollection()
                .whereEqualTo("idWorkmate", uid)
                .get()
                .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                isNotificationActiveOfCurrentWorkmates.postValue(task.getResult().toObjects(Workmate.class).get(0).getIsNotificationActive());
                            } else {
                                Log.d("Error", "Error getting documents: ", task.getException());
                            }
                        }
                );
        return isNotificationActiveOfCurrentWorkmates;
    }

}
