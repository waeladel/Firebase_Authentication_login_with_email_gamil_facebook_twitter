package com.getin.car.activities;

import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.os.Bundle;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.getin.car.R;
import com.getin.car.authentication.PolyUtil;
import com.getin.car.fragments.TripInfoFragment;
import com.getin.car.fragments.TripRulesFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import androidx.fragment.app.FragmentManager;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.getin.car.activities.BaseActivity.trip;
import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class PostActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        RoutingListener,
        TripInfoFragment.OnFragmentInteractionListener,
        TripRulesFragment.OnFragmentInteractionListener{

    private final static String TAG = PostActivity.class.getSimpleName();

    private GoogleMap mMap;
    private static final int MY_PERMISSION_REQUEST_CODE = 7000;
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 7001;
    public static final int EXPLAIN_READ_EXTERNAL_STORAGE = 7;
    public static final int PERMISSION_NOT_GRANTED = 8;
    private static final int EXPLAIN_LOCATION_PERMISSION = 9;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiClient;
    private FusedLocationProviderClient mFusedLocationClient;
    private static Location mLastLocation;

    public PlaceAutocompleteFragment autocompleteOrigin;
    public PlaceAutocompleteFragment autocompleteDestination;


    private static final int UPDATE_INTERVAL = 10 * 1000;  /* 10 secs */
    private static final int FASTEST_INTERVAL  = 3000;
    private static final int DISPLACEMENT = 10;

    //private Marker mCurrentMarker;
    private Marker mOriginMarker;
    private Marker mDestinationMarker;
    private MarkerOptions mMarkerOptions;
    private LatLngBounds mBounds;
    SupportMapFragment mapFragment;

    private static volatile LatLng mOrigin ;
    private static volatile LatLng mDestination ;
    private static volatile String encodedPoly;
    private static volatile String mOriginName;
    private static volatile String mOriginAddress;
    private static volatile String mDestinationName;
    private static volatile String mDestinationAddress;


    private List<Polyline> polylines;
    private List<Marker> MarkersList;
    private List<Integer> distances;
    private List<Integer> durations;

    private LatLngBounds.Builder mBoundBuilder;

    private Button mTripInfoButton;
    private TripInfoFragment tripInfoFragment;
    private TripRulesFragment tripRulesFragment;
    // Get FragmentManager
    public FragmentManager fragmentManager;

    //initialize the FirebaseAuth instance
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;

    //initialize the Firebase Database
    private FirebaseFirestore db;

    private RequestQueue requestQueue;

    //private static final int[] COLORS = new int[]{R.color.colorGreen,R.color.colorBlue,R.color.colorRed,R.color.colorAccent,R.color.primary_dark_material_light};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        polylines = new ArrayList<>();
        MarkersList = new ArrayList<>();
        distances = new ArrayList<Integer>();// to add the distance of all poly
        durations = new ArrayList<Integer>();
        fragmentManager = getSupportFragmentManager();

        requestQueue = Volley.newRequestQueue(this);

        // Init view //
        mTripInfoButton = findViewById(R.id.trip_info_btn);

        mTripInfoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "mTripInfoButton clicked ");
                if(mOrigin !=  null && mDestination != null && encodedPoly != null){
                    trip.setOrigin(latLngToGeoPoint(mOrigin));
                    trip.setDestination(latLngToGeoPoint(mDestination));
                    trip.setPolyline(encodedPoly);
                    Log.i(TAG, "mOrigin name: "+mOriginName + "mDestination name= "+ mDestinationName);
                    Log.i(TAG, "mOrigin address: "+mOriginAddress + "mDestination address= "+ mDestinationAddress);
                    Log.i(TAG, "setPolyline: "+encodedPoly);

                    if(mOriginName != null || mOriginAddress != null){
                        trip.setLabel(getString(R.string.label_text ,mOriginName, mDestinationName));
                        trip.setDetails(getString(R.string.label_text ,mOriginAddress, mDestinationAddress));
                    }else{
                        geoCode(mOrigin, "currentLocation");// get address and set label and details
                    }
                }

                Log.i(TAG, "get setDestination clicked,Origin= "+ trip.getOrigin()+"Destination= "+trip.getDestination()+"Polyline= "+trip.getPolyline());
                tripInfoFragment  = TripInfoFragment.newInstance();//new EditProfileFrag();
                FragmentTransaction tripInfoTransaction = fragmentManager.beginTransaction();
                tripInfoTransaction.add(R.id.map_layout, tripInfoFragment,"tripInfoFrag");
                tripInfoTransaction.addToBackStack("tripInfoFrag");
                tripInfoTransaction.commit();

            }
        });

        autocompleteOrigin = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_origin);
        autocompleteOrigin.setHint(getString(R.string.origin));

        autocompleteOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, "Placeg etId: " + place.getId());
                Log.i(TAG, "Place Address: " + place.getAddress());
                Log.i(TAG, "Place LatLng: " + place.getLatLng());
                Log.i(TAG, "Place Locale: " + place.getLocale());
                Log.i(TAG, "Place Attributions: " + place.getAttributions());
                mOrigin = place.getLatLng();
                mOriginName = place.getName().toString();
                mOriginAddress = place.getAddress().toString();

                displayMarker(mOrigin, "Origin");

                if (mOrigin != null && mDestination != null){
                    getRoutes(mOrigin, mDestination);
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        autocompleteDestination = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_destination);
        autocompleteDestination.setHint(getString(R.string.destination));

        autocompleteDestination.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, "Placeg etId: " + place.getId());
                Log.i(TAG, "Place Address: " + place.getAddress());
                Log.i(TAG, "Place LatLng: " + place.getLatLng());
                Log.i(TAG, "Place Locale: " + place.getLocale());
                Log.i(TAG, "Place Attributions: " + place.getAttributions());
                mDestination = place.getLatLng();
                mDestinationName = place.getName().toString();
                mDestinationAddress = place.getAddress().toString();
                //displayMarker(mDestination, "Destination");
                if (mOrigin != null && mDestination != null){
                    displayMarker(mDestination, "Destination");
                    getRoutes(mOrigin, mDestination);
                }else{

                    if (mDestination != null){
                        LatLng currentOrigin = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                        Log.i(TAG, "Place mLastLocation: " + currentOrigin);
                        mOrigin = currentOrigin;
                        getRoutes(mOrigin, mDestination);
                        displayMarker(mDestination, "Destination");
                        geoCode(mOrigin, "Origin");
                    }

                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });


        // Init view //

        // Functions //
        //startLocationUpdates();
        //displayLocation();

        // Obtain the FirebaseDatabase instance.
        db =  FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        //initialize the AuthStateListener method
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                    Log.d(TAG, "onAuthStateChanged:signed_in_getDisplayName:" + user.getDisplayName());
                    Log.d(TAG, "onAuthStateChanged:signed_in_getEmail():" + user.getEmail());
                    Log.d(TAG, "onAuthStateChanged:signed_in_getPhotoUrl():" + user.getPhotoUrl());
                    Log.d(TAG, "onAuthStateChanged:signed_in_emailVerified?:" + user.isEmailVerified());

                    trip.setOwnerId(user.getUid());
                    fetchUserData(user.getUid());

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                Log.d(TAG, "onAuthStateChanged:signed_out");

            }
        };

    }//end of onCreate


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
        Log.d(TAG, "MainActivity onStart");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "MainActivity onStop");
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        isGooglePlayServicesAvailable();
    }

    // To get titles and address for latlang
    private void geoCode(LatLng point , final String markerTag) {
        Log.d(TAG, "geoCode LatLng =" +point);
        Log.d(TAG, "geoCode markerTag =" +markerTag);

        String geoCodeingKey = getString(R.string.geocoding_key);
        Log.d(TAG, "geoCodeingKey =" +geoCodeingKey);

        String requestURL = "https://maps.googleapis.com/maps/api/geocode/json?latlng="+
                point.latitude+","+point.longitude+
                "&key="+geoCodeingKey;

        JsonObjectRequest request = new JsonObjectRequest(requestURL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            if(markerTag.equalsIgnoreCase("Destination")){
                                mDestinationAddress = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                                mDestinationName = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                                autocompleteDestination.setText(mDestinationAddress);
                                Log.d(TAG, "geoCode mDestinationAddress =" +mDestinationAddress);

                            }else if(markerTag.equalsIgnoreCase("Origin")){
                                mOriginAddress = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                                mOriginName = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                                autocompleteOrigin.setText(mOriginAddress);
                                Log.d(TAG, "geoCode mOriginAddress =" +mOriginAddress);
                            }else{
                                mOriginAddress = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                                mOriginName = response.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                                trip.setLabel(getString(R.string.label_text ,mOriginName, mDestinationName));
                                trip.setDetails(getString(R.string.label_text ,mOriginAddress, mDestinationAddress));
                                Log.d(TAG, "geoCode mOriginAddress =" +mOriginAddress);
                                Log.d(TAG, "geoCode mDestinationName =" +mDestinationName);
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                Log.e(TAG, "geoCode mOriginAddress =" +error.getMessage());

            }
        });

        requestQueue.add(request);

    }


    //get current user name and profile
    private void fetchUserData(String uid) {
        DocumentReference docRef = db.collection("users").document(uid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        String userName = document.getString("name");
                        if(userName != null){
                            trip.setOwnerName(userName);
                        }

                        String avatarUrl = document.getString("avatar");
                        if (avatarUrl != null){
                            trip.setOwnerPic(avatarUrl);
                            Log.d(TAG, "DocumentSnapshot mProfileImageButton sAvatarUrl= " +avatarUrl);
                        }

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                    //listener.onFailed(task.getException());
                }
            }
        });
    }

    // get last location once
    private void getLastLocation() {

        Log.d(TAG, "getLastLocation is on");

        if(checkPermissions() && isGooglePlayServicesAvailable()){
            mFusedLocationClient = getFusedLocationProviderClient(this);
            mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                mLastLocation = location;
                                //onLocationChanged(mLastLocation);
                                LatLng latlang = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                                displayMarker(latlang, "Origin");

                            }
                        }
                    });

        }
    }

    // Trigger new location updates at interval
    private void startLocationUpdates() {
        if(checkPermissions() && isGooglePlayServicesAvailable()){
            // Create the location request to start receiving updates
            mLocationRequest = new LocationRequest();
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            mLocationRequest.setInterval(UPDATE_INTERVAL);
            mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
            mLocationRequest.setSmallestDisplacement(DISPLACEMENT);

            // Create LocationSettingsRequest object using location request
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            LocationSettingsRequest locationSettingsRequest = builder.build();

            // Check whether location settings are satisfied
            // https://developers.google.com/android/reference/com/google/android/gms/location/SettingsClient
            SettingsClient settingsClient = LocationServices.getSettingsClient(this);
            settingsClient.checkLocationSettings(locationSettingsRequest);

            // new Google API SDK v11 uses getFusedLocationProviderClient(this)
            getFusedLocationProviderClient(this).requestLocationUpdates(mLocationRequest, new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            // do work here
                            onLocationChanged(locationResult.getLastLocation());
                        }
                    },
                    Looper.myLooper());
        }

    }

    /*private void displayLocation(Location LastLocation) {
        *//*if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            return;
        }*//*
        if (isGooglePlayServicesAvailable()){
            final double latitude = LastLocation.getLatitude();
            final double longitude = LastLocation.getLongitude();

            // set Marker
            if (mCurrentMarker != null){
                mCurrentMarker.remove();// remove existing marker
            }

            autocompleteOrigin.setText(getString(R.string.current_location));
            LatLng locationLatLng = new LatLng(latitude, longitude);
            mCurrentMarker = mMap.addMarker(new MarkerOptions()
                    .position(locationLatLng)
                    .title(getString(R.string.me))
                    .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_front_car_black))
                    .draggable(true)
                    .snippet("Kiel is cool")

            );
            //mMap.moveCamera(CameraUpdateFactory.newLatLng(locationLatLng));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(locationLatLng,15.0f));
        }

    }
*/

    private void displayMarker(LatLng latlang, String marker) {

        switch (marker) {
            case "Origin":
                if (mOriginMarker != null){
                    mOriginMarker.remove();// remove existing marker
                }else{
                    autocompleteOrigin.setText(getString(R.string.current_location));
                }

                mOriginMarker = mMap.addMarker(new MarkerOptions()
                        .position(latlang)
                        .title(getString(R.string.origin))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_front_car_black))
                        .draggable(true)
                        .snippet("Kiel is cool")

                );

                mOriginMarker.setTag("Origin");
                MarkersList.add(mOriginMarker);// add marker to markers list to calculate bounds

                break;
            case "Destination":
                if (mDestinationMarker != null){
                    mDestinationMarker.remove();// remove existing marker
                }
                mDestinationMarker = mMap.addMarker(new MarkerOptions()
                        .position(latlang)
                        .title(getString(R.string.destination))
                        //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_front_car_black))
                        .draggable(true)
                        .snippet("Kiel is cool")

                );

                mDestinationMarker.setTag("Destination");
                MarkersList.add(mDestinationMarker);// add marker to markers list to calculate bounds
                break;
        }// end of switch

        if (mOriginMarker != null && mDestinationMarker != null){
            if (MarkersList.size() >= 2 ){
                mBoundBuilder.include(mOriginMarker.getPosition());
                mBoundBuilder.include(mDestinationMarker.getPosition());
                mBounds = mBoundBuilder.build();
            }
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int autocomplete_height = findViewById(R.id.autocomplete_layout).getHeight();
            int remainHeight = height - autocomplete_height;

            Log.d(TAG, "autocomplete_height= "+autocomplete_height+"remainHeight= "+remainHeight );


            int padding = (int) (width * 0.15); // offset from edges of the map 10% of screen

            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(mBounds,width, remainHeight,  padding));
        }else{
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlang,15.0f));
        }
    }

    private GeoPoint latLngToGeoPoint(LatLng latLng) {
        GeoPoint geoPoint = new GeoPoint(latLng.latitude, latLng.longitude );
        return geoPoint;
    }


    private void getRoutes(LatLng origin, LatLng destination) {

        String directionsKey = getString(R.string.directions_key);
        Log.d(TAG, "directionsKey =" +directionsKey);

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(origin, destination)
                .key(directionsKey)
                .build();
        routing.execute();
    }



        private boolean checkPermissions() {
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)== PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions();
            return false;
        }
    }

    private void requestPermissions() {

        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION ) || ActivityCompat.shouldShowRequestPermissionRationale(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            showMaterialDialog(EXPLAIN_LOCATION_PERMISSION);

            /*Snackbar.make(findViewById(R.id.map), R.string.location_access_required,
                    Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // Request the permission
                    ActivityCompat.requestPermissions(PostActivity.this,
                            new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                    android.Manifest.permission.ACCESS_FINE_LOCATION},
                            MY_PERMISSION_REQUEST_CODE);
                }
            }).show();*/

        } else {
            // No explanation needed; request the permission
            //Snackbar.make(findViewById(R.id.map), R.string.location_unavailable, Snackbar.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                            android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_REQUEST_CODE);
        }

    }

    private boolean isGooglePlayServicesAvailable() {
        // Check that Google Play services is available
        GoogleApiAvailability googleAPI = GoogleApiAvailability.getInstance();
        int resultCode = googleAPI.isGooglePlayServicesAvailable(this);
        // If Google Play services is not available
        if(resultCode != ConnectionResult.SUCCESS) {
            if(googleAPI.isUserResolvableError(resultCode)) {
                googleAPI.getErrorDialog(this, resultCode,
                        PLAY_SERVICES_RESOLUTION_REQUEST).show();
            }

            return false;
        }
        Log.d(TAG, "Google Play services is available.");
        return true;
    }

    private void showMaterialDialog(int id) {
        switch (id) {
            case PERMISSION_NOT_GRANTED:
                new MaterialDialog.Builder(this)
                        .title(R.string.permission_not_granted_title)
                        .content(R.string.permission_not_granted)
                        .positiveText(R.string.ok)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();                            }
                        })
                        .show();
                break;
            case EXPLAIN_LOCATION_PERMISSION:
                new MaterialDialog.Builder(this)
                        .title(R.string.access_location)
                        .content(R.string.location_access_required)
                        .positiveText(R.string.grant)
                        .negativeText(R.string.cancel)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                                // Request the permission
                                ActivityCompat.requestPermissions(PostActivity.this,
                                        new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                                android.Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSION_REQUEST_CODE);
                            }
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(MaterialDialog dialog, DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .show();
                break;
        }
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        Log.d(TAG, "onMapReady:" + googleMap);

        mMap = googleMap;
        if(checkPermissions() && isGooglePlayServicesAvailable()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            //mMap.getUiSettings().setZoomControlsEnabled(true);
            getLastLocation();
            //startLocationUpdates();

        }

        mBoundBuilder = new LatLngBounds.Builder();

        // Add a marker in Sydney and move the camera
        /*LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));*/
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                Log.d(TAG, "onMarkerDragStart:" + marker.getPosition());

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                //Log.d(TAG, "onMarkerDrag:" + marker.getPosition());

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Log.d(TAG, "onMarkerDragEnd:" + marker.getPosition()+"id= "+marker.getId()+"tag= "+marker.getTag());
                try {
                    switch (marker.getTag().toString()) {
                        case "Origin":
                            mOrigin = marker.getPosition();
                            Log.d(TAG, "marker.getTitle:" + marker.getTitle());
                            geoCode(mOrigin, "Origin");
                            //autocompleteOrigin.setText(marker.getPosition().toString());
                            if (mOrigin != null && mDestination != null){
                                getRoutes(mOrigin, mDestination);
                            }
                            break;
                        case "Destination":
                            mDestination = marker.getPosition();
                            geoCode(mDestination, "Destination");
                            //autocompleteDestination.setText(marker.getPosition().toString());
                            if (mOrigin != null && mDestination != null){
                                getRoutes(mOrigin, mDestination);
                            }

                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

        });

        mMap.setOnPolylineClickListener(new GoogleMap.OnPolylineClickListener() {
            public void onPolylineClick(Polyline polyline) {
                /*int strokeColor = polyline.getColor() ^ 0x0000CC00;
                polyline.setColor(strokeColor);*/
                //polyline.getZIndex()
                Log.i(TAG, "Polyline points @ " + polyline.getPoints());
                Log.i(TAG, "Polyline getId @ " + polyline.getId());
                Log.i(TAG, "polyline getZIndex() @ " + polyline.getZIndex());

                // loop throw all polylines array to set there ZIndex to 0 and colors to grey except selected one
                for (int i = 0; i <polylines.size(); i++) {
                    polylines.get(i).setZIndex(0.0f);
                    polylines.get(i).setColor(ContextCompat.getColor(PostActivity.this, R.color.colorGrey));

                    if (polyline.getId().equals(polylines.get(i).getId())){// this is the selected one
                        polyline.setZIndex(1.0f);
                        polyline.setColor(ContextCompat.getColor(PostActivity.this, R.color.colorAccent));
                        Log.i(TAG, "polyline setZIndex= " +polyline.getZIndex());
                        encodedPoly = PolyUtil.encode(polyline.getPoints());

                        Log.d(TAG, "distances.get(i)= " +distances.get(i));
                        Log.d(TAG, "durations.get(i)= " +durations.get(i));
                        Log.d(TAG, "durations size)= " +durations.size()+ "distances size= "+distances.size() );


                        trip.setDistance(distances.get(i));
                        trip.setDuration(durations.get(i));
                    }
                }
                //Toast.makeText(PostActivity.this, "Polyline klick: " + polyline.getPoints(), Toast.LENGTH_LONG).show();
            }
        });

        /*mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latlang) {
                Log.d(TAG, "onMapLongClick:" + latlang.toString());
            }

        });*/

    }// end of on map ready


    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "onLocationChanged:" + location);
        mLastLocation = location;
        //onLocationChanged(mLastLocation);
        //displayLocation(mLastLocation);
        LatLng latlang = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
        displayMarker(latlang, "Origin");
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.d(TAG, "onStatusChanged:");

    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.d(TAG, "onProviderEnabled:");

    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.d(TAG, "onProviderDisabled:");

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "onConnected:");
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended:");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_CODE: {
                Log.d(TAG, "onRequestPermissionsResult: grantResults.length= "+grantResults.length
                +"grantResults= "+ grantResults[0]);
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // init task you need to do.
                    if(isGooglePlayServicesAvailable()) {
                        getLastLocation();
                        //startLocationUpdates();
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    showMaterialDialog(PERMISSION_NOT_GRANTED);
                    //Toast.makeText(PostActivity.this, R.string.permission_denied, Toast.LENGTH_LONG).show();
                }
                break;
            }

        }
    }

    @Override
    public void onFragmentInteraction(String fragName) {
        Log.d(TAG, "mama helwa");
        switch (fragName){
            case "tripRulesFragment":
                Log.i(TAG, "mama mTripInfoButton clicked ");

                Log.i(TAG, "mama et setDestination clicked,Origin= "+ trip.getOrigin()+"Destination= "+trip.getDestination()+"Polyline= "+trip.getPolyline());
                tripRulesFragment  = TripRulesFragment.newInstance();//new EditProfileFrag();
                FragmentTransaction tripRulesTransaction = fragmentManager.beginTransaction();
                tripRulesTransaction.add(R.id.map_layout, tripRulesFragment, "tripRulesFragment");
                tripRulesTransaction.addToBackStack("tripRulesFragment");
                tripRulesTransaction.commit();
                break;

            case "postTrip":
                Log.d(TAG, "mama postTrip clicked ");
                db.collection("trips")
                        .add(trip)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
                                Toast.makeText(PostActivity.this, R.string.add_successfully, Toast.LENGTH_SHORT).show();

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                                Toast.makeText(PostActivity.this, R.string.route_try_again, Toast.LENGTH_SHORT).show();
                            }
                        });

                finish();
                //fragmentManager.popBackStack("tripInfoFrag", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                break;

        }

    }

    //// Listener for routs ////////

    @Override
    public void onRoutingFailure(RouteException e) {

        if(e != null) {
            Toast.makeText(this, R.string.route_error + e.getMessage(), Toast.LENGTH_LONG).show();
        }else {
            Toast.makeText(this, R.string.route_try_again, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onRoutingStart() {
        // The Routing Request starts
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {

        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        if(distances.size()>0 || durations.size()>0 ) {

            for (int i = 0; i <distances.size(); i++) {
                distances.remove(i);
            }
            for (int i = 0; i <durations.size(); i++) {
                durations.remove(i);
            }
        }

        distances = new ArrayList<Integer>();
        durations = new ArrayList<Integer>();

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            //int colorIndex = i % COLORS.length;

            PolylineOptions polyOptions = new PolylineOptions();
            //polyOptions.color(getResources().getColor(COLORS[colorIndex]));
            if(i == shortestRouteIndex){
                Log.d(TAG,"shortest Route Index= "+shortestRouteIndex + "i+"+route.get(i).getDistanceValue());
                polyOptions.color(ContextCompat.getColor(this,R.color.colorAccent));
                polyOptions.zIndex(1.0f);
                Log.d(TAG,"shortest Route getPolyline= "+ "Country"+route.get(i).getCountry()
                        //+ " Polyline "+route.get(i).getPolyline()
                        + " getDistanceValue "+route.get(i).getDistanceValue()
                        + " getDistanceText "+route.get(i).getDistanceText()
                        //+ " getEndAddressText "+route.get(i).getEndAddressText()
                        + " getName "+route.get(i).getName()
                        + " getLatLgnBounds "+route.get(i).getLatLgnBounds()
                        + " getDistanceValue "+route.get(i).getDistanceValue()
                        + " getDurationValue "+route.get(i).getDurationValue()
                        + " getDistanceText "+route.get(i).getDistanceText()
                        + " getDurationText "+route.get(i).getDurationText()
                        + " getCountry "+route.get(i).getCountry());
                trip.setDistance(route.get(i).getDistanceValue());
                trip.setDuration(route.get(i).getDurationValue());

                        //+ " getPoints "+route.get(i).getPoints()
                        //+ " getSegments "+route.get(i).getSegments());
                encodedPoly = PolyUtil.encode(route.get(i).getPoints());
            }else{
                polyOptions.color(ContextCompat.getColor(this,R.color.colorGrey));
            }
            polyOptions.width(10 + i * 3);
            polyOptions.clickable(true);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            // to add the distance of all poly
            distances.add(route.get(i).getDistanceValue());
            durations.add(route.get(i).getDurationValue());

            Log.d(TAG,"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue());
            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onRoutingCancelled() {
        Log.i(TAG, "Routing was cancelled.");
    }


}

