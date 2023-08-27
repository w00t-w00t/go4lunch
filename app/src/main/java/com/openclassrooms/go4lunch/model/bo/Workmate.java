package com.openclassrooms.go4lunch.model.bo;

import org.jetbrains.annotations.Nullable;

/**
 * Model object representing a workmate.
 * A workmate is a person working in the same company as the user of the app.
 */
public class Workmate {

    /** ID */
    private String idWorkmate;

    /** Name */
    private String name;

    /** Email */
    private String email;

    /** Notification */
    private Boolean isNotificationActive;

    /** Url picture
     * TODO : use proper URL type, in a real app
     */
    @Nullable
    private String urlPicture;

    /** Empty constructor */
    public Workmate() {
    }

    /** Constructor with all parameters */
    public Workmate(String idWorkmate, String name, String email, @Nullable String urlPicture, Boolean isNotificationActive) {
        this.idWorkmate = idWorkmate;
        this.name = name;
        this.email = email;
        this.urlPicture = urlPicture;
        this.isNotificationActive=isNotificationActive;
    }

    /** Constructor with all parameters, except the notification one */
    public Workmate(String idWorkmate, String name, String email, @Nullable String urlPicture) {
        this.idWorkmate = idWorkmate;
        this.name = name;
        this.email = email;
        this.urlPicture = urlPicture;
        this.isNotificationActive=true;
    }

    /** Getters */
    public String getName() {
        return name;
    }
    @Nullable
    public String getUrlPicture() {
        return urlPicture;
    }
    public String getEmail() {
        return email;
    }

    public String getIdWorkmate() {
        return idWorkmate;
    }

    public Boolean getIsNotificationActive() {
        return isNotificationActive;
    }

    /** Setters */
    public void setIdWorkmate(String idWorkmate) {
        this.idWorkmate = idWorkmate;
    }
    public void setName(String name) {
        this.name = name;
    }
    public void setUrlPicture(@Nullable String urlPicture) {
        this.urlPicture = urlPicture;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public void setIsNotificationActive(Boolean isNotificationActive) {
        this.isNotificationActive = isNotificationActive;
    }
}

