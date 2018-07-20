package com.example.hp.myapplication;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PointOfInterest;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import arcore.MyArActivity;
import model.FoodStore;

import static android.location.LocationManager.GPS_PROVIDER;
import static com.example.hp.myapplication.PermissionAccess.PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapClickListener,
        View.OnClickListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnPoiClickListener, GoogleMap.OnMyLocationButtonClickListener,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private GoogleMap mMap;
    private CameraPosition mCameraPosition;
    private Marker marker;
    private Button searchStoreBtn;
    private GoogleApiClient mGoogleApiClient;
    private static final int REQUEST_CHECK_SETTINGS = 2;
    private LocationManager manager;

    private FusedLocationProviderClient mFusedClientProvider ;
    private final LatLng mDefaultLocation = new LatLng( 21.028511, 105.804817);
    private static final int DEFAULT_ZOOM = 16;

    private boolean mLocationPermissionGranted;
    private GeoDataClient mGeoDataClient;

    //2 locations to check multiclick on 1 loaction
    private Location mLastKnownLocation, mAfterLocation;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private static final int PLACE_PICKER_REQUEST = 1;
    private BottomSheetBehavior bottomSheetBehavior;
    private List<FoodStore> storeList;
    private Dialog dialog;
    private TextView dialogContent;
    private TextView tvSoluong;
    private Button dialogButton;
    private PlaceApi placeApi;

    private static final String  PERMISSION_INTERNET = "You must be connect to the internet!";
    private static final String  PERMISSION_LOCATION = "You must agree to enable your location!";
    private static final String SEARCH_TYPE_RESTAURANT = "restaurant";
    private static final String SEARCH_TYPE_MEAL_DELEVERY = "meal_delivery";
    private static final String SEARCH_TYPE_MEAL_TAKEAWAY = "meal_takeaway";
    private static final String SEARCH_TYPE_SUPERMARKET = "supermarket";
    private static final String SEARCH_KEY_QUAN_NHAU = "quán nh?u";
    private static final String SEARCH_KEY_QUAN_COM = "quán c?m";
    String url01,url02,url03,url04,url05,url06;

    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        //create dialog contents
        dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_internet_request);
        dialogContent = dialog.findViewById(R.id._permission_content);
        dialogButton = dialog.findViewById(R.id.confirm);
        dialogButton.setOnClickListener(this);
        //check internet connection
        try {
            if(new PermissionAccess().execute(MapsActivity.this).get() == false){
                dialogContent.setText(PERMISSION_INTERNET);
                dialog.show();
            }else{
                mAfterLocation = new Location("Huy Van");
                mAfterLocation.setLongitude(0.0);
                mAfterLocation.setLatitude(0.0);
                tvSoluong = findViewById(R.id.so_luong);
                searchStoreBtn = findViewById(R.id.search_store);
                searchStoreBtn.setOnClickListener(this);
                mFusedClientProvider = LocationServices.getFusedLocationProviderClient(this);
                mGeoDataClient = Places.getGeoDataClient(this);
                if(savedInstanceState != null){
                    mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
                    mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
                }
                SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.map);

                mapFragment.getMapAsync(this);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                if (marker != null) {
                    View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_content, (FrameLayout) findViewById(R.id.map), false);

                    TextView title = infoWindow.findViewById(R.id.title);
                    title.setText(marker.getTitle());

                    TextView snippet = infoWindow.findViewById(R.id.snippet);
                    snippet.setText(marker.getSnippet());

                    return infoWindow;
                }else{
                    Log.i("wtf:", "marker is null");
                    return null;
                }
            }
        });
        //get permission
        getLocationPerission();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode){
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    mLocationPermissionGranted = true;
                    getLocationPerission();
                }
                break;
            default:
                dialogContent.setText(PERMISSION_LOCATION);
                dialog.show();
        }
    }

    @SuppressLint("MissingPermission")
    private void updateLocationUI(){
        if(mMap == null){
            return;
        }
        try{
            if(mLocationPermissionGranted){
                System.out.println("key_01 vao set my location");
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            }else{
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPerission();
            }
        }catch (Exception ex){
            Log.e("Excption: %s", ex.getMessage());
        }
    }

    @SuppressLint("MissingPermission")
    private void getDeviceLocation(){
        try{
            if(mLocationPermissionGranted){
//                @SuppressLint("MissingPermission") Task<Location> locationResult =
                        mFusedClientProvider.getLastLocation()
                                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                                    @Override
                                    public void onSuccess(Location location) {
                                        if(location != null){
                                            System.out.println("key_01 loaction != null");
                                            mLastKnownLocation = location;
                                            LatLng latLng = new LatLng(mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude());
                                            marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
                                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM),1000,null);
                                        }else{
                                            System.out.println("key_01 loaction == null");
                                            turnOnGPS();
                                        }
                                    }
                                });
            }
        }catch (Exception e){
            Log.e("Exception: %s", e.getMessage());
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.none:
                mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
                break;
            case R.id.normal:
                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                break;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                break;
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                break;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                break;
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.place_menu, menu);
        return true;
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapClick(LatLng latLng) {
        if(marker != null){
            marker.remove();
        }
        //set current location
        mLastKnownLocation.setLatitude(latLng.latitude);
        mLastKnownLocation.setLongitude(latLng.longitude);
        updateLocationUI();
        marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,DEFAULT_ZOOM),1000,null);
    }


    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.search_store){
            if(!isLocationTheSame(mAfterLocation,mLastKnownLocation)) {
                mAfterLocation.setLongitude(mLastKnownLocation.getLongitude());
                mAfterLocation.setLatitude(mLastKnownLocation.getLatitude());
                searchStoreBtn.setEnabled(false);
                url01 = PlaceApi.search(SEARCH_TYPE_MEAL_DELEVERY, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1000, null);
                url02 = PlaceApi.search(SEARCH_TYPE_MEAL_TAKEAWAY, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1000, null);
                url03 = PlaceApi.search(SEARCH_TYPE_RESTAURANT, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1000, null);
                url04 = PlaceApi.search(SEARCH_TYPE_SUPERMARKET, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1000, null);
                url05 = PlaceApi.search(null, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1000, SEARCH_KEY_QUAN_COM);
                url06 = PlaceApi.search(null, mLastKnownLocation.getLatitude(), mLastKnownLocation.getLongitude(), 1000, SEARCH_KEY_QUAN_NHAU);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        try {
                            placeApi = new PlaceApi(mMap, searchStoreBtn, MapsActivity.this);
                            storeList = placeApi.execute(url01, url02, url03, url04, url05, url06).get();
                            //                placeApi.cancel(true);
                            tvSoluong.setText(String.valueOf(storeList.size()));
                            bottomSheetBehavior = BottomSheetBehavior.from(findViewById(R.id.bottom_sheet));

                            BottomSheetListView listView = findViewById(R.id.food_list_view);

                            CustomFoodListView customFoodListView = new CustomFoodListView(MapsActivity.this, R.layout.food_listview, storeList);
                            listView.setAdapter(customFoodListView);
                            bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                    Intent intent = new Intent(MapsActivity.this, MyArActivity.class);
                                    startActivity(intent);
                                }
                            });
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }


                    }
                }, 15000);   //15 seconds

            }else{
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
            }
        }else if(view.getId() == dialogButton.getId()){
            dialog.cancel();
            this.finishAffinity();
        }
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Geocoder geocoder =  new Geocoder(this);
        try {
            List<Address> addresses = geocoder.getFromLocation(marker.getPosition().latitude,marker.getPosition().longitude,1);
            String content = "";
            for (Address address: addresses){
                if(address.getAddressLine(0) !=null){
                    content = address.getAddressLine(0);
                }
            }
            Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return true;
    }

    @Override
    public void onPoiClick(PointOfInterest pointOfInterest) {
        Toast.makeText(this, pointOfInterest.name, Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        if(marker !=null){
            marker.remove();
        }
        getDeviceLocation();
        return true;
    }

    private void turnOnGPS(){
        if (mGoogleApiClient == null) {
            System.out.println("key_01 vao google api client = null");
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .build();
            mGoogleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);

            LocationSettingsRequest.Builder locationSettingsRequestBuilder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);
            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, locationSettingsRequestBuilder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @SuppressLint("MissingPermission")
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            System.out.println("key_01 vao LocationSettingsStatusCodes.SUCCESS");
                            try{
                                manager = (LocationManager) getSystemService( Context.LOCATION_SERVICE );
                                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 1.0f, MapsActivity.this);
                                if (!manager.isProviderEnabled( GPS_PROVIDER ) ) {
                                    startActivityForResult(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), 0);
                                }else{
                                    System.out.println("key_01 update location ui");
                                    updateLocationUI();
                                    getDeviceLocation();
                                }

                            }
                            catch (Exception e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            System.out.println("key_01 vao LocationSettingsStatusCodes.RESOLUTION_REQUIRED");
                            try {
                                status.startResolutionForResult(
                                        MapsActivity.this,
                                        REQUEST_CHECK_SETTINGS);
//                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            } catch (IntentSender.SendIntentException e) {
                                e.printStackTrace();
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            System.out.println("key_01 vao LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE");
                            break;

                            default:
                                System.out.println("vao nothing");
                    }
                }
            });
        }else{
            updateLocationUI();
            getDeviceLocation();
        }

    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
