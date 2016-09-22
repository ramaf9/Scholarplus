package com.v1.firebase.scholarplus.ui.login.userprofiledetail;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.helper.CustomViewPager;
import com.v1.firebase.scholarplus.model.User;
import com.v1.firebase.scholarplus.ui.login.profiledetail.PdpitchingFragment;
import com.v1.firebase.scholarplus.utils.Constants;

import java.text.ParseException;

/**
 * Created by MacBook on 6/28/16.
 */
public class UProfileFragment extends Fragment {
    private String uid;
    private TextView nama,jurusan,institut,age,ipk;
    private ImageView foto;
    private FirebaseDatabase mFirebaseDatabaseRef;
    private ValueEventListener mEventListener;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private Button EditProfile;
    private int REQUEST_CAMERA = 0, SELECT_FILE = 1;
    private String userChoosenTask;
    private TabLayout tabLayout;
    private String videoUri;
    private int[] imageResId = {
            R.drawable.ic_grid_on_white,
            R.drawable.ic_videocam_white_24dp,
//            R.drawable.ic_label_white,
//            R.drawable.ic_place_white
    };
    public UProfileFragment() {
        /* Required empty public constructor */
    }

    /**
     * Create fragment and pass bundle with data as it's arguments
     * Right now there are not arguments...but eventually there will be.
     */
    public static UProfileFragment newInstance(String uid) {
        UProfileFragment fragment = new UProfileFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_ENCODED_EMAIL,uid);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(Constants.KEY_ENCODED_EMAIL);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        CustomViewPager viewPager = (CustomViewPager) rootView.findViewById(R.id.subpager);
        tabLayout = (TabLayout) rootView.findViewById(R.id.tlUserProfileTabs);
        nama = (TextView)rootView.findViewById(R.id.tvUsername);
        jurusan = (TextView)rootView.findViewById(R.id.tvJurusan);
        institut = (TextView) rootView.findViewById(R.id.tvInsitut);
        foto = (ImageView) rootView.findViewById(R.id.ivUserProfilePhoto);

        age = (TextView) rootView.findViewById(R.id.tv_age);
        ipk = (TextView) rootView.findViewById(R.id.tv_ipk);

//        EditProfile = (Button)rootView.findViewById(R.id.btnEditProfile);
        mFirebaseDatabaseRef = FirebaseDatabase.getInstance();
        mDatabaseRef = mFirebaseDatabaseRef.getReference(Constants.KEY_USERS+"/"+uid);
        mStorageRef = FirebaseStorage.getInstance().getReference().child(Constants.KEY_USERS+"/"+uid);

        mEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if(user != null){
                    nama.setText(user.getName());
                    jurusan.setText(user.getJurusan());
                    institut.setText(user.getInstitut());
                    ipk.setText("IPK : "+user.getIpk());

                    if (user.getPhoto() != null && !user.getPhoto().isEmpty())
//                        new DownLoadImageTask(foto).execute(user.getPhoto());
                        Picasso.with(getActivity().getApplicationContext())
                                .load(user.getPhoto())
//                                .resize(50, 50)
//                                .centerCrop()
                                .into(foto);
                    try {
                        age.setText(Constants.getAge(user.getDob())+" Years Old");
                    }
                    catch (ParseException e){

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabaseRef.addValueEventListener(mEventListener);


//        EditProfile.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
//                intent.putExtra("new",false);
//                startActivity(intent);
//
//            }
//        });

        SectionPagerAdapter adapter = new SectionPagerAdapter(getActivity().getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(4);

        viewPager.setAdapter(adapter);
        viewPager.setPagingEnabled(false);

        /**
         * Setup the mTabLayout with view pager
         */
        tabLayout.setupWithViewPager(viewPager);

        adapter.setupTabIcons();

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onStop() {
        super.onStop();
        mDatabaseRef.removeEventListener(mEventListener);
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
                    fragment = UPdportfolioFragment.newInstance(uid);
                    break;
                case 1:
                    fragment = PdpitchingFragment.newInstance(uid);
                    break;
//                case 2:
//                    fragment = PdpitchingFragment.newInstance();
//                    break;
//                case 3:
//                    fragment = PdpitchingFragment.newInstance();
//                    break;
                default:
                    fragment = UPdportfolioFragment.newInstance(uid);
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
            tabLayout.getTabAt(0).setIcon(imageResId[0]);
            tabLayout.getTabAt(1).setIcon(imageResId[1]);
//            tabLayout.getTabAt(2).setIcon(imageResId[2]);
//            tabLayout.getTabAt(3).setIcon(imageResId[3]);
        }
    }

}

