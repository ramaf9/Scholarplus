package com.v1.firebase.scholarplus.ui.connect;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.common.ConnectionResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.model.User;
import com.v1.firebase.scholarplus.model.connect.ConnectDetail;
import com.v1.firebase.scholarplus.ui.BaseActivity;
import com.v1.firebase.scholarplus.utils.Constants;

/**
 * Created by MacBook on 6/17/16.
 */
public class ConnectDetailActivity extends BaseActivity {
    private RecyclerView mConRecyclerView;
    private LinearLayoutManager mLinearLayoutManager;
    private FirebaseRecyclerAdapter<ConnectDetail, ConnectViewHolder>
            mFirebaseAdapter;
    private ValueEventListener rec;
    private DatabaseReference mFirebaseDatabaseReference;
    private String uid;
    private Button mButton;
    private EditText mEditTextMessage;
    private ProgressBar mProgressBar;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_connect);
        Intent i = this.getIntent();
        uid = i.getStringExtra(Constants.KEY_ID);

        mLinearLayoutManager = new LinearLayoutManager(this.getApplicationContext());
        mConRecyclerView = (RecyclerView) findViewById(R.id.messageRecyclerView);
        mLinearLayoutManager.setStackFromEnd(true);
        mButton = (Button) findViewById(R.id.sendButton);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mEditTextMessage = (EditText) findViewById(R.id.messageEditText);
        setTitle(i.getStringExtra(Constants.DETAIL_NAME));


        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference().child(Constants.FIREBASE_PROPERTY_CONNECTC+"/"+uid);



                    mFirebaseAdapter = new FirebaseRecyclerAdapter<ConnectDetail, ConnectViewHolder>(
                            ConnectDetail.class,
                            R.layout.item_message,
                            ConnectViewHolder.class,
                            mFirebaseDatabaseReference

                    ) {
                        @Override
                        protected void populateViewHolder(final ConnectViewHolder viewHolder,final ConnectDetail model, int position) {
                            String uid = model.getUid();
                            viewHolder.message.setText(model.getBody());
                            DatabaseReference dbUser = FirebaseDatabase.getInstance().getReference().child(Constants.KEY_USERS+"/"+uid);
                            dbUser.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    mProgressBar.setVisibility(ProgressBar.INVISIBLE);

                                    User user = dataSnapshot.getValue(User.class);

                                    if (user != null){
                                        viewHolder.createdByUser.setText(user.getName());

                                        if (user.getPhoto() != null && !user.getPhoto().isEmpty()){

//                                            new DownLoadImageTask(viewHolder.photoBy).execute(user.getPhoto());
                                            Picasso.with(getApplicationContext())
                                                    .load(user.getPhoto())
                                                    .resize(50, 50)
                                                    .centerCrop()
                                                    .into(viewHolder.photoBy);
                                        }


                                        viewHolder.photoBy.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                //
                                            }
                                        });

                                    }


                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }
                    };

        mFirebaseAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                int friendlyMessageCount = mFirebaseAdapter.getItemCount();
                int lastVisiblePosition =
                        mLinearLayoutManager.findLastCompletelyVisibleItemPosition();
                // If the recycler view is initially being loaded or the
                // user is at the bottom of the list, scroll to the bottom
                // of the list to show the newly added message.
                if (lastVisiblePosition == -1 ||
                        (positionStart >= (friendlyMessageCount - 1) &&
                                lastVisiblePosition == (positionStart - 1))) {
                    mConRecyclerView.scrollToPosition(positionStart);
                }
            }
        });
        mConRecyclerView.setLayoutManager(mLinearLayoutManager);
        mConRecyclerView.setAdapter(mFirebaseAdapter);

        mEditTextMessage.setFilters(new InputFilter[]{new InputFilter.LengthFilter(150)});
        mEditTextMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().trim().length() > 0) {
                    mButton.setEnabled(true);
                } else {
                    mButton.setEnabled(false);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mEditTextMessage.getText().toString().equals("")) {

                                ConnectDetail condet = new ConnectDetail(mEditTextMessage.getText().toString(),mEncodedUid);
                                mFirebaseDatabaseReference.push().setValue(condet);
                                mEditTextMessage.setText("");



                }
                else{
                    Log.d("LOOL","NULIS APA BOSH");
                }
            }
        });

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onStop() {
        super.onStop();
        mFirebaseAdapter.cleanup();
    }

    public static class ConnectViewHolder extends RecyclerView.ViewHolder {
        public TextView createdByUser,message;
        public ImageView photoBy;
        public View mView;

        public ConnectViewHolder(View v) {
            super(v);
            mView = v;
            message = (TextView)itemView.findViewById(R.id.messengerTextView);
            photoBy = (ImageView) itemView.findViewById(R.id.messengerImageView);
            createdByUser = (TextView)itemView.findViewById(R.id.messageTextView);
        }

    }
}
