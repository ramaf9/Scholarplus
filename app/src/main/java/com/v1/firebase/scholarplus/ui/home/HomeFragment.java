package com.v1.firebase.scholarplus.ui.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.model.News;
import com.v1.firebase.scholarplus.model.Scholarship;
import com.v1.firebase.scholarplus.model.User;
import com.v1.firebase.scholarplus.ui.scholarlist.ScholarListDetailActivity;
import com.v1.firebase.scholarplus.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

/**
 * Created by sekartanjung on 6/8/16.
 */
public class HomeFragment extends Fragment implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<News, HomeViewHolder>
            mFirebaseAdapter;
    private FirebaseRecyclerAdapter<Scholarship, RecViewHolder>
            mFirebaseAdapter2;
    private RecyclerView mNewsRecyclerView,mRecRecyclerView;
    private LinearLayoutManager mLinearLayoutManager,mLinearLayoutManager2;
    private ProgressBar mProgressBar;
    private TextView welcometv;
    private ValueEventListener rec;
    private String uid;
    private String userChoosenTask;
    private int count = 0;
    private CoordinatorLayout cl_recommended;
    private SliderLayout mDemoSlider;
    private HashMap<String,String> url_maps;
    public HomeFragment() {
        /* Required empty public constructor */
    }

    /**
     * Create fragment and pass bundle with data as it's arguments
     * Right now there are not arguments...but eventually there will be.
     */
    public static HomeFragment newInstance(String uid) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_ENCODED_EMAIL, uid);
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
        View rootView = inflater.inflate(R.layout.fragment_home, container, false);
        cl_recommended = (CoordinatorLayout) rootView.findViewById(R.id.card_view_recommended);
        welcometv = (TextView) rootView.findViewById(R.id.tv_welcome);
        mNewsRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_active_lists);
        mRecRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_recommended);
        mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mDemoSlider = (SliderLayout)rootView.findViewById(R.id.slider);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);

        mLinearLayoutManager2 = new LinearLayoutManager(getActivity().getApplicationContext());
        mLinearLayoutManager2.setReverseLayout(true);
        mLinearLayoutManager2.setStackFromEnd(true);

        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        url_maps = new HashMap<String, String>();
        DatabaseReference ref = mFirebaseDatabaseReference;
        ref.child("news").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null){
                    News data = dataSnapshot.getValue(News.class);
                    url_maps.put(data.getNama(),data.getPhotopath());

                   String key =  url_maps.keySet().toArray()[0].toString();
//                    for(String name : url_maps.keySet()){
                        TextSliderView textSliderView = new TextSliderView(getContext());
                        // initialize a SliderLayout
                        textSliderView
                                .description(key)
                                .image(url_maps.get(key))
                                .setScaleType(BaseSliderView.ScaleType.Fit);

                        //add your extra information
                        textSliderView.bundle(new Bundle());
                        textSliderView.getBundle()
                                .putString("extra",key);

                    
                    mDemoSlider.addSlider(textSliderView);
//                    }
                }
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

        HashMap<String,Integer> file_maps = new HashMap<String, Integer>();
        file_maps.put("Hannibal",R.drawable.common_full_open_on_phone);
        file_maps.put("Big Bang Theory",R.drawable.common_full_open_on_phone);
        file_maps.put("House of Cards",R.drawable.common_full_open_on_phone);
        file_maps.put("Game of Thrones", R.drawable.common_full_open_on_phone);


        mDemoSlider.setPresetTransformer(SliderLayout.Transformer.Accordion);
        mDemoSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        mDemoSlider.setCustomAnimation(new DescriptionAnimation());
        mDemoSlider.setDuration(4000);
        mDemoSlider.addOnPageChangeListener(this);


        DatabaseReference recDatabase = mFirebaseDatabaseReference;
        rec = recDatabase.child(Constants.KEY_USERS+"/"+uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                final User user = dataSnapshot.getValue(User.class);

                if (user != null) {
                    welcometv.setText(user.getName());
                    mFirebaseAdapter2 = new FirebaseRecyclerAdapter<Scholarship,
                            RecViewHolder>(
                            Scholarship.class,
                            R.layout.single_active_list,
                            RecViewHolder.class,
                            mFirebaseDatabaseReference.child("beasiswa")
                                    .orderByChild(Constants.FIREBASE_PROPERTY_KUANTITATIF + "/" + Constants.FIREBASE_PROPERTY_IPK)
                                    .endAt(user.getIpk())
                                    .limitToLast(3)) {

                        @Override
                        protected void populateViewHolder(final RecViewHolder viewHolder,
                                                          final Scholarship scholarship, int position) {
                                String v = scholarship.getKuantitatif().get(Constants.FIREBASE_PROPERTY_SEMESTER).toString();

                                if (!user.getSemester().isEmpty() && Double.parseDouble(user.getSemester()) >= Double.parseDouble(v)) {
                                    count++;
                                    cl_recommended.setVisibility(View.VISIBLE);
                                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                                    viewHolder.createdByUser.setText(scholarship.getInstansi());
                                    if (scholarship.getPhotopath() != null)
//                                        new DownLoadImageTask(viewHolder.photoBy).execute(scholarship.getPhotopath());
                                    Picasso.with(getActivity().getApplicationContext())
                                            .load(scholarship.getPhotopath())
//                                            .resize(50, 50)
//                                            .centerCrop()
                                            .into(viewHolder.photoBy);

                                    final String itemId = this.getRef(position).getKey();
                                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent i = new Intent(getActivity(),ScholarListDetailActivity.class);
                                        viewHolder.photoBy.buildDrawingCache();
                                        Bitmap bm= viewHolder.photoBy.getDrawingCache();
                                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                        bm.compress(Bitmap.CompressFormat.PNG, 10, baos);
                                        byte[] b = baos.toByteArray();

                                        i.putExtra(Constants.FIREBASE_PROPERTY_BEASISWA, scholarship);
                                        i.putExtra(Constants.DETAIL_PHOTO,b);
                                        i.putExtra(Constants.KEY_ID,itemId);
                                        startActivity(i);

                                    }
                                });
                                }
                            else if(count == 0){
                                    cl_recommended.setVisibility(View.GONE);
                            }
                            else{
                                    cl_recommended.setVisibility(View.VISIBLE);
                                    viewHolder.mView.setVisibility(View.GONE);
                                }


                        }

                    };

                    mRecRecyclerView.setLayoutManager(mLinearLayoutManager2);
                    mRecRecyclerView.setAdapter(mFirebaseAdapter2);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mFirebaseAdapter = new FirebaseRecyclerAdapter<News,
                HomeViewHolder>(
                News.class,
                R.layout.single_active_list_cardview,
                HomeViewHolder.class,
                mFirebaseDatabaseReference.child("news")) {

            @Override
            protected void populateViewHolder(final HomeViewHolder viewHolder,
                                              News news, int position) {
                mProgressBar.setVisibility(ProgressBar.INVISIBLE);
                viewHolder.createdByUser.setText(news.getSource());
                if (news.getPhotopath() != null)
//                    new DownLoadImageTask(viewHolder.photoBy).execute(news.getPhotopath());
                    Picasso.with(getActivity().getApplicationContext())
                            .load(news.getPhotopath())
//                                            .resize(50, 50)
//                                            .centerCrop()
                            .into(viewHolder.photoBy);

                String s = Html.fromHtml(news.getNama()).toString();
                final String nama = s;

                final String deskripsi = news.getDeskripsi();
                    viewHolder.deskripsi.setText(Html.fromHtml(s+"<br><center> <font color='#d36e4c'> <b>See more</b></font></center>"));
                    viewHolder.deskripsi.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getActivity(),NewsDetailActivity.class);
                            viewHolder.photoBy.buildDrawingCache();
                            Bitmap bm= viewHolder.photoBy.getDrawingCache();
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] b = baos.toByteArray();

                            i.putExtra(Constants.DETAIL_PHOTO,b);
                            i.putExtra(Constants.DETAIL_BY,viewHolder.createdByUser.getText());
                            i.putExtra(Constants.DETAIL_NAME,nama);
                            i.putExtra(Constants.DETAIL_DESC,deskripsi);
                            startActivity(i);

                        }
                    });




            }
        };

