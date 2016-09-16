package com.v1.firebase.scholarplus.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.service.NotificationListener;
import com.v1.firebase.scholarplus.ui.login.LoginActivity;
import com.v1.firebase.scholarplus.ui.login.RegisterActivity;
import com.v1.firebase.scholarplus.utils.Constants;

/**
 * Created by sekartanjung on 6/5/16.
 */
public abstract class BaseActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener {
    protected String mProvider, mEncodedUid;
    /* Client used to interact with Google APIs. */
    protected GoogleApiClient mGoogleApiClient;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    protected FirebaseAuth mFirebaseAuthRef;
    protected SharedPreferences sp;
    protected ProgressDialog mLoadingProgressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFirebaseAuthRef = FirebaseAuth.getInstance();
        startService(new Intent(this, NotificationListener.class));

        mLoadingProgressDialog = new ProgressDialog(this);
        mLoadingProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mLoadingProgressDialog.setMessage("please be patient");
        mLoadingProgressDialog.setCancelable(false);

        /* Setup the Google API object to allow Google logins */
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//
//        /**
//         * Build a GoogleApiClient with access to the Google Sign-In API and the
//         * options specified by gso.
//         */
//
//        /* Setup the Google API object to allow Google+ logins */
//        mGoogleApiClient = new GoogleApiClient.Builder(this)
//                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
//                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
//                .build();
        try {
            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    // .requestEmail()
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    // .requestScopes(new Scope(Scopes.DRIVE_APPFOLDER))
                    // .requestServerAuthCode(getString(R.string.server_client_id), false)
                    .build();
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        } catch (IllegalStateException e) {
            Log.e("IllegalStateException ", e.toString());
        }

        /**
         * Getting mProvider and mEncodedEmail from SharedPreferences
         */
        sp = PreferenceManager.getDefaultSharedPreferences(BaseActivity.this);
        /* Get mEncodedEmail and mProvider from SharedPreferences, use null as default value */
        mEncodedUid = sp.getString(Constants.KEY_ENCODED_EMAIL, null);
        mProvider = sp.getString(Constants.KEY_PROVIDER, null);


        if (!((this instanceof LoginActivity) || (this instanceof RegisterActivity))) {

            mAuthListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth authData) {
                     /* The user has been logged out */
                    FirebaseUser user = mFirebaseAuthRef.getCurrentUser();
                    if (user == null) {
                        Log.d("DIDALAME","NULL");
                        /* Clear out shared preferences */
                        SharedPreferences.Editor spe = sp.edit();
                        spe.putString(Constants.KEY_ENCODED_EMAIL, null);
                        spe.putString(Constants.KEY_PROVIDER, null);
                        takeUserToLoginScreenOnUnAuth();
                    }
                    Log.d("DIDALAME","LOGIN");
                }
            };
            mFirebaseAuthRef.addAuthStateListener(mAuthListener);
        }

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (item != null && item.getItemId() == android.R.id.home) {
//            if (drawer.isDrawerOpen(Gravity.RIGHT)) {
//                drawer.closeDrawer(Gravity.RIGHT);
//                return true;
//            } else {
//                drawer.openDrawer(Gravity.RIGHT);
//                return true;
//            }
//        }

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            logout();
            return true;
        }
        else if (id == android.R.id.home){
            onBackPressed();
            return true;

        }

        return super.onOptionsItemSelected(item);
    }
    private void takeUserToProfile(){
        Intent intent = new Intent(BaseActivity.this, MainActivity.class);
        startActivity(intent);
        finish();

    }
    private void takeUserToLoginScreenOnUnAuth() {
        /* Move user to LoginActivity, and remove the backstack */
        Intent intent = new Intent(BaseActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        /* Cleanup the AuthStateListener */
        if (!((this instanceof LoginActivity) || (this instanceof RegisterActivity))) {
            mFirebaseAuthRef.removeAuthStateListener(mAuthListener);
        }

    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }
    protected void logout() {
        mFirebaseAuthRef.signOut();
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        /* Logout if mProvider is not null  */

    }
    @Override
    public void onStart() {
        super.onStart();
//        mFirebaseAuthRef.addAuthStateListener(mAuthListener);
    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if (mAuthListener != null) {
//            mFirebaseAuthRef.removeAuthStateListener(mAuthListener);
//        }
//    }

}
