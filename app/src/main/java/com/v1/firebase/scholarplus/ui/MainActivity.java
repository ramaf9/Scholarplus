package com.v1.firebase.scholarplus.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialcamera.MaterialCamera;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.model.User;
import com.v1.firebase.scholarplus.service.NotificationListener;
import com.v1.firebase.scholarplus.ui.home.HomeFragment;
import com.v1.firebase.scholarplus.ui.login.EditProfileActivity;
import com.v1.firebase.scholarplus.ui.login.profiledetail.AddPdportfolioActivity;
import com.v1.firebase.scholarplus.ui.scholarlist.ScholarListFragment;
import com.v1.firebase.scholarplus.utils.Constants;
import com.v1.firebase.scholarplus.utils.Utility;

import java.io.File;
import java.io.FileOutputStream;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private TabLayout tabLayout;
    private int[] imageResId = {
            R.drawable.ic_home_tab,
            R.drawable.ic_scholar_tab,
            R.drawable.ic_connect_tab,
            R.drawable.ic_action_name
    };
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1, REQUEST_VIDEO = 2;
    private final static int CAMERA_RQ = 6969;
    private String userChoosenTask;
    private FirebaseStorage mFirebaseStorageRef;
    private Uri mImageUri;


    ViewPager viewPager;
    SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mFirebaseStorageRef = FirebaseStorage.getInstance();
        initializeScreen();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(Gravity.LEFT)) {
            drawer.closeDrawer(Gravity.LEFT);
        }

        else {
            super.onBackPressed();
        }
    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    private void initializeScreen(){
        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);
        if (!isMyServiceRunning(NotificationListener.class)){
            startService(new Intent(this, NotificationListener.class));
        }


//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        final DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);


        toggle.syncState();
