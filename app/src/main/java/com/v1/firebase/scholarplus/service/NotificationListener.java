package com.v1.firebase.scholarplus.service;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.model.Scholarship;
import com.v1.firebase.scholarplus.model.User;
import com.v1.firebase.scholarplus.ui.MainActivity;
import com.v1.firebase.scholarplus.utils.Constants;

/**
 * Created by MacBook on 9/16/16.
 */
public class NotificationListener extends Service {
    private DatabaseReference mFirebaseDatabase,mFirebaseUser;
    public boolean mRunning;
    private String mUid;
    private Double mIpk;
    private int mSemester,mCount;

    @Override
    public void onCreate() {
        super.onCreate();

        mRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //Opening sharedpreferences
//        SharedPreferences sharedPreferences = getSharedPreferences(Constants.SHARED_PREF, MODE_PRIVATE);

        //Getting the firebase id from sharedpreferences
//        String id = sharedPreferences.getString(Constants.UNIQUE_ID, null);

        //Creating a firebase object
//        Firebase firebase = new Firebase(Constants.FIREBASE_APP + id);

        //Adding a valueevent listener to firebase
        //this will help us to  track the value changes on firebase
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mCount = 0;
        if (!mRunning && user != null) {
            mUid = user.getUid();
            mFirebaseUser = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_USERS+"/"+mUid);
            mFirebaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User data = dataSnapshot.getValue(User.class);
                    mIpk = Double.valueOf(data.getIpk());
                    mSemester = Integer.valueOf(data.getSemester());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            mFirebaseDatabase = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_PROPERTY_BEASISWA);
            Query ref = mFirebaseDatabase.orderByChild(Constants.KEY_ASAL).equalTo(Constants.VALUE_ASAL_DN);
            ref.addChildEventListener(new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    Scholarship data = dataSnapshot.getValue(Scholarship.class);
                    if (Double.valueOf(data.getKuantitatif().get(Constants.FIREBASE_PROPERTY_IPK).toString()) <= mIpk && Integer.valueOf(data.getKuantitatif().get(Constants.FIREBASE_PROPERTY_SEMESTER).toString()) <= mSemester ) {
                        mCount++;
                        String msg = "Anda memiliki "+String.valueOf(mCount)+" beasiswa yang cocok";
                        String title = "Rekomendasi Beasiswa";
                        showNotification(msg,title);
                    }

                }

                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {
                    Scholarship data = dataSnapshot.getValue(Scholarship.class);
                    if (Double.valueOf(data.getKuantitatif().get(Constants.FIREBASE_PROPERTY_IPK).toString()) <= mIpk && Integer.valueOf(data.getKuantitatif().get(Constants.FIREBASE_PROPERTY_SEMESTER).toString()) <= mSemester ) {
                        mCount--;
                        String msg = "Anda memiliki "+String.valueOf(mCount)+" beasiswa yang cocok";
                        String title = "Rekomendasi Beasiswa";
                        showNotification(msg,title);
                    }
                }

                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            return START_STICKY;
        }
        return super.onStartCommand(intent, flags, startId);
    }


    private void showNotification(String msg,String title){
        //Creating a notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);
        builder.setContentIntent(pendingIntent);
        builder.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        builder.setContentTitle(title);
        builder.setContentText(msg);
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }

}
