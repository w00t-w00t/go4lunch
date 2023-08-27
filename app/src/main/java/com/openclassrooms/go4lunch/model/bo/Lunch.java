package com.openclassrooms.go4lunch.model.bo;

/**
 * Model object representing a lunch.
 *
 * A lunch is a specific Restaurant chosen by a specific Workmate at a specific date.
 */
public class Lunch {

    /** Date Lunch, stored as String, because its a fake app */
    // TODO : change this to proper java.time.Instant type, in a real app
    private String dateLunch;

    /** Chosen Restaurant by a Workmate */
    private Restaurant chosenRestaurant;

    /** Workmate who chose the Restaurant */
    private Workmate workmate;

    /** Empty constructor */
    public Lunch() {
        super();
    }

    /** Constructor with all parameters */
    public Lunch(String dateLunch, Restaurant chosenRestaurant, Workmate workmate) {
        this();
        this.dateLunch = dateLunch;
        this.chosenRestaurant = chosenRestaurant;
        this.workmate = workmate;
    }

    /** Getters */
    public String getDateLunch() {
        return dateLunch;
    }
    public Restaurant getChosenRestaurant() {
        return chosenRestaurant;
    }
    public Workmate getWorkmate() { return workmate; }

    /** Setters */
    public void setDateLunch(String dateLunch) { this.dateLunch = dateLunch; }
    public void setChosenRestaurant(Restaurant chosenRestaurant) { this.chosenRestaurant = chosenRestaurant; }
    public void setWorkmate(Workmate workmate) { this.workmate = workmate; }

}
