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

/**
 * WorkmateRepository is a Singleton class that provides methods to interact with the Workmates collection in Firestore
 */
public class WorkmateRepository {

    /**
     * The tag for the log messages
     */
    private static final String TAG = LunchRepository.class.getSimpleName();

    /**
     * The name of the collection in Firestore for the workmates
     */
    private static final String WORKMATE_COLLECTION_NAME = "workmates";

    /**
     * The name of the workmates sub collection in Firestore for storing the liked restaurants for the workmates
     */
    private static final String LIKED_SUB_COLLECTION_NAME = "liked_restaurant";

    /**
     * The name of the field that describe the name of the restaurant in the document liked restaurant
     */
    private static final String LIKED_COLLECTION_NAME_FIELD = "name";

    /**
     * Get a list of workmates as LiveData
     */
    private final MutableLiveData<ArrayList<Workmate>> listOfWorkmates = new MutableLiveData<>();

    /**
     * Get a boolean to know if the current workmate has liked the restaurant, as LiveData
     */
    private MutableLiveData<Boolean> isLiked = new MutableLiveData<>();

    /**
     * Get a boolean to know if the current workmate has notification active, as LiveData
     */
    private MutableLiveData<Boolean> isNotificationActive = new MutableLiveData<>();

    /**
     * Empty constructor
     */
    public WorkmateRepository() {
        super();
    }

    /**
     * Repository singleton
     */
    private static volatile WorkmateRepository instance;
    public static WorkmateRepository getInstance() {
        if (instance == null) {
            instance = new WorkmateRepository();
        }
        return instance;
    }

    /**
     * Get the current workmate
     */
    public FirebaseUser getCurrentWorkmate() {
        return FirebaseAuth.getInstance().getCurrentUser();
    }

    /**
     * Sign out the current workmate
     */
    public Task<Void> signOut(Context context) {
        return AuthUI.getInstance().signOut(context);
    }

    /**
     * Select the collection of workmates from Firestore
     * @return the collection of workmates
     */
    public static CollectionReference getWorkmatesCollection() {
        return FirebaseFirestore.getInstance().collection(WORKMATE_COLLECTION_NAME);
    }

    /**
     * Get the workmate from Firestore
     */
    private Workmate getFirebaseUserAsWorkmate() {
        // Get the authenticated user from Firebase
        FirebaseUser user = getCurrentWorkmate();

        // The workmate to create
        Workmate workmate = null;
        if (user != null) {
            // Get the user's information
            String urlPicture = (user.getPhotoUrl() != null) ? user.getPhotoUrl().toString() : null;
            String name = user.getDisplayName();
            String uid = user.getUid();
            String email = user.getEmail();

            // Create a new workmate
            workmate = new Workmate(uid, name, email, urlPicture);
        } else {
            Log.e(TAG, "getFirebaseUserAsWorkmate: workmate is null");
        }
        return workmate;
    }

    /**
     * Initialize the workmate from Firebase AUTH to Firestore
     */
    public void createOrUpdateWorkmate() {
        // Get the authenticated user from Firebase
        Workmate workmate = getFirebaseUserAsWorkmate();
        // If the user is not null, create/update the workmate in Firestore
        if (workmate != null) {
            // Add/update the workmate to Firestore, using the USER_ID as the document ID
            getWorkmatesCollection().document(workmate.getIdWorkmate()).set(workmate);
        }
    }

    /**
     * Set the notification active for the current workmate
     * @param isNotificationActive the boolean to set
     */
    public void createOrUpdateWorkmate(Boolean isNotificationActive) {
        // Get the authenticated user from Firebase
        Workmate workmate = getFirebaseUserAsWorkmate();
        if (workmate != null) {
            workmate.setIsNotificationActive(isNotificationActive);
            // Add/update the workmate to Firestore, using the USER_ID as the document ID
            getWorkmatesCollection().document(workmate.getIdWorkmate()).set(workmate);
        }
    }

    /**
     * Get the list of ALL workmates from Firestore
     * @return the list of workmates as LiveData
     */
    public MutableLiveData<ArrayList<Workmate>> getAllWorkmates() {
        getWorkmatesCollection()
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    ArrayList<Workmate> workmates = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        workmates.add(document.toObject(Workmate.class));
                        Log.d(TAG, "SIZE LIST RESTAURANT " + document.toObject(Workmate.class).getName());
                    }
                    Log.d(TAG, "SIZE LIST RESTAURANT " + workmates.size());
                    listOfWorkmates.setValue(workmates);
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            })
            .addOnFailureListener(e -> listOfWorkmates.postValue(null));
        return listOfWorkmates;
    }

    /**
     * Add the restaurant as a liked restaurant for the current workmate in Firestore
     */
    public void addLikeRestaurant(Restaurant restaurant) {
        FirebaseUser workmates = getCurrentWorkmate();
        String uid = workmates.getUid();
        getWorkmatesCollection().document(uid).collection(LIKED_SUB_COLLECTION_NAME).add(restaurant);
    }

    /**
     * Delete the restaurant as a liked restaurant for the current workmate in Firestore
     */
    public void deleteLikeRestaurant(Restaurant restaurant) {
        FirebaseUser workmates = getCurrentWorkmate();
        String uid = workmates.getUid();
        getWorkmatesCollection().document(uid)
            .collection(LIKED_SUB_COLLECTION_NAME)
            .whereEqualTo(LIKED_COLLECTION_NAME_FIELD, restaurant.getName())
            .get()
            .addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getReference().delete();
                    }
                } else {
                    Log.d(TAG, "Error delete documents: ", task.getException());
                }
            });

    }

    /**
     * Check if the current workmate has liked the restaurant
     * @param restaurant the restaurant to check
     * @return a boolean as LiveData
     */
    public MutableLiveData<Boolean> checkIfCurrentWorkmateLikeThisRestaurant(Restaurant restaurant) {

        FirebaseUser workmates = getCurrentWorkmate();
        String uid = workmates.getUid();
        getWorkmatesCollection()
            .document(uid)
            .collection(LIKED_SUB_COLLECTION_NAME)
            .whereEqualTo(LIKED_COLLECTION_NAME_FIELD, restaurant.getName())
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

    /**
     * Get the notification active for the current workmate
     * @return a boolean as LiveData
     */
    public MutableLiveData<Boolean> getIsNotificationActive() {
        FirebaseUser workmates = getCurrentWorkmate();
        String uid = workmates.getUid();
        getWorkmatesCollection()
            .whereEqualTo("idWorkmate", uid)
            .get()
            .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        isNotificationActive.postValue(task.getResult().toObjects(Workmate.class).get(0).getIsNotificationActive());
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                }
            );
        return isNotificationActive;
    }

}
