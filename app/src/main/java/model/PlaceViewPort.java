package model;

import com.google.android.gms.maps.model.LatLng;

public class PlaceViewPort {
    private LatLng northEast;
    private LatLng southwest;

    public PlaceViewPort() {
    }

    public PlaceViewPort(LatLng northEast, LatLng southwest) {
        this.northEast = northEast;
        this.southwest = southwest;
    }

    public LatLng getNorthEast() {
        return northEast;
    }

    public void setNorthEast(LatLng northEast) {
        this.northEast = northEast;
    }

    public LatLng getSounthwest() {
        return southwest;
    }

    public void setSounthwest(LatLng sounthwest) {
        this.southwest = sounthwest;
    }
}
