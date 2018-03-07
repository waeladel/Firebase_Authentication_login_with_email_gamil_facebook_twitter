package com.getin.car.authentication;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.getin.car.App;
import com.getin.car.R;
import com.getin.car.activities.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Created on 23/03/2017.
 */

abstract public class FirebaseUtils {

    private final static String TAG = FirebaseUtils.class.getSimpleName();

    Context context = App.getContext();

    //Declare the FirebaseAuth and AuthStateListener objects.

    //initialize the FirebaseAuth instance
    private static FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private static FirebaseAuth.AuthStateListener mAuthListener;


    public static void SignInWithEmail(String email, String password) {

        Toast.makeText(App.getContext(), password,
                Toast.LENGTH_SHORT).show();
    }

    public static boolean isValidEmail(CharSequence email) {
        if (TextUtils.isEmpty(email)) {
            return false;
        } else {
            return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
        }
    }

    public static boolean isValidPassword(CharSequence password) {

        if (TextUtils.isEmpty(password) || password.length()< 6) {
            Log.d(TAG, "password length= "+ password.length());
            return false;
        } else {
            return true;
        }
    }

    public static boolean isValidName(CharSequence name) {

        if (TextUtils.isEmpty(name) || name.length()<= 1){
            Log.d(TAG, "name length= "+ name.length());
            return false;
        } else {
            return true;
        }
    }

}
