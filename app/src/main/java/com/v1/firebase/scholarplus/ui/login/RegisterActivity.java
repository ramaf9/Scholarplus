package com.v1.firebase.scholarplus.ui.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.ui.BaseActivity;
import com.v1.firebase.scholarplus.utils.Constants;

/**
 * Created by sekartanjung on 6/5/16.
 */
public class RegisterActivity extends BaseActivity {
    private static final String LOG_TAG = RegisterActivity.class.getSimpleName();
    private ProgressDialog mAuthProgressDialog;
    private FirebaseAuth mFirebaseRef;
    private EditText mEditTextEmailCreate,mEditTextPasswordCreate,mEditTextPassword2Create;
    private String mUserEmail, mPassword,mPassword2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mFirebaseRef = FirebaseAuth.getInstance();
        initializeScreen();
    }
    public void initializeScreen() {
        mEditTextEmailCreate = (EditText) findViewById(R.id.edit_text_email_create);
        mEditTextPasswordCreate = (EditText) findViewById(R.id.edit_text_password_create);
        mEditTextPassword2Create = (EditText) findViewById(R.id.edit_text_password2_create);
        LinearLayout linearLayoutCreateAccountActivity = (LinearLayout) findViewById(R.id.linear_layout_create_account_activity);
//        initializeBackground(linearLayoutCreateAccountActivity);

        /* Setup the progress dialog that is displayed later when authenticating with Firebase */
        mAuthProgressDialog = new ProgressDialog(this);
        mAuthProgressDialog.setTitle(getResources().getString(R.string.progress_dialog_loading));
        mAuthProgressDialog.setMessage(getResources().getString(R.string.progress_dialog_check_inbox));
        mAuthProgressDialog.setCancelable(false);
    }

    public void onCreateAccountPressed(View view) {
        mUserEmail = mEditTextEmailCreate.getText().toString().toLowerCase();
        mPassword = mEditTextPasswordCreate.getText().toString();
        mPassword2 = mEditTextPassword2Create.getText().toString();

        /**
         * Check that email and user name are okay
         */
        boolean validEmail = isEmailValid(mUserEmail);
        boolean validPassword = isPasswordValid(mPassword,mPassword2);
        if (!validEmail  || !validPassword) return;

        /**
         * If everything was valid show the progress dialog to indicate that
         * account creation has started
         */
        mAuthProgressDialog.show();

        /**
         * Create new user with specified email and password
         */
        mFirebaseRef.createUserWithEmailAndPassword(mUserEmail, mPassword)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(LOG_TAG, "createUserWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            showErrorToast(task.getException().toString());
                        } else {
                            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(RegisterActivity.this);
                            SharedPreferences.Editor spe = sp.edit();



                            /**
                             * Save name and email to sharedPreferences to create User database record
                             * when the registered user will sign in for the first time
                             */
                            spe.putString(Constants.KEY_SIGNUP_EMAIL, mUserEmail).apply();

                            showErrorToast("A confirmation link has been sent to your email");
                        }
                        mAuthProgressDialog.dismiss();

                        // ...
                    }
                });
    }


    private boolean isEmailValid(String email) {
        boolean isGoodEmail =
                (email != null && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches());
        if (!isGoodEmail) {
            mEditTextEmailCreate.setError(String.format(getString(R.string.error_invalid_email_not_valid),
                    email));
            return false;
        }
        return isGoodEmail;
    }
    private boolean isPasswordValid(String password,String password2) {
        if (password.equals("") || password.length() < 6 ) {
            mEditTextPasswordCreate.setError(getResources().getString(R.string.error_invalid_password_not_valid));
            return false;
        }
        if (!password.equals(password2)){
            mEditTextPasswordCreate.setError(getResources().getString(R.string.error_password_not_matches));
            return false;
        }
        return true;
    }


    private void showErrorToast(String message) {
        Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_LONG).show();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
