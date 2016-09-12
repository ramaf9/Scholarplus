package com.v1.firebase.scholarplus.ui.login.profiledetail;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.NavUtils;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.ui.BaseActivity;
import com.v1.firebase.scholarplus.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created by MacBook on 6/25/16.
 */
public class AddPdportfolioActivity extends BaseActivity {
    private TextView judul;
    private EditText deskripsi;
    private ImageView createdBy,photo;
    private FirebaseDatabase mFirebaseDatabaseRef;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private Button sendBtn;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_portfolio);
        judul = (TextView)findViewById(R.id.tv_judul);
        createdBy = (ImageView) findViewById(R.id.created_by);
        photo = (ImageView)findViewById(R.id.ivFeedCenter);
        sendBtn = (Button) findViewById(R.id.sendButton);
        deskripsi = (EditText) findViewById(R.id.et_portfolio);
        mFirebaseDatabaseRef = FirebaseDatabase.getInstance();
        mDatabaseRef = mFirebaseDatabaseRef.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference().child(Constants.KEY_USERS+"/"+mEncodedUid+"/portfolio");


        Intent i = this.getIntent();
        String filename = i.getStringExtra("image");
        Bitmap bmp = null;
        try {
//            FileInputStream is = this.openFileInput("bitmap.png");
//            bmp = BitmapFactory.decodeStream(is);

            bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(),Uri.parse(filename));
//            bmp = Utility.getResizedBitmap(bmp,500);
            photo.setImageBitmap(bmp);
//            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        judul.setText("Scholarplus");

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postPortfolio();
            }
        });

        deskripsi.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
        deskripsi.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    sendBtn.setEnabled(true);
                    sendBtn.setBackgroundResource(R.color.colorAccent);
                } else {
                    sendBtn.setEnabled(false);
                    sendBtn.setBackgroundResource(R.color.iron);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

    }
    private void postPortfolio(){
        photo.buildDrawingCache();
        Bitmap bm= photo.getDrawingCache();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        final String id = mDatabaseRef.child(Constants.KEY_USERS+"/"+mEncodedUid+"/portfolio").push().getKey();
        byte[] photo = baos.toByteArray();

        if (photo.length/1024 < 2048){
            mLoadingProgressDialog.show();
            StorageReference profileRef = mStorageRef.child(id);
            UploadTask uploadTask = profileRef.putBytes(photo);
            uploadTask.addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle unsuccessful uploads
                    mDatabaseRef.child(Constants.KEY_USERS+"/"+mEncodedUid+"/portfolio/"+id).removeValue();
                }
            }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                    Uri downloadUrl = taskSnapshot.getDownloadUrl();
                    String url = downloadUrl.getScheme()+":"+downloadUrl.getEncodedSchemeSpecificPart();

                    HashMap<String, Object> portfolio = new HashMap<>();
                    portfolio.put(Constants.FIREBASE_PROPERTY_NAME,deskripsi.getText().toString());
                    portfolio.put(Constants.FIREBASE_PROPERTY_PHOTO,url);
                    mDatabaseRef.child(Constants.KEY_USERS+"/"+mEncodedUid+"/portfolio/"+id).setValue(portfolio).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            NavUtils.navigateUpFromSameTask(AddPdportfolioActivity.this);
                            mLoadingProgressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Success",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            });
        }
        else{
            Toast.makeText(this, "File is too big",
                    Toast.LENGTH_SHORT).show();

        }


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
