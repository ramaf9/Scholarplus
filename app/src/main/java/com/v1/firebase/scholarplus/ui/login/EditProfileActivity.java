package com.v1.firebase.scholarplus.ui.login;

import android.app.AlertDialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.model.User;
import com.v1.firebase.scholarplus.ui.BaseActivity;
import com.v1.firebase.scholarplus.ui.MainActivity;
import com.v1.firebase.scholarplus.utils.Constants;
import com.v1.firebase.scholarplus.utils.Utility;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by sekartanjung on 6/6/16.
 */
public class EditProfileActivity extends BaseActivity {
    private boolean newStatus;
    private EditText mEditUsername,mEditDOB,mEditSemester,mEditIpk;
    private Button mPickDate;
    private AutoCompleteTextView mEditCity,mEditProvince;
    private AutoCompleteTextView mEditInstitut,mEditFakultas,mEditJurusan;
    private Button editCv;
    private FirebaseDatabase mFirebaseDatabaseRef;
    private FirebaseStorage mFirebaseStorageRef;
    ArrayList<String> institut = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private static final int FILE_SELECT_CODE = 0;
    private String cvUri;
    private String userChoosenTask="";
    private static final String LOG_TAG = EditProfileActivity.class.getSimpleName();
    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editprofile);
        Intent i = this.getIntent();
        newStatus = i.getBooleanExtra("new",false);
        if (getSupportActionBar() != null && newStatus) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
            setTitle("Set up your profile");
        }else{
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            setTitle("Edit profile");
        }

        mFirebaseAuthRef = FirebaseAuth.getInstance();
        mFirebaseStorageRef = FirebaseStorage.getInstance();
        mFirebaseDatabaseRef = FirebaseDatabase.getInstance();
        initializeScreen(newStatus);

    }
    private void selectFile() {
        final CharSequence[] items = { "Choose from file",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add CV!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
//                boolean result= Utility.checkPermission(getApplicationContext());

                if (items[item].equals("Choose from file")) {
                    userChoosenTask="Take Photo";
//                    if(result)
                        fileIntent();

                }  else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void fileIntent() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select a File to Upload"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            Toast.makeText(this, "Please install a File Manager.",
                    Toast.LENGTH_SHORT).show();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    if(userChoosenTask.equals("Choose from file"))
                        fileIntent();

                } else {
                    //code for deny
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    // Get the Uri of the selected file
                    Uri uri = data.getData();


                    // Get the path
                    try {

                        final String path = Utility.getPath(getApplicationContext(),uri);
                        Log.d("paile",path);

                        Uri file = Uri.fromFile(new File(path));
                        File filesize = new File(path);
                        if (filesize.length()/1024 > 2048){
                            Toast.makeText(this, "File is too big",
                                    Toast.LENGTH_SHORT).show();
                        }
                        else{
                            mLoadingProgressDialog.show();
                            StorageReference riversRef = mFirebaseStorageRef.getReference().child(Constants.KEY_USERS+"/"+mEncodedUid+"/CV");
//                        +file.getLastPathSegment()
                            UploadTask uploadTask = riversRef.putFile(file);

                            // Register observers to listen for when the download is done or if it fails
                            uploadTask.addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle unsuccessful uploads
                                    mLoadingProgressDialog.dismiss();
                                }
                            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    mLoadingProgressDialog.dismiss();
                                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                                    cvUri = downloadUrl.getScheme()+":"+downloadUrl.getEncodedSchemeSpecificPart();
                                    editCv.setText("File selected");

                                }
                            });
                        }

                    }
                    catch (Exception e){
                        Log.d("Pail",e.getMessage());
                    }


                    // Get the file instance
                    // File file = new File(path);
                    // Initiate the upload
                }
                break;
        }

    }
    private void initializeScreen(boolean newStatus){
        mEditUsername = (EditText) findViewById(R.id.edit_text_username);
        mEditDOB = (EditText)findViewById(R.id.edit_text_dob);
        mEditSemester = (EditText) findViewById(R.id.edit_text_semester);
        mEditIpk = (EditText) findViewById(R.id.edit_text_ipk);
        mEditInstitut = (AutoCompleteTextView) findViewById(R.id.edit_text_institut);
        mEditFakultas = (AutoCompleteTextView) findViewById(R.id.edit_text_fakultas);
        mEditJurusan = (AutoCompleteTextView) findViewById(R.id.edit_text_jurusan);
        mEditCity = (AutoCompleteTextView) findViewById(R.id.edit_text_city);
        mEditProvince = (AutoCompleteTextView)findViewById(R.id.edit_text_province);
        editCv = (Button) findViewById(R.id.btnCv);
        mPickDate = (Button)findViewById(R.id.pick_date);



        mEditInstitut.addTextChangedListener(new GenericTextWatcher(mEditInstitut,Constants.FIREBASE_PROPERTY_INSTITUT));
        mEditFakultas.addTextChangedListener(new GenericTextWatcher(mEditFakultas,Constants.FIREBASE_PROPERTY_FAKULTAS));
        mEditJurusan.addTextChangedListener(new GenericTextWatcher(mEditJurusan,Constants.FIREBASE_PROPERTY_JURUSAN));
        mEditCity.addTextChangedListener(new GenericTextWatcher(mEditCity,Constants.FIREBASE_PROPERTY_CITY));
        mEditProvince.addTextChangedListener(new GenericTextWatcher(mEditProvince,Constants.FIREBASE_PROPERTY_PROVINCE));
        editCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFile();
            }
        });

        mPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setDOB();
            }
        });
        final FirebaseUser user = mFirebaseAuthRef.getCurrentUser();
        final DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(Constants.KEY_USERS+"/"+user.getUid());

        databaseUser.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    /* If nothing is there ...*/
                    User currentUser = dataSnapshot.getValue(User.class);
                    if (dataSnapshot.getValue() != null) {
                        mEditUsername.setText(currentUser.getName());
                        mEditDOB.setText(currentUser.getDob());
                        mEditProvince.setText(currentUser.getProvince());
                        mEditCity.setText(currentUser.getCity());

                        mEditInstitut.setText(currentUser.getInstitut());
                        mEditFakultas.setText(currentUser.getFakultas());
                        mEditJurusan.setText(currentUser.getJurusan());
                        mEditSemester.setText(currentUser.getSemester());
                        mEditIpk.setText(currentUser.getIpk());

                        mEncodedUid = dataSnapshot.getKey();
                    }
                    else{
                        if (user.getDisplayName() != null){
                            mEditUsername.setText(user.getDisplayName());
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError firebaseError) {
                    Log.d(LOG_TAG, getString(R.string.log_error_occurred) + firebaseError.getMessage());
                }
            });


    }
    private String upperCasing(String s){
        String result = "";
        if (!s.isEmpty()){
            result = s.substring(0,1).toUpperCase()+s.substring(1,s.length()).toLowerCase();
        }
        return result;
    }
    public void onSavePressed(View view){
        if (adapter != null)
            adapter.clear();
        if (!mEditIpk.getText().toString().matches("") && !mEditSemester.getText().toString().matches("") && !mEditInstitut.getText().toString().matches("") && !mEditFakultas.getText().toString().matches("")
                && !mEditJurusan.getText().toString().matches("")) {
            final FirebaseUser user = mFirebaseAuthRef.getCurrentUser();
            final DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(Constants.KEY_USERS + "/" + user.getUid());

            HashMap<String, Object> profile = new HashMap<>();
            try {

                profile.put(Constants.FIREBASE_PROPERTY_NAME, upperCasing(mEditUsername.getText().toString()));
                profile.put(Constants.FIREBASE_PROPERTY_EMAIL, user.getEmail());
                profile.put(Constants.FIREBASE_PROPERTY_DOB, mEditDOB.getText().toString());
                profile.put(Constants.FIREBASE_PROPERTY_PROVINCE, upperCasing(mEditProvince.getText().toString()));
                profile.put(Constants.FIREBASE_PROPERTY_CITY, upperCasing(mEditCity.getText().toString()));

                profile.put(Constants.FIREBASE_PROPERTY_INSTITUT, upperCasing(mEditInstitut.getText().toString()));
                profile.put(Constants.FIREBASE_PROPERTY_FAKULTAS, upperCasing(mEditFakultas.getText().toString()));
                profile.put(Constants.FIREBASE_PROPERTY_JURUSAN, upperCasing(mEditJurusan.getText().toString()));
                profile.put(Constants.FIREBASE_PROPERTY_SEMESTER, mEditSemester.getText().toString());
                profile.put(Constants.FIREBASE_PROPERTY_IPK, mEditIpk.getText().toString());

                profile.put(Constants.FIREBASE_PROPERTY_CV, cvUri);


            } catch (Exception e) {
                e.printStackTrace();

            }


            if (newStatus == false) {
                databaseUser.updateChildren(profile).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            task.getResult();
                            task.getException();
                        }
                    }
                });
            } else {
                HashMap<String, Object> timestampJoined = new HashMap<>();
                timestampJoined.put(Constants.FIREBASE_PROPERTY_TIMESTAMP, ServerValue.TIMESTAMP);

                User newUser = new User(profile, timestampJoined);

                databaseUser.setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Intent intent = new Intent(EditProfileActivity.this, MainActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            task.getResult();
                            task.getException();
                        }
                    }
                });
            }

        }
        else{
            Toast.makeText(this, "Tolong isi data yang kosong", Toast.LENGTH_SHORT).show();
            return;
        }
    }
    public void setDOB(){
        DialogFragment newFragment = new Datepicker();
        newFragment.show(EditProfileActivity.this.getFragmentManager(), "Datepicker");
    }
    public void selectDOB(String s){
        mEditDOB.setText(s);
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private class GenericTextWatcher implements TextWatcher{

        private AutoCompleteTextView view;
        private String orderChild, mInput;
        private GenericTextWatcher(AutoCompleteTextView view,String child) {
            this.view = view;
            this.orderChild = child;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

        public void afterTextChanged(Editable editable) {
            mInput = editable.toString();
            if (adapter != null) adapter.clear();
            if (mInput.equals("")){
                Log.d("gak ada input","ya benar");
                view.setAdapter(null);
            }else{
                adapter = new ArrayAdapter<String>(EditProfileActivity.this,android.R.layout.simple_list_item_activated_1,institut);


                Query ref = mFirebaseDatabaseRef.getReference(Constants.KEY_USERS).orderByChild(orderChild)
                        .startAt(mInput.toUpperCase()).endAt(mInput.toLowerCase() + "~").limitToFirst(5);

                ref.addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                        User user = dataSnapshot.getValue(User.class);
                        switch(orderChild){
                            case Constants.FIREBASE_PROPERTY_INSTITUT:
                                if (user.getInstitut().length() > mInput.length() && !institut.contains(user.getInstitut()))
                                    institut.add(user.getInstitut());
                                    break;
                            case Constants.FIREBASE_PROPERTY_FAKULTAS:
                                if (user.getFakultas().length() > mInput.length() && !institut.contains(user.getFakultas()))
                                    institut.add(user.getFakultas());
                                break;
                            case Constants.FIREBASE_PROPERTY_JURUSAN:
                                if (user.getJurusan().length() > mInput.length() && !institut.contains(user.getJurusan()))
                                    institut.add(user.getJurusan());
                                break;
                            case Constants.FIREBASE_PROPERTY_PROVINCE:
                                if (user.getProvince().length() > mInput.length() && !institut.contains(user.getProvince()))
                                    institut.add(user.getProvince());
                                break;
                            case Constants.FIREBASE_PROPERTY_CITY:
                                if (user.getCity().length() > mInput.length() && !institut.contains(user.getCity()))
                                    institut.add(user.getCity());
                                break;
                        }
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                view.setAdapter(adapter);

            }
        }
    }

}
