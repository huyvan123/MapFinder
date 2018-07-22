package com.example.hp.myapplication;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Button;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import model.FoodStore;

public class PlaceApi extends AsyncTask<String, Void, List<FoodStore>> {

    private static final String RESULTS = "results";
    private static final String GEOMETRY = "geometry";
    private static final String LOCATION = "location";
    private static final String LAT = "lat";
    private static final String LNG = "lng";
    private static final String ICON = "icon";
    private static final String PLACE_ID = "place_id";
    private static final String ID = "id";
    private static final String NAME = "name";
    private static final String PHOTOS = "photos";
    private static final String MAXWIDTH = "400";
    private static final String PHOTO_REFERENCE = "photo_reference";

    private static final String DETAIL_RESULT = "result";
    private static final String DETAIL_FORMATTED_ADDRESS = "formatted_address";
    private static final String DETAIL_FORMATTED_PHONENUMBER = "formatted_phone_number";
    private static final String DETAIL_INTERNATIONAL_PHONENUMBER = "international_phone_number";
    private static final String DETAIL_OPENING_HOURS = "opening_hours";
    private static final String DETAIL_OPEN_NOW = "open_now";
    private static final String DETAIL_WEEK_DAY_TEXT = "weekday_text";
    private static final String DETAIL_RATING = "rating";
    private static final String DETAIL_WEBSITE = "website";


    private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api";
    private static final String TYPE_PLACE = "/place";
    private static final String TYPE_DETAIL = "/details";
    private static final String TYPE_SEARCH = "/nearbysearch";
    private static final String TYPE_PHOTO = "/photo";
    private static final String OUT_JSON = "/json";
    private static final String API_KEY = "AIzaSyB5x96cq3PIuDGjyi2cec4xNfHR2JqO6jA";
    private static final String METHOD_GET = "GET";

    private static final int DEFAULT_QUANTITY = 5;
    private static final int DEFAULT_BEGIN = 0;

    private List<Marker> markerList;
    private GoogleMap mMap;
    private Button searchButton;
    private Activity activity;
    private List<String> placeIDtList;

    public PlaceApi(GoogleMap googleMap, Button button, Activity activity) {
        super();
        this.mMap = googleMap;
        this.searchButton = button;
        this.activity = activity;
        markerList = new ArrayList<>();
        placeIDtList = new ArrayList<>();
    }

