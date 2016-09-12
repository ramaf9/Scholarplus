package com.v1.firebase.scholarplus;


import com.google.firebase.database.FirebaseDatabase;
/**
 * Created by rama on 6/5/16.
 */
public class ScholarplusApplication extends android.app.Application {
    @Override
    public void onCreate() {
        super.onCreate();
        /* Initialize Firebase */
        FirebaseDatabase.getInstance().setPersistenceEnabled(true );
    }
}