//        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                if (drawer.isDrawerOpen(Gravity.RIGHT)) {
//                    drawer.closeDrawer(Gravity.RIGHT);
//                } else {
//                    drawer.openDrawer(Gravity.RIGHT);
//                }
//            }
//        });

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        adapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
        /**
         * Setup the mTabLayout with view pager
         */
        tabLayout.setupWithViewPager(viewPager);
        adapter.setupTabIcons();

        View hView =  navigationView.getHeaderView(0);

        final TextView dUnama = (TextView)hView.findViewById(R.id.namah);
        final TextView dUemail = (TextView)hView.findViewById(R.id.textView);
        final ImageView dUphoto = (ImageView)hView.findViewById(R.id.imageView);


        DatabaseReference mDatabaseRef = FirebaseDatabase.getInstance().getReference(Constants.KEY_USERS+"/"+mEncodedUid);
        mDatabaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user != null) {
                    dUnama.setText(user.getName());

                    dUemail.setText(user.getEmail());

                    if(user.getPhoto() != null && !user.getPhoto().isEmpty())
//                        new DownLoadImageTask(dUphoto).execute(user.getPhoto());
                        Picasso.with(getApplicationContext())
                                .load(user.getPhoto())
//                                            .resize(50, 50)
//                                            .centerCrop()
                                .into(dUphoto);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void onActionAPressed(View view) {
        secondDialog();
    }

    public void onActionBPressed(View view) {
//        Snackbar.make(view, "Fitur coming soon", Snackbar.LENGTH_SHORT).show();
        firstDialog();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        if (item != null && item.getItemId() == android.R.id.home) {
//            if (drawer.isDrawerOpen(Gravity.LEFT)) {
//                drawer.closeDrawer(Gravity.LEFT);
//            } else {
//                drawer.openDrawer(Gravity.LEFT);
//            }
//        }
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            FirebaseAuth a = FirebaseAuth.getInstance();
//            a.signOut();
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_share) {
            Intent intent = new Intent(this, EditProfileActivity.class);
            intent.putExtra("new",false);
            startActivity(intent);
        } else if (id == R.id.nav_logout) {
            logoutDialog();
        }
//        else if (id == R.id.nav_camera) {
//            secondDialog();
//        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void firstDialog(){
//        Intent videoIntent = new Intent(android.provider.MediaStore.ACTION_VIDEO_CAPTURE);
////        mMediaUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);
////        if (mMediaUri == null) {
////            // display an error
////            Toast.makeText(MainActivity.this, "storage error",
////                    Toast.LENGTH_LONG).show();
////        }
////        else {
////            videoIntent.putExtra(MediaStore.EXTRA_OUTPUT, mMediaUri);
//            videoIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 20);
//            videoIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0); // 0 = lowest res
//            startActivityForResult(videoIntent, REQUEST_VIDEO);
//        File saveFolder = new File(Environment.getExternalStorageDirectory(), "MaterialCamera Sample");
//        if (!saveFolder.mkdirs())
//            throw new RuntimeException("Unable to create save directory, make sure WRITE_EXTERNAL_STORAGE permission is granted.");
        File destination = null;
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            // Only use external storage directory if permission is granted, otherwise cache directory is used by default
            destination = new File(Environment.getExternalStorageDirectory(),
                    "VID_"+System.currentTimeMillis() + ".mp4");
        }

        new MaterialCamera(this)
                .countdownSeconds(20)
//                .allowRetry(true)                                  // Whether or not 'Retry' is visible during playback
//                .autoSubmit(false)                                 // Whether or not user is allowed to playback videos after recording. This can affect other things, discussed in the next section.
                .saveDir(destination)                               // The folder recorded videos are saved to
                .primaryColorAttr(R.attr.colorAccent)             // The theme color used for the camera, defaults to colorPrimary of Activity in the constructor
                .showPortraitWarning(false)                         // Whether or not a warning is displayed if the user presses record in portrait orientation
//                .defaultToFrontFacing(false)                       // Whether or not the camera will initially show the front facing camera
//                .retryExits(false)                                 // If true, the 'Retry' button in the playback screen will exit the camera instead of going back to the recorder
                .restartTimerOnRetry(true)                        // If true, the countdown timer is reset to 0 when the user taps 'Retry' in playback
                .continueTimerInPlayback(false)                    // If true, the countdown timer will continue to go down during playback, rather than pausing.
                .videoEncodingBitRate(1024000)                     // Sets a custom bit rate for video recording.
                .audioEncodingBitRate(50000)                       // Sets a custom bit rate for audio recording.
                .videoFrameRate(24)                                // Sets a custom frame rate (FPS) for video recording.
                .qualityProfile(MaterialCamera.QUALITY_HIGH)       // Sets a quality profile, manually setting bit rates or frame rates with other settings will overwrite individual quality profile settings
                .videoPreferredHeight(1280)                         // Sets a preferred height for the recorded video output.
                .videoPreferredAspect(16f / 9f)                     // Sets a preferred aspect ratio for the recorded video output.
                .maxAllowedFileSize(1024 * 1024 * 3)               // Sets a max file size of 5MB, recording will stop if file reaches this limit. Keep in mind, the FAT file system has a file size limit of 4GB.
                .iconRecord(R.drawable.mcam_action_play)           // Sets a custom icon for the button used to start recording
                .iconStop(R.drawable.mcam_action_stop)             // Sets a custom icon for the button used to stop recording
                .iconFrontCamera(R.drawable.mcam_camera_front)     // Sets a custom icon for the button used to switch to the front camera
                .iconRearCamera(R.drawable.mcam_camera_rear)       // Sets a custom icon for the button used to switch to the rear camera
                .iconRestart(R.drawable.evp_action_restart)        // Sets a custom icon used to restart playback
                .labelRetry(R.string.mcam_retry)                   // Sets a custom button label for the button used to retry recording, when available
                .labelUseVideo(R.string.mcam_use_video)            // Sets a custom button label for the button used to confirm a recording
                .start(CAMERA_RQ);
    }

    private void secondDialog () {
//        new AlertDialog.Builder(MainActivity.this)
//                .setTitle(" Coming soon")
//                .setMessage("Scholarplus Alpha v 0.7")
//                .setPositiveButton("Okay", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//
//                    }
//                })
//
//                .setIcon(R.drawable.ic_calendars)
//                .show();
        final CharSequence[] items = { "Take Photo", "Choose from Library",
                "Cancel" };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Photo!");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
//                boolean result= Utility.checkPermission(getApplicationContext());

                if (items[item].equals("Take Photo")) {
                    userChoosenTask="Take Photo";
//                    if(result)
                        cameraIntent();

                } else if (items[item].equals("Choose from Library")) {
                    userChoosenTask="Choose from Library";
//                    if(result)
                        galleryIntent();

                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select File"),SELECT_FILE);
    }
    private void cameraIntent()
    {
        Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");

        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        mImageUri = Uri.fromFile(destination);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
        startActivityForResult(intent, REQUEST_CAMERA);
    }
//    @Override
//    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
//        switch (requestCode) {
//            case Utility.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
//                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                    if(userChoosenTask.equals("Take Photo"))
//                        cameraIntent();
//                    else if(userChoosenTask.equals("Choose from Library"))
//                        galleryIntent();
//                } else {
//                    //code for deny
//                }
//                break;
//        }
//    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_FILE)
                onSelectFromGalleryResult(data);
            else if (requestCode == REQUEST_CAMERA)
                onCaptureImageResult(data);
            else if (requestCode == CAMERA_RQ)
                onCaptureVideoResult(data);
        }
    }

    private void onCaptureImageResult(Intent data) {
//        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
//        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
//        thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);
//
//        File destination = new File(Environment.getExternalStorageDirectory(),
//                System.currentTimeMillis() + ".jpg");
//
//        FileOutputStream fo;
//        try {
//            destination.createNewFile();
//            fo = new FileOutputStream(destination);
//            fo.write(bytes.toByteArray());
//            fo.close();
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        intentAddPortfolio(thumbnail);
        Intent in1 = new Intent(this, AddPdportfolioActivity.class);
        in1.putExtra("image", mImageUri.toString());
        startActivity(in1);
    }
    private void onCaptureVideoResult(Intent data) {
        Uri uri = data.getData();


        // Get the path
        try {

            final String path = Utility.getPath(getApplicationContext(),uri);


            Uri file = Uri.fromFile(new File(path));
            File filesize = new File(path);
            if (filesize.length()/1024 > 5120){
                Toast.makeText(this, "File is too big",
                        Toast.LENGTH_SHORT).show();
            }
            else{
                mLoadingProgressDialog.show();
                StorageReference riversRef = mFirebaseStorageRef.getReference().child(Constants.KEY_USERS+"/"+mEncodedUid+"/pitch");
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
                        // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
                        Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        mLoadingProgressDialog.dismiss();
                        String cvUri = downloadUrl.getScheme()+":"+downloadUrl.getEncodedSchemeSpecificPart();
                        final DatabaseReference databaseUser = FirebaseDatabase.getInstance().getReference(Constants.KEY_USERS+"/"+mEncodedUid+"/pitch");

                        databaseUser.setValue(cvUri).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                //
                                Toast.makeText(getApplicationContext(), "Success",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
            }

        }
        catch (Exception e){
            Log.d("Pail",e.getMessage());
        }


    }

    private void onSelectFromGalleryResult(Intent data) {
            mImageUri = data.getData();
        Intent in1 = new Intent(this, AddPdportfolioActivity.class);
        in1.putExtra("image", mImageUri.toString());
        startActivity(in1);
//        Bitmap bm=null;
//        if (data != null) {
//            try {
//                bm = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
//
//                intentAddPortfolio(bm);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }

    }
    private void intentAddPortfolio(Bitmap bm){
//        String fileName = "portfolio_"+mEncodedUid;
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//
//        bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
//        byte[] photo = baos.toByteArray();
//        if (photo.length/1024 < 2048){
//
//            try {
//
//                FileOutputStream fo = openFileOutput(fileName, Context.MODE_PRIVATE);
//                fo.write(baos.toByteArray());
//                // remember close file output
//                fo.close();
//                Intent i = new Intent(this,AddPdportfolioActivity.class);
//
//                startActivity(i);
//            } catch (Exception e) {
//                e.printStackTrace();
//                fileName = null;
//            }
//
//        }else{
//            Toast.makeText(this, "File is too big",
//                    Toast.LENGTH_SHORT).show();
//        }

        try {
            //Write file
            String filename = "bitmap.png";

            FileOutputStream stream = this.openFileOutput(filename, Context.MODE_PRIVATE);
            bm.compress(Bitmap.CompressFormat.PNG, 100, stream);

            //Cleanup
            stream.close();
            bm.recycle();

            //Pop intent
            Intent in1 = new Intent(this, AddPdportfolioActivity.class);
            in1.putExtra("image", filename);
            startActivity(in1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void logoutDialog () {
        new AlertDialog.Builder(MainActivity.this)
                .setTitle("Logout")
                .setMessage("Anda yakin ?")
                .setPositiveButton("iya", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        logout();
                    }
                })
                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                })


                .show();
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    public class SectionPagerAdapter extends FragmentStatePagerAdapter {


        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        /**
         * Use positions (0 and 1) to find and instantiate fragments with newInstance()
         *
         * @param position
         */
        @Override
        public Fragment getItem(int position) {

            Fragment fragment = null;

            /**
             * Set fragment to different fragments depending on position in ViewPager
             */
            switch (position) {
                case 0:
                    fragment = HomeFragment.newInstance(mEncodedUid);
                    break;
                case 1:
                    fragment = ScholarListFragment.newInstance();
                    break;
//                case 2:
//                    fragment = ConnectFragment.newInstance();
//                    break;
//                case 3:
//                    fragment = ProfileFragment.newInstance(mEncodedUid);
//                    break;
                default:
                    fragment = HomeFragment.newInstance(mEncodedUid);
                    break;
            }

            return fragment;
        }


        @Override
        public int getCount() {
            return 2;
        }

        /**
         * Set string resources as titles for each fragment by it's position
         *
         * @param position
         */
        @Override
        public CharSequence getPageTitle(int position) {
            return "";
        }
        private void setupTabIcons() {
            //inankeker
            for (int i = 0; i < tabLayout.getTabCount(); i++)
            {
                tabLayout.getTabAt(i).setIcon(imageResId[i]);
            }

//            for (int i = 0; i < tabLayout.getTabCount(); i++)
//            {
//                FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(60, 60); //set new width & Height
//                params.gravity = Gravity.CENTER; //set gravity back to center
//                tabLayout.getChildAt(i).setLayoutParams(params);//set ur new params
//
//            }

            tabLayout.getTabAt(0).getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            tabLayout.getTabAt(1).getIcon().setColorFilter(getResources().getColor(R.color.unselected), PorterDuff.Mode.SRC_IN);
//            tabLayout.getTabAt(2).getIcon().setColorFilter(getResources().getColor(R.color.unselected), PorterDuff.Mode.SRC_IN);
//            tabLayout.getTabAt(3).getIcon().setColorFilter(getResources().getColor(R.color.unselected), PorterDuff.Mode.SRC_IN);


            tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    tab.getIcon().setColorFilter(getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                    viewPager.setCurrentItem(tab.getPosition());
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {
                    tab.getIcon().setColorFilter(getResources().getColor(R.color.unselected), PorterDuff.Mode.SRC_IN);
                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
            //inankeker
        }
    }
}
