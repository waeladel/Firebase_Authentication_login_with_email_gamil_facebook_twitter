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

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.getin.car.App;
import com.getin.car.R;
import com.getin.car.authentication.FirebaseUtils;
import com.getin.car.fragments.LoginFragment;
import com.getin.car.fragments.RegisterFragment;
import com.google.android.gms.appinvite.AppInvite;
import com.google.android.gms.appinvite.AppInviteInvitationResult;
import com.google.android.gms.appinvite.AppInviteReferral;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.TwitterAuthProvider;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import java.util.List;
import java.util.concurrent.Executor;

import io.fabric.sdk.android.Fabric;


public class MainActivity extends BaseActivity implements LoginFragment.OnFragmentInteractionListener,GoogleApiClient.OnConnectionFailedListener  {

    private final static String TAG = MainActivity.class.getSimpleName();

    private EditText mEmailField;
    private EditText mPasswordField;
    private Button mLoginButton;
    private Button mRegisterButton;

    //google sign in buttons and variables
    private SignInButton mGoogleButton;

    //facebook button and managers
    private LoginButton mFacebookButton;

    //Twitter button
    private TwitterLoginButton mTwitterButton;

    private String mEmail;
    private String mPassword;

    //initialize the FirebaseAuth instance
    public static FirebaseAuth mAuth;
    public static FirebaseAuth.AuthStateListener mAuthListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity onCreate");
        // Configure Twitter SDK
        TwitterAuthConfig authConfig =  new TwitterAuthConfig(
                getString(R.string.twitter_consumer_key),
                getString(R.string.twitter_consumer_secret));
        Fabric.with(this, new Twitter(authConfig));

        // Inflate layout (must be done after Twitter is configured)
        setContentView(R.layout.activity_main);

        // Create an auto-managed GoogleApiClient with access to App Invites. got it from baseActivity
      /*  mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(AppInvite.API)
                .enableAutoManage(this, this)
                .build();*/

        // Check for App Invite invitations and launch deep-link activity if possible.
        // Requires that an Activity is registered in AndroidManifest.xml to handle
        // deep-link URLs.
        //boolean autoLaunchDeepLink = true;
        boolean autoLaunchDeepLink = false;
        AppInvite.AppInviteApi.getInvitation(mGoogleApiClient, this, autoLaunchDeepLink)
                .setResultCallback(
                        new ResultCallback<AppInviteInvitationResult>() {
                            @Override
                            public void onResult(AppInviteInvitationResult result) {
                                Log.d(TAG, "getInvitation:onResult:" + result.getStatus());
                                if (result.getStatus().isSuccess()) {
                                    // Extract information from the intent
                                    Intent intent = result.getInvitationIntent();
                                    String deepLink = AppInviteReferral.getDeepLink(intent);
                                    String invitationId = AppInviteReferral.getInvitationId(intent);
                                    Log.d(TAG, "getInvitation:deepLink:" + deepLink);
                                    Log.d(TAG, "getInvitation:invitationId:" + invitationId);

                                    // Because autoLaunchDeepLink = true we don't have to do anything
                                    // here, but we could set that to false and manually choose
                                    // an Activity to launch to handle the deep link here.
                                    // ...
                                    /*Intent congratulationIntent = new Intent(ActivityAlarmClock.this, DeepLinkActivity.class);
                                    //startIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(congratulationIntent);*/
                                    Toast.makeText(MainActivity.this, getString(R.string.deep_link_congratulation),
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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


        // [START initialize_fblogin]
        // Initialize Facebook Login button
        mCallbackManager = CallbackManager.Factory.create();
        mFacebookButton = (LoginButton) findViewById(R.id.facebook_btn);
        mFacebookButton.setReadPermissions("email", "public_profile");
        mFacebookButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
                // [START_EXCLUDE]
                //updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d(TAG, "facebook:onError", error);
                // [START_EXCLUDE]
                //updateUI(null);
                Toast.makeText(MainActivity.this, R.string.auth_failed,
                        Toast.LENGTH_SHORT).show();
                // [END_EXCLUDE]
            }
        });
        // [END initialize_fblogin]


        // [START initialize_twitter_login]
        mTwitterButton = (TwitterLoginButton) findViewById(R.id.twitter_btn);
        mTwitterButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.d(TAG, "twitterLogin:success" + result);
                handleTwitterSession(result.data);

                //Requesting a user’s emai but app must be whitelisted by Twitter first
                /*TwitterAuthClient authClient = new TwitterAuthClient();
                authClient.requestEmail(result.data, new Callback<String>() {
                    @Override
                    public void success(Result<String> result) {
                        // Do something with the result, which provides the email address
                        Log.w(TAG, "email address="+ result);
                    }
                    @Override
                    public void failure(TwitterException exception) {
                        // Do something on failure
                        Log.w(TAG, "faild to get email");
                    }
                });*/
            }

            @Override
            public void failure(TwitterException exception) {
                Log.w(TAG, "twitterLogin:failure", exception);
                //updateUI;
                Toast.makeText(MainActivity.this, R.string.auth_failed,
                        Toast.LENGTH_SHORT).show();
            }
        });
        // [END initialize_twitter_login]

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
                mIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(mIntent);
            }
        });
    }
    // [END on_create]
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "onConnectionFailed:" + connectionResult);
        //showMessage(getString(R.string.google_play_services_error));
        Toast.makeText(MainActivity.this, getString(R.string.google_play_services_error),
                Toast.LENGTH_LONG).show();
        // Sending failed or it was canceled
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
    public void onPause () {
        super.onPause ();
        Log.d(TAG, "MainActivity onPause");
        if (mProgress != null){
            mProgress.dismiss();
            Log.d(TAG, "hide mProgress onPause MainActivity");
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
                            mProgress.dismiss();
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
                    Toast.LENGTH_LONG).show();
        }
    }

    // Google sing in methods
    // Intent to open activity enables user to select one of his google accoints
    private void googleSignIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, GOOGLE_SIGN_IN_RC);
        Log.d(TAG, "signInIntent Activity started");
    }

    // Activity result after user selects the account he wants to use
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "requestCode ="+ requestCode);

        switch (requestCode){
            case GOOGLE_SIGN_IN_RC:
                // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
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
                    mProgress.dismiss();
                    Toast.makeText(MainActivity.this, R.string.auth_failed,
                            Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Google Sign In failed");
                }
                break;
            case 64206: //facebook
                Log.d(TAG, "Facebook requestCode= " + requestCode);
                // Pass the activity result back to the Facebook SDK
                mCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
            case 140:  //twitter
                // Pass the activity result to the Twitter login button.
                mTwitterButton.onActivityResult(requestCode, resultCode, data);
                break;

            default: //do twitter again just in case
                // Pass the activity result to the Twitter login button.
                mTwitterButton.onActivityResult(requestCode, resultCode, data);
                break;
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
                        mProgress.dismiss();
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "signInWithCredential", task.getException());
                            Toast.makeText(MainActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }



    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
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

                        // ...
                    }
                });
    }

    // [START auth_with_twitter]
    private void handleTwitterSession(TwitterSession session) {
        Log.d(TAG, "handleTwitterSession:" + session);
        // [START_EXCLUDE silent]
        mProgress.setMessage(getResources().getString(R.string.signing_in_progress));
        mProgress.show();
        // [END_EXCLUDE]

        AuthCredential credential = TwitterAuthProvider.getCredential(
                session.getAuthToken().token,
                session.getAuthToken().secret);

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
                        // [START_EXCLUDE]
                        mProgress.dismiss();
                        // [END_EXCLUDE]
                    }
                });
    }
    // [END auth_with_twitter]



}
