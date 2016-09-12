package com.v1.firebase.scholarplus.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.ui.BaseActivity;
import com.v1.firebase.scholarplus.ui.MainActivity;
import com.v1.firebase.scholarplus.utils.Constants;

/**
 * Created by sekartanjung on 6/5/16.
 */
public class LoginActivity extends BaseActivity {
    private static final String LOG_TAG = LoginActivity.class.getSimpleName();
    /* A dialog that is presented until the Firebase authentication finished. */
    private ProgressDialog mAuthProgressDialog;
    /* References to the Firebase */
    /* Listener for Firebase session changes */
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    private EditText mEditTextEmailInput, mEditTextPasswordInput;
    private FirebaseAuth mFirebaseAuth;
    private SharedPreferences mSharedPref;
    private SharedPreferences.Editor mSharedPrefEditor;

    /**
     * Variables related to Google Login
     */
    /* A flag indicating that a PendingIntent is in progress and prevents us from starting further intents. */
    private boolean mGoogleIntentInProgress;
    /* Request code used to invoke sign in user interactions for Google+ */
    public static final int RC_GOOGLE_LOGIN = 1;
    /* A Google account object that is populated if the user signs in with Google */
    GoogleSignInAccount mGoogleAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mSharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        mSharedPrefEditor = mSharedPref.edit();

        /**
         * Create Firebase references
         */
        mFirebaseAuth = FirebaseAuth.getInstance();

        /**
         * Link layout elements from XML and setup progress dialog
         */
        initializeScreen();

        /**
         * Call signInPassword() when user taps "Done" keyboard action
         */
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    updateUI(user);
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                    final DatabaseReference userLocation = FirebaseDatabase.getInstance().getReference(Constants.KEY_USERS+"/"+user.getUid());
                    userLocation.addListenerForSingleValueEvent(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                        /* If nothing is there ...*/
                            if (!dataSnapshot.exists()) {

                                Intent intent = new Intent(LoginActivity.this, EditProfileActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                intent.putExtra("new",true);
                                startActivity(intent);
                                finish();

                            }
                            else{
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);


                        }

                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            Log.d(LOG_TAG, getString(R.string.log_error_occurred) + firebaseError.getMessage());
                        }
                    });


                } else {
                    // User is signed out
                    Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]

                // [END_EXCLUDE]
            }
        };
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
        mEditTextPasswordInput.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {

                if (actionId == EditorInfo.IME_ACTION_DONE || keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    signInPassword();
                }
                return true;
            }
        });
    }
    public void initializeScreen() {
        mEditTextEmailInput = (EditText) findViewById(R.id.edit_text_email);
        mEditTextPasswordInput = (EditText) findViewById(R.id.edit_text_password);
        mEditTextPasswordInput.setTransformationMethod(new PasswordTransformationMethod());

        LinearLayout linearLayoutLoginActivity = (LinearLayout) findViewById(R.id.linear_layout_login_activity);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getString(R.string.progress_dialog_authenticating_with_firebase));
        mAuthProgressDialog.setCancelable(false);
        /* Setup Google Sign In */
        setupGoogleSignIn();
    }

    private void setupGoogleSignIn() {
        Button signInButton = (Button) findViewById(R.id.login_with_google);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onSignInGooglePressed(v);
            }
        });
    }
    public void onSignInGooglePressed(View view) {

        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_GOOGLE_LOGIN);
        mAuthProgressDialog.show();

    }
    private void updateUI(final FirebaseUser user) {
        String unprocessedUid=null;
        if (user != null) {

            unprocessedUid = user.getUid();
            mSharedPrefEditor.putString(Constants.KEY_ENCODED_EMAIL, unprocessedUid).apply();
            mSharedPrefEditor.putString(Constants.KEY_PROVIDER, user.getProviderId()).apply();
            /* If no user exists, make a user */


        } else {
            unprocessedUid = mSharedPref.getString(Constants.KEY_ENCODED_EMAIL, null);
        }
        mEncodedUid = unprocessedUid;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_GOOGLE_LOGIN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // ...
                mAuthProgressDialog.dismiss();
                showErrorToast("Login with google failed");
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
//        showErrorToast(credential.toString());
        mFirebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                        if (task.isSuccessful()){
                            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            if (user != null) {
                                // User is signed in
                                updateUI(user);
                                Log.d(LOG_TAG, "onAuthStateChanged:signed_in:" + user.getUid());

                            } else {
                                // User is signed out
                                Log.d(LOG_TAG, "onAuthStateChanged:signed_out");
                            }
                        }

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            Log.w(LOG_TAG, "signInWithCredential", task.getException());
                            showErrorToast(task.getException().toString());

                        }
                        // ...
                        mAuthProgressDialog.dismiss();
                    }
                });
    }
    public void signInPassword() {
        String email = mEditTextEmailInput.getText().toString();
        String password = mEditTextPasswordInput.getText().toString();

        /**
         * If email and password are not empty show progress dialog and try to authenticate
         */
        if (email.equals("")) {
            mEditTextEmailInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }

        if (password.equals("")) {
            mEditTextPasswordInput.setError(getString(R.string.error_cannot_be_empty));
            return;
        }
        mAuthProgressDialog.show();
        mFirebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Log.d(LOG_TAG, "signInWithCredential:onComplete:" + task.isSuccessful());

                // If sign in fails, display a message to the user. If sign in succeeds
                // the auth state listener will be notified and logic to handle the
                // signed in user can be handled in the listener.
                if (!task.isSuccessful()) {
                    Log.w(LOG_TAG, "signInWithCredential", task.getException());
                    showErrorToast(task.getException().toString());

                }

                // ...
                mAuthProgressDialog.dismiss();
            }
        });;
    }
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        /**
         * An unresolvable error has occurred and Google APIs (including Sign-In) will not
         * be available.
         */
        mAuthProgressDialog.dismiss();
        showErrorToast(result.toString());
    }
    private void showErrorToast(String message) {
        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_LONG).show();
    }
    public void onSignUpPressed(View view) {
        Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
        startActivity(intent);
    }

    public void onSignInPressed(View view) {
        signInPassword();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    public void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);

    }
}