    public static String getUrlForSearchPlace(String type, double lat, double lng, int radius, String key) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_PLACE);
            sb.append(TYPE_SEARCH);
            sb.append(OUT_JSON);
            sb.append("?location=").append(lat).append(",").append(lng);
            sb.append("&radius=").append(radius);
            if(type != null){
                sb.append("&type=").append(type);
            }
            if(key != null){
                sb.append("&keyword=").append(key);
            }
            sb.append("&keyword=").append("");
            sb.append("&key=").append(API_KEY);

        return sb.toString();
    }
    public static String getUrlForSearchPlaceDetail(String placeid) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            StringBuilder sb = new StringBuilder(PLACES_API_BASE);
            sb.append(TYPE_PLACE);
            sb.append(TYPE_DETAIL);
            sb.append(OUT_JSON);
            sb.append("?placeid=").append(placeid);
            sb.append("&key=").append(API_KEY);

        return sb.toString();
    }
    public static String getUrlForSearchPlacePhoto(String maxWidth, String photoReference){
        StringBuilder builder = new StringBuilder(PLACES_API_BASE);
        builder.append(TYPE_PLACE);
        builder.append(TYPE_PHOTO);
        builder.append("?maxwidth=").append(maxWidth);
        builder.append("&photoreference=").append(photoReference);
        builder.append("&key=").append(API_KEY);
        return builder.toString();
    }

    @Override
    protected List<FoodStore> doInBackground(String... placesURL) {
        List<String> jsonResultList = new ArrayList<>();
        for (String placeSearchURL : placesURL) {
            jsonResultList.add(getRespondFromUrl(placeSearchURL));
        }
        try {
            setAllLocation(jsonResultList);
            return loadFoodStore(DEFAULT_QUANTITY, DEFAULT_BEGIN);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(List<FoodStore> foodStores) {
        super.onPostExecute(foodStores);
        this.searchButton.setEnabled(true);
        this.cancel(true);
    }
    private FoodStore getFoodStoreDetail(String placeId) throws JSONException {
        FoodStore foodStore;
        JSONObject jsonObject = new JSONObject(getRespondFromUrl(getUrlForSearchPlaceDetail(placeId)));
        JSONObject jsonResult = jsonObject.getJSONObject(DETAIL_RESULT);
        String address = jsonResult.getString(DETAIL_FORMATTED_ADDRESS);
        String phonenumber = "N/A";
        if(jsonResult.has(DETAIL_FORMATTED_PHONENUMBER)){
            phonenumber = jsonResult.getString(DETAIL_FORMATTED_PHONENUMBER);
        }
        String iconUrl;
        if (jsonResult.has(PHOTOS)){
            JSONArray photos = jsonResult.getJSONArray(PHOTOS);
            JSONObject photo = photos.getJSONObject(0);
            String photoReferrence = photo.getString(PHOTO_REFERENCE);
            iconUrl = getUrlForSearchPlacePhoto(MAXWIDTH,photoReferrence);
        }else {
            iconUrl = jsonResult.getString(ICON);
        }
        String id = jsonResult.getString(ID);
        String internationalPhonenumber = "";
        if(jsonResult.has(DETAIL_INTERNATIONAL_PHONENUMBER)){
            internationalPhonenumber = jsonResult.getString(DETAIL_INTERNATIONAL_PHONENUMBER);
        }
        String name = jsonResult.getString(NAME);
        JSONObject jsonOpeningHours = new JSONObject();
        if(jsonResult.has(DETAIL_OPENING_HOURS)){
            jsonOpeningHours = jsonResult.getJSONObject(DETAIL_OPENING_HOURS);
        }
        String openNow = "N/A";
        if(jsonOpeningHours.has(DETAIL_OPEN_NOW)){
            openNow = changeOpenNow(jsonOpeningHours.getString(DETAIL_OPEN_NOW));
        }
        JSONArray arrayWeekDayText = new JSONArray();
        if(jsonOpeningHours.has(DETAIL_WEEK_DAY_TEXT)){
            arrayWeekDayText = jsonOpeningHours.getJSONArray(DETAIL_WEEK_DAY_TEXT);
        }
        List<String> openTimes = new ArrayList<>();
        for (int i = 0; i < arrayWeekDayText.length(); i++){
            openTimes.add(arrayWeekDayText.getString(i));
        }
        String rating = "N/A";
        if(jsonResult.has(DETAIL_RATING)){
            rating = jsonResult.getString(DETAIL_RATING);
        }
        String website = "N/A";
        if(jsonResult.has(DETAIL_WEBSITE)){
            website = jsonResult.getString(DETAIL_WEBSITE);
        }
        foodStore = new FoodStore(id,address,phonenumber,iconUrl,openNow,openTimes,rating,website,name,internationalPhonenumber);
        return foodStore;
    }

    private  String changeOpenNow(String before){
        switch (before){
            case "true": return "yes";
            case "false": return "no";
            default: return "N/A";
        }
    }

    private String getRespondFromUrl(String url){
        StringBuilder placesBuilder = new StringBuilder();
            try {
                URL requestUrl = new URL(url);
                HttpURLConnection connection = (HttpURLConnection)requestUrl.openConnection();
                connection.setRequestMethod(METHOD_GET);
                connection.connect();
                int responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = null;
                    InputStream inputStream = connection.getInputStream();
                    if (inputStream == null) {
                        return "";
                    }
                    reader = new BufferedReader(new InputStreamReader(inputStream));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        placesBuilder.append(line + "\n");
                    }
                    if (placesBuilder.length() == 0) {
                        return "";
                    }
                }
            } catch (MalformedURLException e) {
                Log.e("test", "Error processing Places API URL", e);
            } catch (IOException e) {
                Log.e("test", "Error connecting to Places API", e);
            }
        return placesBuilder.toString();
    }

    //for recycler

    public static List<LatLng> locations = new ArrayList<>();

    public void setAllLocation(List<String> listJsonString) throws JSONException {
        for (String jsonString: listJsonString) {
            JSONObject jsonObject = new JSONObject(jsonString);
            JSONArray jsonArrayResult = jsonObject.getJSONArray(RESULTS);
            for (int i = 0; i < jsonArrayResult.length(); i++) {
                //get each array element
                JSONObject jsonArray = jsonArrayResult.getJSONObject(i);
                String placeId = jsonArray.getString(PLACE_ID);
                if (!placeIDtList.contains(placeId)) {
                    placeIDtList.add(placeId);
                    //get geometry
                    JSONObject jsonGeometry = jsonArray.getJSONObject(GEOMETRY);
                    //get location
                    JSONObject jsonLocation = jsonGeometry.getJSONObject(LOCATION);
                    double lat = jsonLocation.getDouble(LAT);
                    double lng = jsonLocation.getDouble(LNG);
                    LatLng location = new LatLng(lat, lng);
                    locations.add(location);
                }

            }
        }
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                for (Marker m: markerList) {
                    System.out.println("recycle remove marker");
                    m.setVisible(false);
                    m.remove();
                }
                markerList = new ArrayList<>();
                Marker marker;
                for (LatLng f: locations){
                    marker = mMap.addMarker(new MarkerOptions().position(f).icon(BitmapDescriptorFactory.defaultMarker()));
                    markerList.add(marker);
                }
            }
        });
    }

    public List<FoodStore> loadFoodStore(int quantity, int begin) throws JSONException {
        List<FoodStore> storeList = new ArrayList<>();
        if ( begin == 0){
            storeList.add(null);
        }
        for(int i = begin; i < (begin + quantity); i++){
            try {
                storeList.add(getFoodStoreDetail(placeIDtList.get(i)));
            }catch (IndexOutOfBoundsException e){
                break;
            }
        }
        return storeList;
    }

}