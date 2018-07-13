package model;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.List;

public class FoodStore implements Serializable{
    private String id;
    private String placeID;
    private String address;
    private String phonenumber;
    private String inernationalPhonenumber;
    private String iconUrl;
    private String openNow;
    private List<String> openTime;
    private LatLng location;
    private String rating;
    private String website;
    private String name;

    public FoodStore() {
    }

    public FoodStore(String id, String address, String phonenumber, String iconUrl, String openNow,
                     List<String> openTime, String rating, String website, String name, String inernationalPhonenumber) {
        this.id = id;
        this.address = address;
        this.phonenumber = phonenumber;
        this.iconUrl = iconUrl;
        this.openNow = openNow;
        this.openTime = openTime;
        this.rating = rating;
        this.website = website;
        this.name = name;
        this.inernationalPhonenumber = inernationalPhonenumber;
    }

    public String getInernationalPhonenumber() {
        return inernationalPhonenumber;
    }

    public void setInernationalPhonenumber(String inernationalPhonenumber) {
        this.inernationalPhonenumber = inernationalPhonenumber;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPlaceID() {
        return placeID;
    }

    public void setPlaceID(String placeID) {
        this.placeID = placeID;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getOpenNow() {
        return openNow;
    }

    public void setOpenNow(String openNow) {
        this.openNow = openNow;
    }

    public List<String> getOpenTime() {
        return openTime;
    }

    public void setOpenTime(List<String> openTime) {
        this.openTime = openTime;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
