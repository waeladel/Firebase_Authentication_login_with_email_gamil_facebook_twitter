package com.getin.car.activities;


import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.getin.car.App;
import com.getin.car.R;
import com.getin.car.authentication.FirebaseUtils;
import com.getin.car.fragments.LoginFragment;
import com.getin.car.fragments.RegisterFragment;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.List;
import java.util.concurrent.Executor;


public class MainActivity extends BaseActivity implements LoginFragment.OnFragmentInteractionListener  {

    private final static String TAG = MainActivity.class.getSimpleName();

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private Button mRegisterButton;

    //google sign in buttons and variables
    private SignInButton mGoogleButton;
    private static final int RC_SIGN_IN = 9001; //for google sing in
    private GoogleApiClient mGoogleApiClient;


    private String mEmail;
    private String mPassword;

    public FirebaseAnalytics mFirebaseAnalytics;
    //initialize the FirebaseAuth instance
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Obtain the FirebaseAnalytics instance.
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        mEmailField  = (EditText) findViewById(R.id.email_address_editText);
        mEmailField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "Editable email= "+ editable.toString());
                String EditableEmail = editable.toString().trim();
                if(TextUtils.isEmpty(EditableEmail)){
                    mEmailField.setError(getResources().getString(R.string.required));
                }else if(!TextUtils.isEmpty(EditableEmail)&& !FirebaseUtils.isValidEmail(EditableEmail)){
                    mEmailField.setError(getResources().getString(R.string.email_is_not_valid));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });


        mPasswordField = (EditText) findViewById(R.id.password_editText);
        mPasswordField.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "Editable password= "+ editable.toString());
                String EditablePassword = editable.toString().trim();
                if(TextUtils.isEmpty(EditablePassword)){
                    mPasswordField.setError(getResources().getString(R.string.required));
                }else if(!TextUtils.isEmpty(EditablePassword)&& !FirebaseUtils.isValidPassword(EditablePassword)){
                    mPasswordField.setError(getResources().getString(R.string.password_must_be_six));
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // other stuffs
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // other stuffs
            }
        });

        mLoginButton = (Button) findViewById(R.id.Login_btn);
        mRegisterButton = (Button) findViewById(R.id.create_account_btn);
        mGoogleButton = (SignInButton) findViewById(R.id.google_btn);

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onButtonPressed("LoginClicked");
                SignInWithEmail();
                Log.d(TAG, "mLoginButton clicked ");
            }
        });

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onButtonPressed("RegisterClicked");
                Log.d(TAG, "mRegisterButton clicked ");
                //create fragment with Register Fragment
                mRegisterFragment  = new RegisterFragment();
                //fragmentManager.beginTransaction().replace(R.id.content_main, mRegisterFragment,"mRegisterFragment").commit();
                FragmentTransaction RegisterTransaction =fragmentManager.beginTransaction();
                RegisterTransaction.add(R.id.content_main, mRegisterFragment,"mRegisterFragment");
                RegisterTransaction.addToBackStack("RegisterClicked");
                RegisterTransaction.commit();
            }
        });

        mGoogleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //onButtonPressed("mGoogleClicked");
                googleSignIn();
                Log.d(TAG, "mGoogleButton clicked ");
            }
        });

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
                    // lets Remove all fragments if any
                    /*fragmentsList = fragmentManager.getFragments();
                    Log.d(TAG, "signed_in_fragmentsList:" + fragmentsList);
                    if (fragmentsList != null) {
                        for (Fragment fragment : fragmentsList) {
                            fragmentManager.beginTransaction().remove(fragment).commit();
                        }
                    }*/
                    //Removing mLoginFragment if Exists
                    /*mLoginFragment = (LoginFragment) fragmentManager.findFragmentByTag("mLoginFragment");
                    if (mLoginFragment != null){
                        fragmentManager.beginTransaction().remove(mLoginFragment).commit() ;                   //RegisterTransaction.addToBackStack(null);
                    }*/
                    //Removing mRegisterFragment if Exists
                    /*mRegisterFragment = (RegisterFragment) fragmentManager.findFragmentByTag("mRegisterFragment");
                    if (mRegisterFragment != null){
                        fragmentManager.beginTransaction().remove(mRegisterFragment).commit() ;                   //RegisterTransaction.addToBackStack(null);
                    }*/
                    Intent mIntent = new Intent(MainActivity.this, ProfileActivity.class);
                    //mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    //mIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    startActivity(mIntent);
                    finish();

                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    /*mLoginFragment  = new LoginFragment();
                    //fragmentManager.beginTransaction().add(R.id.content_main, mLoginFragment,"mLoginFragment").commit();
                    FragmentTransaction LoginTransaction = fragmentManager.beginTransaction();
                    LoginTransaction.add(R.id.content_main, mLoginFragment,"mLoginFragment");
                    //LoginTransaction.addToBackStack(null);
                    LoginTransaction.commit();*/
                }
                // ...
            }
        };

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
                        Toast.makeText(MainActivity.this, R.string.auth_failed,
                                Toast.LENGTH_SHORT).show();
                        Log.d(TAG, "mGoogleApiClient onConnectionFailed");
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        // [END google Clint]

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                //FirebaseUtils.SignInWithEmail("sdfsdf","fasfasf");
                /*mLoginFragment  = new LoginFragment();
                FragmentTransaction fragmentTransaction2 =fragmentManager.beginTransaction();
                fragmentTransaction2.add(R.id.content_main, mLoginFragment,"mLoginFragment");
                fragmentTransaction2.commit();*/

                //Removing mLoginFragment if Exists
                /*mLoginFragment = (LoginFragment) fragmentManager.findFragmentByTag("mLoginFragment");
                if (mLoginFragment != null){
                    fragmentManager.beginTransaction().remove(mLoginFragment).commit() ;                   //RegisterTransaction.addToBackStack(null);
                }
                //Removing mRegisterFragment if Exists
                mRegisterFragment = (RegisterFragment) fragmentManager.findFragmentByTag("mRegisterFragment");
                if (mRegisterFragment != null){
                    fragmentManager.beginTransaction().remove(mRegisterFragment).commit() ;                   //RegisterTransaction.addToBackStack(null);
                }*/
                Log.d(TAG, "fragmentManager BackStackEntryCount ="+ fragmentManager.getBackStackEntryCount());
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    FragmentManager.BackStackEntry first = fragmentManager.getBackStackEntryAt(0);
                    Log.d(TAG, "first BackStackEntry ="+ first);
                    //fragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                    //fragmentManager.popBackStack(first.getId(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
                }
                Intent mIntent = new Intent(MainActivity.this, ProfileActivity.class);
                //mIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(mIntent);
            }
        });
    }

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
    public void onFragmentInteraction(String FragmentName) {// listens to login fragments buttons
        Log.d(TAG, "FragmentName = "+ FragmentName);

        switch (FragmentName){
            case "RegisterClicked":
                //Replace current fragment with Register Fragment
                /*mRegisterFragment  = new RegisterFragment();
                //fragmentManager.beginTransaction().replace(R.id.content_main, mRegisterFragment,"mRegisterFragment").commit();
                FragmentTransaction RegisterTransaction =fragmentManager.beginTransaction();
                RegisterTransaction.replace(R.id.content_main, mRegisterFragment,"mRegisterFragment");
                //RegisterTransaction.addToBackStack("RegisterClicked");
                RegisterTransaction.commit();*/
                break;
            /*case "LoginClicked":
                //Replace current fragment with Register Fragment
                LoginFragment mLoginFragment = (LoginFragment) fragmentManager.findFragmentByTag("mLoginFragment");
                FragmentTransaction RemoveLoginTransaction =fragmentManager.beginTransaction();
                //RemoveLoginTransaction.addToBackStack(null);
                if (mLoginFragment != null){
                    RemoveLoginTransaction.remove(mLoginFragment);
                    RemoveLoginTransaction.commit();
                }
                break;*/
            default:
                break;
        }
    }
    // Sing in method using email and password
    private void SignInWithEmail() {
        mEmail = mEmailField.getText().toString().trim();
        mPassword = mPasswordField.getText().toString().trim();

        if(FirebaseUtils.isValidEmail(mEmail) && FirebaseUtils.isValidPassword(mPassword)){
            Log.d(TAG, "Both are not empty");
            mProgress.setMessage(getResources().getString(R.string.signing_in_progress));
            mProgress.show();

            mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());
                            mProgress.hide();
                            // If sign in fails, display a message to the user. If sign in succeeds
                            // the auth state listener will be notified and logic to handle the
                            // signed in user can be handled in the listener.
                            if (!task.isSuccessful()) {
                                Log.w(TAG, "signInWithEmail:failed", task.getException());
                                Toast.makeText(MainActivity.this, R.string.auth_failed,
                                        Toast.LENGTH_SHORT).show();
                            }else{
                                Log.d(TAG, "signInWithEmail:succeeded");
                                //onButtonPressed("LoginClicked");
                            }
                        }
                    });

        }else{
            Log.d(TAG, "Both are empty");
            Toast.makeText(MainActivity.this, R.string.empty_email_password,
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Google sing in methods
    // Intent to open activity enables user to select one of his google accoints
    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.d(TAG, "signInIntent Activity started");
    }

    // Activity result after user selects the account he wants to use
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            mProgress.setMessage(getResources().getString(R.string.signing_in_progress));
            mProgress.show();
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
                Log.d(TAG, "Google Sign In was successful lets Auth");
            } else {
                // Google Sign In failed, update UI appropriately
                mProgress.hide();
                Toast.makeText(MainActivity.this, R.string.auth_failed,
                        Toast.LENGTH_SHORT).show();
                Log.d(TAG, "Google Sign In failed");
            }
        }
    }

    //After Successfully login we need to authenticate the user with firebase to trigger the listener
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }
                        mProgress.hide();
                    }
                });
    }



}
