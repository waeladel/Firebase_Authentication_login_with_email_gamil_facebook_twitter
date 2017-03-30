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

import com.facebook.internal.Utility;
import com.facebook.internal.Validate;
import com.getin.car.R;
import com.getin.car.fragments.LoginFragment;
import com.getin.car.fragments.RegisterFragment;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

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
    //List<Fragment> fragmentsList;

    public ProgressDialog mProgress;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "BaseActivity onCreate");

        fragmentManager = getSupportFragmentManager();

        mProgress = new ProgressDialog(this);


    }

}