//                this.finish();
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        System.out.println("key_01 vao 0K");
                        turnOnGPS();
                        break;
                    case Activity.RESULT_CANCELED:
                        System.out.println("key_01 not connected");
                        this.finishAffinity();
                        break;
                    default:
                        break;
                }
                break;
            case PLACE_PICKER_REQUEST:
                if(requestCode == PLACE_PICKER_REQUEST){
                    if(resultCode == RESULT_OK){
                        Place place = PlacePicker.getPlace(this, intent);
                        mLastKnownLocation.setLatitude(place.getLatLng().latitude);
                        mLastKnownLocation.setLongitude(place.getLatLng().longitude);
                        getDeviceLocation();
                    }
                }
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    private void getLocationPerission(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mLocationPermissionGranted = true;
            System.out.println("key_01 after check location ui");
            mMap.setOnMarkerClickListener(this);
            mMap.setOnMyLocationButtonClickListener(this);
            mMap.setOnMapClickListener(this);
            mMap.setOnPoiClickListener(this);
            turnOnGPS();
        }else{
            System.out.println("key_01 enter else");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    private boolean isLocationTheSame(Location l1, Location l2){
        if(l1.getLatitude() == l2.getLatitude() && l1.getLongitude() == l2.getLongitude()){
            return true;
        }
        return false;
    }

}