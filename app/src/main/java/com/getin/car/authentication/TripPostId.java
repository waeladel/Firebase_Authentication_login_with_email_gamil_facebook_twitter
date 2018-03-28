package com.getin.car.authentication;

import android.support.annotation.NonNull;

import com.google.firebase.firestore.Exclude;

/**
 * Created by hp on 27/03/2018.
 */

public class TripPostId {

    @Exclude
    public String TripPostId;

    public <T extends TripPostId> T withId(@NonNull final String id) {
        this.TripPostId = id;
        return (T) this;
    }
}
