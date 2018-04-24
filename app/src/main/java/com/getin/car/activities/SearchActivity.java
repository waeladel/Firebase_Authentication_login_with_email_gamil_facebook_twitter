package com.getin.car.activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import com.getin.car.R;
import com.getin.car.fragments.SearchFiltersFragment;
import com.getin.car.fragments.TripInfoFragment;
import com.getin.car.fragments.TripRulesFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.getin.car.activities.BaseActivity.trip;
import static com.getin.car.activities.BaseActivity.search;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class SearchActivity extends FragmentActivity implements OnMapReadyCallback,
        SearchFiltersFragment.OnFragmentInteractionListener{

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

    private Button mSearchFilterButton;
    private SearchFiltersFragment mSearchFiltersFragment;
    private TripRulesFragment tripRulesFragment;
    // Get FragmentManager
    public FragmentManager fragmentManager;

    //initialize the FirebaseAuth instance
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;

    //initialize the Firebase Database
    private FirebaseFirestore db;

    private RequestQueue requestQueue;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.search_map);
        mapFragment.getMapAsync(this);

        MarkersList = new ArrayList<>();
        fragmentManager = getSupportFragmentManager();

        requestQueue = Volley.newRequestQueue(this);

        // Init view //
        mSearchFilterButton = findViewById(R.id.search_filter_btn);

        mSearchFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "mSearchFilterButton clicked ");
                if(mOrigin !=  null && mDestination != null){
                    search.setOrigin(latLngToGeoPoint(mOrigin));
                    search.setDestination(latLngToGeoPoint(mDestination));
                    //trip.setPolyline(encodedPoly);
                    Log.d(TAG, "mOrigin name: "+mOriginName + "mDestination name= "+ mDestinationName);
                    Log.d(TAG, "mOrigin address: "+mOriginAddress + "mDestination address= "+ mDestinationAddress);

                }

                Log.d(TAG, "get setDestination clicked,Origin= "+ trip.getOrigin()+"Destination= "+trip.getDestination()+"Polyline= "+trip.getPolyline());
                mSearchFiltersFragment  = SearchFiltersFragment.newInstance();//new SearchFiltersFragment();
                FragmentTransaction searchFiltersTransaction = fragmentManager.beginTransaction();
                searchFiltersTransaction.add(R.id.search_map_layout, mSearchFiltersFragment,"searchFiltersFrag");
                searchFiltersTransaction.addToBackStack("searchFiltersFrag");
                searchFiltersTransaction.commit();

            }
        });

        autocompleteOrigin = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.search_autocomplete_origin);
        autocompleteOrigin.setHint(getString(R.string.origin));

        autocompleteOrigin.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(Place place) {
                Log.i(TAG, "Place: " + place.getName());
                Log.i(TAG, "Placeg etId: " + place.getId());
                Log.i(TAG, "Place Address: " + place.getAddress());
                Log.i(TAG, "Place LatLng: " + place.getLatLng());
                Log.i(TAG, "Place Locale: " + place.getLocale());
                Log.i(TAG, "Place Attributions: " + place.getAttributions());
                mOrigin = place.getLatLng();
                mOriginName = place.getName().toString();

                if (mOrigin != null){
                    displayMarker(mOrigin, "Origin");
                }
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });

        autocompleteDestination = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.search_autocomplete_destination);
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
                //displayMarker(mDestination, "Destination");
                if (mDestination != null){
                    displayMarker(mDestination, "Destination");
                }

            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
            }
        });
    } // End of onCreate


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
        mMap = googleMap;
        Log.d(TAG, "onMapReady:" + googleMap);

        if(checkPermissions() && isGooglePlayServicesAvailable()) {
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            //mMap.getUiSettings().setZoomControlsEnabled(true);
            getLastLocation();
            //startLocationUpdates();

        }

        mBoundBuilder = new LatLngBounds.Builder();

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
                            break;
                        case "Destination":
                            mDestination = marker.getPosition();
                            geoCode(mDestination, "Destination");
                            //autocompleteDestination.setText(marker.getPosition().toString());
                            break;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

    } // End of on map ready

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
            case "searchFiltersFragment":
                Log.d(TAG, "start search clicked ");
                Intent returnIntent = new Intent();
                if(search.getOrigin()!= null && search.getDestination() != null){
                    setResult(SearchActivity.RESULT_OK,returnIntent);
                }else{
                    setResult(SearchActivity.RESULT_CANCELED,returnIntent);
                }
                finish();
                break;
        }

    }

    // get last location once

    private void getLastLocation() {

        Log.d(TAG, "getLastLocation is on");

        if (checkPermissions() && isGooglePlayServicesAvailable()) {
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
                                LatLng latlang = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
                                displayMarker(latlang, "Origin");
                                mOrigin = latlang;
                            }
                        }
                    });

        }
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
            int autocomplete_height = findViewById(R.id.search_autocomplete_layout).getHeight();
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
                                ActivityCompat.requestPermissions(SearchActivity.this,
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


}
