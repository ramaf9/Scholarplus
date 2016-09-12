package com.v1.firebase.scholarplus.ui.connect;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.ui.BaseActivity;
import com.v1.firebase.scholarplus.ui.login.userprofiledetail.UProfileFragment;
import com.v1.firebase.scholarplus.utils.Constants;

/**
 * Created by MacBook on 6/26/16.
 */
public class UserDetailActivity extends BaseActivity {
    private TabLayout tabLayout;
    private int[] imageResId = {
            R.drawable.ic_home_tab,
            R.drawable.ic_scholar_tab,
            R.drawable.ic_connect_tab,
            R.drawable.ic_action_name
    };
    private String userProfileId;


    ViewPager viewPager;
    SectionPagerAdapter adapter = new SectionPagerAdapter(getSupportFragmentManager());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent i = this.getIntent();
        userProfileId = i.getStringExtra(Constants.KEY_ID);

        setTitle(i.getStringExtra(Constants.FIREBASE_PROPERTY_NAME));
        initializeScreen();

    }

    private void initializeScreen(){
        viewPager = (ViewPager) findViewById(R.id.pager);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);

        setSupportActionBar(toolbar);
        getSupportActionBar().setElevation(0);


        adapter = new SectionPagerAdapter(getSupportFragmentManager());
        viewPager.setOffscreenPageLimit(4);
        viewPager.setAdapter(adapter);
        /**
         * Setup the mTabLayout with view pager
         */
        tabLayout.setupWithViewPager(viewPager);
        adapter.setupTabIcons();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                    fragment = UProfileFragment.newInstance(userProfileId);
                    break;
                default:
                    fragment = UProfileFragment.newInstance(userProfileId);
                    break;
            }

            return fragment;
        }


        @Override
        public int getCount() {
            return 1;
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
