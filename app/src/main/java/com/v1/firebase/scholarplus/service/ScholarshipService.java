package com.v1.firebase.scholarplus.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by MacBook on 9/16/16.
 */
public class ScholarshipService extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent startServiceIntent = new Intent(context, NotificationListener.class);
        context.startService(startServiceIntent);
    }
}
