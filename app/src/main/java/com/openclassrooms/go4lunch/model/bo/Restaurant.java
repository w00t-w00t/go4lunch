package com.openclassrooms.go4lunch.model.bo;

import org.jetbrains.annotations.Nullable;

import java.io.Serializable;

/**
 * Describe a Restaurant features
 */
public class Restaurant implements Serializable {

    /** ID */
    private String id;

    /** Name */
    private String name;

    /** Rating */
    private Float rating;

    /** Hour closed */
    private String hourClosed;

    /** Phone number */
    @Nullable
    private String phone;

    /** Type : coffee, cinema, bar, ... */
    @Nullable
    private String type;

    /** Url picture */
    @Nullable
    private String urlPicture;

    /** Web site */
    @Nullable
    private String webSite;

    /** Address */
    @Nullable
    private String address;

    /** Empty constructor */
    public Restaurant() {
        super();
    }

    /** Constructor with all parameters */
    public Restaurant(String id, String name, @Nullable String phone, Float rating, @Nullable String type, @Nullable String urlPicture, @Nullable String webSite, @Nullable String address, String hourClosed) {
        this();
        this.id = id;
        this.name = name;
        this.phone = phone;
        this.rating = rating;
        this.type = type;
        this.urlPicture = urlPicture;
        this.webSite = webSite;
        this.address = address;
        this.hourClosed = hourClosed;
    }

    /** GETTERS */
    public String getId() { return id; }

    public String getName() { return name; }

    @Nullable
    public String getAddress() { return address; }

    public String getHourClosed() { return hourClosed; }

    @Nullable
    public String getPhone() { return phone; }

    public Float getRating() { return rating; }

    @Nullable
    public String getType() { return type; }

    @Nullable
    public String getUrlPicture() { return urlPicture; }

    @Nullable
    public String getWebSite() { return webSite; }

    /** SETTERS */
    public void setId(String id) { this.id = id; }

    public void setName(String name) { this.name = name; }

    public void setPhone(@Nullable String phone) { this.phone = phone; }

    public void setRating(Float rating) { this.rating = rating; }

    public void setType(@Nullable String type) { this.type = type; }

    public void setUrlPicture(@Nullable String urlPicture) { this.urlPicture = urlPicture; }

    public void setWebSite(@Nullable String webSite) { this.webSite = webSite; }

    public void setAddress(@Nullable String address) { this.address = address; }

    public void setHourClosed(String hourClosed) { this.hourClosed = hourClosed; }

}