//        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
//            @Override
//            public void onItemRangeInserted(int positionStart, int itemCount) {
//                super.onItemRangeInserted(positionStart, itemCount);
//                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
//                int lastVisiblePosition =
//                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
//                // If the recycler view is initially being loaded or the
//                // user is at the bottom of the list, scroll to the bottom
//                // of the list to show the newly added message.
//                if (lastVisiblePosition == -1 ||
//                        (positionStart >= (friendlyMessageCount - 1) &&
//                                lastVisiblePosition == (positionStart - 1))) {
//                    mNewsRecyclerView.scrollToPosition(positionStart);
//                }
//            }
//        });
        mNewsRecyclerView.setLayoutManager(mLinearLayoutManager);
        mNewsRecyclerView.setAdapter(mFirebaseAdapter);

        Button seeMore = (Button) rootView.findViewById(R.id.btn_seemore);
        seeMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewPager host = (ViewPager) getActivity().findViewById(R.id.pager);
                host.setCurrentItem(1);
            }
        });

        return rootView;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("Status","pause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("status","resume");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d("status","start");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("Status","stop");

    }
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {}

    @Override
    public void onPageSelected(int position) {
        Log.d("Slider Demo", "Page Changed: " + position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {}

    @Override
    public void onSliderClick(BaseSliderView slider) {
        Toast.makeText(getContext(),slider.getBundle().get("extra") + "",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mFirebaseDatabaseReference.removeEventListener(rec);
        if (mFirebaseAdapter != null)
            mFirebaseAdapter.cleanup();
        if (mFirebaseAdapter2 != null)
            mFirebaseAdapter2.cleanup();
        Log.d("Status","destroy view");
    }




    public static class HomeViewHolder extends RecyclerView.ViewHolder {
        public TextView createdByUser,deskripsi;
        public ImageView photoBy;
        public View mView;

        public HomeViewHolder(View v) {
            super(v);
            mView = v;
            deskripsi = (TextView)itemView.findViewById(R.id.text_view_list_name);
            photoBy = (ImageView) itemView.findViewById(R.id.ivFeedCenter);
            createdByUser = (TextView)itemView.findViewById(R.id.text_view_created_by_user);
        }

    }


    public static class RecViewHolder extends RecyclerView.ViewHolder {
        public TextView createdByUser;
        public ImageView photoBy;
        public View mView;

        public RecViewHolder(View v) {
            super(v);
            mView = v;
            photoBy = (ImageView) itemView.findViewById(R.id.created_by);
            createdByUser = (TextView)itemView.findViewById(R.id.text_view_created_by_user);
        }

    }
}
