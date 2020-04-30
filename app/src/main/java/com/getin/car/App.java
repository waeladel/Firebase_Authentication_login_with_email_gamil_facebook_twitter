package com.getin.car;

import android.content.Context;
import androidx.multidex.MultiDexApplication;

/**
 * Created on 25/03/2017.
 */

//public class App extends Application {// to enable multidex
public class App extends MultiDexApplication {

    private static Context sApplicationContext;

    @Override
    public void onCreate() {

        super.onCreate();

        sApplicationContext = getApplicationContext();

        // Initialize the SDK before executing any other operations,
        //FacebookSdk.sdkInitialize(sApplicationContext);

    }

    public static Context getContext() {
        return sApplicationContext;
        //return instance.getApplicationContext();
    }

}

