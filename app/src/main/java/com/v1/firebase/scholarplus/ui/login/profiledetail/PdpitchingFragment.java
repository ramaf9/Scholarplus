package com.v1.firebase.scholarplus.ui.login.profiledetail;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.utils.Constants;
import com.volokh.danylo.video_player_manager.manager.PlayerItemChangeListener;
import com.volokh.danylo.video_player_manager.manager.SingleVideoPlayerManager;
import com.volokh.danylo.video_player_manager.manager.VideoPlayerManager;
import com.volokh.danylo.video_player_manager.meta.MetaData;
import com.volokh.danylo.video_player_manager.ui.SimpleMainThreadMediaPlayerListener;
import com.volokh.danylo.video_player_manager.ui.VideoPlayerView;

/**
 * Created by MacBook on 6/17/16.
 */
public class PdpitchingFragment extends Fragment {
    private VideoPlayerView pitchvideo;
    private ImageView pitchvideocover;
    private String uid;
    private VideoPlayerManager<MetaData> mVideoPlayerManager = new SingleVideoPlayerManager(new PlayerItemChangeListener() {
        @Override
        public void onPlayerItemChanged(MetaData metaData) {

        }
    });
    public static PdpitchingFragment newInstance(String id) {
        PdpitchingFragment fragment = new PdpitchingFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_ID, id);
        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            uid = getArguments().getString(Constants.KEY_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.profile_pitch, container, false);
        pitchvideo = (VideoPlayerView) rootView.findViewById(R.id.pitch_video);
        pitchvideocover = (ImageView) rootView.findViewById(R.id.video_cover_1);
        final LinearLayout pv = (LinearLayout) rootView.findViewById(R.id.video_cover);

        final DatabaseReference pitchVideo = FirebaseDatabase.getInstance().getReference().child("users/"+uid+"/pitch");
        pitchVideo.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {
                    final String data = dataSnapshot.getValue(String.class);
                    if (!data.isEmpty()){

                        Uri video = Uri.parse(data);

//                        MediaController mediaController = new MediaController(getActivity());
//                        pitchvideo.setMediaController(mediaController);
//                        pitchvideo.setVideoURI(video);

                        pitchvideo.addMediaPlayerListener(new SimpleMainThreadMediaPlayerListener(){
                            @Override
                            public void onVideoPreparedMainThread() {
                                // We hide the cover when video is prepared. Playback is about to start
                                Log.d("video","start");
                                pv.setVisibility(View.INVISIBLE);
                            }

                            @Override
                            public void onVideoStoppedMainThread() {
                                // We show the cover when video is stopped
                                Log.d("video","stop");
                                pv.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onVideoCompletionMainThread() {
                                // We show the cover when video is completed
                                Log.d("video","complete");
                                pv.setVisibility(View.VISIBLE);
                            }
                        });

                        pv.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mVideoPlayerManager.playNewVideo(null, pitchvideo, data);
                            }
                        });


                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return rootView;
    }
}
