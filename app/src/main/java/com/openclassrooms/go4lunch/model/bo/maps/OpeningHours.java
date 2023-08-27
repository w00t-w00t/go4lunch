package com.openclassrooms.go4lunch.model.bo.maps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

//Generated("jsonschema2pojo")
public class OpeningHours {

    @SerializedName("open_now")
    @Expose
    private Boolean openNow;

    public Boolean getOpenNow() {
        return openNow;
    }

    public void setOpenNow(Boolean openNow) {
        this.openNow = openNow;
    }

}
