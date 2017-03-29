package com.getin.car;

import android.app.Application;
import android.content.Context;

/**
 * Created on 25/03/2017.
 */

public class App extends Application {

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

