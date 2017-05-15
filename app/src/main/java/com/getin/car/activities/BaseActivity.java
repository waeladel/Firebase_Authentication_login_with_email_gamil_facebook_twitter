package com.getin.car.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.internal.Utility;
import com.facebook.internal.Validate;
import com.facebook.login.widget.LoginButton;
import com.getin.car.R;
import com.getin.car.fragments.EditProfileFragment;
import com.getin.car.fragments.LoginFragment;
import com.getin.car.fragments.RegisterFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import java.util.List;

import io.fabric.sdk.android.Fabric;

/**
 * Created on 28/03/2017.
 */

public abstract class BaseActivity extends AppCompatActivity {

    private final static String TAG = BaseActivity.class.getSimpleName();

    // Get FragmentManager
    public FragmentManager fragmentManager;
    //public android.support.v4.app.FragmentTransaction fragmentTransaction;

    RegisterFragment mRegisterFragment;
    LoginFragment mLoginFragment;
    EditProfileFragment EditProfileFrag;
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
    public FirebaseDatabase database;

    //initialize the Firebase UsersReference
    public DatabaseReference UsersRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "BaseActivity onCreate");

        fragmentManager = getSupportFragmentManager();

        mProgress = new ProgressDialog(this);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        // Obtain the FirebaseDatabase instance.
        database = FirebaseDatabase.getInstance();
        UsersRef = database.getReference().child("users");

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        Log.d(TAG, "gso requestIdToken ="+ getString(R.string.default_web_client_id));
        // [END config_signin]

        // [START google Clint]
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener(){
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(BaseActivity.this, R.string.auth_failed,
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "mGoogleApiClient onConnectionFailed");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END google Clint]


    }

}
