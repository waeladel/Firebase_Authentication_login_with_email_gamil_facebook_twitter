package com.getin.car.activities;

import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.getin.car.Manifest;
import com.getin.car.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.ErrorDialogFragment;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.GooglePlayServicesUtil;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class PostActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener,
        RoutingListener {

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
    SupportMapFragment mapFragment;

    private static volatile LatLng mOrigin ;
    private static volatile LatLng mDestination ;

    private List<Polyline> polylines;
    //private List<String> MarkersList;

    private static final int[] COLORS = new int[]{R.color.colorGreen,R.color.colorBlue,R.color.colorRed,R.color.colorAccent,R.color.primary_dark_material_light};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Init view //

        polylines = new ArrayList<>();
        //MarkersList = new ArrayList<>();

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
                //displayMarker(mDestination, "Destination");
                if (mOrigin != null && mDestination != null){
                    displayMarker(mDestination, "Destination");
                    getRoutes(mOrigin, mDestination);
                }else{

                    if (mDestination != null){
                        LatLng currentOrigin = new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude());
                        Log.i(TAG, "Place mLastLocation: " + currentOrigin);
                        getRoutes(currentOrigin, mDestination);
                        displayMarker(mDestination, "Destination");
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

    }




    @Override
    protected void onResume() {
        super.onResume();
        isGooglePlayServicesAvailable();
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
                        .title(getString(R.string.me))
                        .icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_front_car_black))
                        .draggable(true)
                        .snippet("Kiel is cool")

                );

                mOriginMarker.setTag("Origin");

                break;
            case "Destination":
                if (mDestinationMarker != null){
                    mDestinationMarker.remove();// remove existing marker
                }
                mDestinationMarker = mMap.addMarker(new MarkerOptions()
                        .position(latlang)
                        .title(getString(R.string.me))
                        //.icon(BitmapDescriptorFactory.fromResource(R.mipmap.ic_front_car_black))
                        .draggable(true)
                        .snippet("Kiel is cool")

                );

                mDestinationMarker.setTag("Destination");

                break;
        }

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latlang,15.0f));
    }

    private void getRoutes(LatLng origin, LatLng destination) {

        Routing routing = new Routing.Builder()
                .travelMode(AbstractRouting.TravelMode.DRIVING)
                .withListener(this)
                .alternativeRoutes(true)
                .waypoints(origin, destination)
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
                            autocompleteOrigin.setText(marker.getPosition().toString());
                            if (mOrigin != null && mDestination != null){
                                getRoutes(mOrigin, mDestination);
                            }
                            break;
                        case "Destination":
                            mDestination = marker.getPosition();
                            autocompleteDestination.setText(marker.getPosition().toString());
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
                int strokeColor = polyline.getColor() ^ 0x0000CC00;
                polyline.setColor(strokeColor);
                //polyline.getZIndex()
                Log.i(TAG, "Polyline points @ " + polyline.getPoints());
                Log.i(TAG, "Polyline getId @ " + polyline.getId());
                Log.i(TAG, "polyline getZIndex() @ " + polyline.getZIndex());

                // loop throw all polylines array to set there ZIndex to 0 and colors to grey except selected one
                for (int i = 0; i <polylines.size(); i++) {
                    polylines.get(i).setZIndex(0.0f);
                    polylines.get(i).setColor(ContextCompat.getColor(PostActivity.this, R.color.colorGrey));

                    if (polyline.getId().equals(polylines.get(i).getId())){
                        polyline.setZIndex(1.0f);
                        polyline.setColor(ContextCompat.getColor(PostActivity.this, R.color.colorAccent));
                        Log.i(TAG, "polyline setZIndex= " +polyline.getZIndex());

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
            }else{
                polyOptions.color(ContextCompat.getColor(this,R.color.colorGrey));
            }
            polyOptions.width(10 + i * 3);
            polyOptions.clickable(true);
            polyOptions.addAll(route.get(i).getPoints());
            Polyline polyline = mMap.addPolyline(polyOptions);
            polylines.add(polyline);

            Log.d(TAG,"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue());
            Toast.makeText(getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
        }


    }

    @Override
    public void onRoutingCancelled() {
        Log.i(TAG, "Routing was cancelled.");
    }
}

