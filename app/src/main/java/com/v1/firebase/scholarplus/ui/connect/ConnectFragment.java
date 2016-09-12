package com.v1.firebase.scholarplus.ui.connect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.model.User;
import com.v1.firebase.scholarplus.model.connect.Connect;
import com.v1.firebase.scholarplus.utils.Constants;

/**
 * Created by sekartanjung on 6/8/16.
 */
public class ConnectFragment extends Fragment {
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseRecyclerAdapter<Connect, ConnectViewHolder>
            mFirebaseAdapter;
    private FirebaseRecyclerAdapter<User, UsersViewHolder>
            mFirebaseAdapter2;
    private RecyclerView mConRecyclerView,mUsrRecyclerView;
    private LinearLayoutManager mLinearLayoutManager,mLinearLayoutManager2;
    private ProgressBar mProgressBar;
    private ValueEventListener rec;

    public ConnectFragment() {
        /* Required empty public constructor */
    }

    /**
     * Create fragment and pass bundle with data as it's arguments
     * Right now there are not arguments...but eventually there will be.
     */
    public static ConnectFragment newInstance() {
        ConnectFragment fragment = new ConnectFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connect, container, false);
        mConRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_active_lists);
        mUsrRecyclerView = (RecyclerView) rootView.findViewById(R.id.list_view_users_lists);
        mLinearLayoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        mLinearLayoutManager2 = new LinearLayoutManager(getActivity().getApplicationContext());
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.progressBar);
        mLinearLayoutManager.setReverseLayout(true);
        mLinearLayoutManager.setStackFromEnd(true);


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();

        mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                    mFirebaseAdapter = new FirebaseRecyclerAdapter<Connect, ConnectViewHolder>(
                            Connect.class,
                            R.layout.single_active_list,
                            ConnectViewHolder.class,
                            mFirebaseDatabaseReference.child(Constants.FIREBASE_PROPERTY_CONNECT)

                    ) {
                        @Override
                        protected void populateViewHolder(final ConnectViewHolder viewHolder, Connect model, int position) {

                            viewHolder.createdByUser.setText("#"+model.getName());
                            final String idConnect = this.getRef(position).getKey();
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent i = new Intent(getActivity(),ConnectDetailActivity.class);
                                    i.putExtra(Constants.KEY_ID,idConnect);
                                    i.putExtra(Constants.DETAIL_NAME,viewHolder.createdByUser.getText().toString());
                                    startActivity(i);
                                }
                            });
                        }
                    };

                    mConRecyclerView.setLayoutManager(mLinearLayoutManager);
                    mConRecyclerView.setAdapter(mFirebaseAdapter);

        mFirebaseAdapter2 = new FirebaseRecyclerAdapter<User, UsersViewHolder>(
                User.class,
                R.layout.single_active_list,
                UsersViewHolder.class,
                mFirebaseDatabaseReference.child(Constants.KEY_USERS)

        ) {
            @Override
            protected void populateViewHolder(final UsersViewHolder viewHolder,final User model, int position) {
                final String idConnect = this.getRef(position).getKey();
                FirebaseUser curUser = FirebaseAuth.getInstance().getCurrentUser();
                if (!idConnect.equalsIgnoreCase(curUser.getUid().toString())){
                    if (model.getPhoto() != null && !model.getPhoto().isEmpty())
//                        new DownLoadImageTask(viewHolder.photoBy).execute(model.getPhoto());
                        Picasso.with(getActivity().getApplicationContext())
                                .load(model.getPhoto())
//                                .resize(100, 100)
//                                .centerCrop()
                                .into(viewHolder.photoBy);
                    viewHolder.createdByUser.setText(model.getName());

                    viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent i = new Intent(getActivity(),UserDetailActivity.class);
                            i.putExtra(Constants.KEY_ID,idConnect);
                            i.putExtra(Constants.FIREBASE_PROPERTY_NAME,model.getName());
                            startActivity(i);
                        }
                    });
                }
                else{
                    viewHolder.mView.setVisibility(View.GONE);
                }

            }
        };

        mUsrRecyclerView.setLayoutManager(mLinearLayoutManager2);
        mUsrRecyclerView.setAdapter(mFirebaseAdapter2);



        return rootView;

    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseAdapter.cleanup();
        mFirebaseAdapter2.cleanup();
    }

    public static class ConnectViewHolder extends RecyclerView.ViewHolder {
        public TextView createdByUser;
        public ImageView photoBy;
        public View mView;

        public ConnectViewHolder(View v) {
            super(v);
            mView = v;
            photoBy = (ImageView) itemView.findViewById(R.id.created_by);
            createdByUser = (TextView)itemView.findViewById(R.id.text_view_created_by_user);
        }

    }
    public static class UsersViewHolder extends RecyclerView.ViewHolder {
        public TextView createdByUser;
        public ImageView photoBy;
        public View mView;

        public UsersViewHolder(View v) {
            super(v);
            mView = v;
            photoBy = (ImageView) itemView.findViewById(R.id.created_by);
            createdByUser = (TextView)itemView.findViewById(R.id.text_view_created_by_user);
        }

    }

}