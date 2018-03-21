package com.getin.car.activities;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.getin.car.R;
import com.getin.car.authentication.Trip;
import com.getin.car.fragments.CompleteProfileFragment;
import com.getin.car.fragments.EditProfileFragment;
import com.getin.car.fragments.LoginFragment;
import com.getin.car.fragments.RegisterFragment;
import com.getin.car.fragments.TripInfoFragment;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.Places;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created on 28/03/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private final static String TAG = BaseActivity.class.getSimpleName();

    // Get FragmentManager
    public FragmentManager fragmentManager;
    //public android.support.v4.app.FragmentTransaction fragmentTransaction;

    public RegisterFragment mRegisterFragment;
    public LoginFragment mLoginFragment;
    public CompleteProfileFragment completeProfileFrag;
    public EditProfileFragment editProfileFragment;
    public TripInfoFragment tripInfoFragment;

    //List<Fragment> fragmentsList;

    public ProgressDialog mProgress;

    //google sign in buttons and variables
    public static final int GOOGLE_SIGN_IN_RC = 9001; //for google sing in
    public GoogleApiClient mGoogleApiClient;

    //facebook button and managers
    public CallbackManager mCallbackManager;

    //initialize the Firebase Analytics
    public FirebaseAnalytics mFirebaseAnalytics;

    //initialize the Firebase Database
    //public FirebaseDatabase database;
    public FirebaseFirestore db;

    //initialize the Firebase UsersReference
    //public DatabaseReference UsersRef;
    //public DocumentReference UserDocRef ;
    public CollectionReference usersColRef;
    public static Trip trip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "BaseActivity onCreate");

        fragmentManager = getSupportFragmentManager();

        mProgress = new ProgressDialog(this);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Obtain the FirebaseDatabase instance.
        /*database = FirebaseDatabase.getInstance();
        UsersRef = database.getReference().child("users");*/
        db =  FirebaseFirestore.getInstance();
        usersColRef = db.collection("users");


        // [START config_signin]
        // Configure Google Sign In
        /*GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        Log.d(TAG, "gso requestIdToken ="+ getString(R.string.default_web_client_id));*/
        // [END config_signin]

        //create trip object
        trip = new Trip();

        // [START google Clint]
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .addApi(Places.GEO_DATA_API)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener(){
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(BaseActivity.this, R.string.auth_failed,
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "mGoogleApiClient onConnectionFailed");
                    }
                })
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END google Clint]

    }

}
