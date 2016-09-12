package com.v1.firebase.scholarplus.ui.login.profiledetail;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.model.profile.Portfolio;
import com.v1.firebase.scholarplus.utils.Constants;

import java.io.ByteArrayOutputStream;

/**
 * Created by MacBook on 6/17/16.
 */
public class PdportfolioFragment extends Fragment {
    private RecyclerView mPortRecyclerView;
    private GridLayoutManager mGridLayoutManager;
    private FirebaseRecyclerAdapter<Portfolio,PortfolioViewHolder>
            mFirebaseAdapter;
    private LinearLayout mLinearLayout;
    private ProgressBar mProgressBar;
    private DatabaseReference mDatabaseRef;
    private StorageReference mStorageRef;
    private String uid;

    public static PdportfolioFragment newInstance(String uid) {
        PdportfolioFragment fragment = new PdportfolioFragment();
        Bundle args = new Bundle();
        args.putString(Constants.KEY_ID, uid);
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

        View rootView = inflater.inflate(R.layout.profile_porto, container, false);
        mPortRecyclerView = (RecyclerView) rootView.findViewById(R.id.rvUserPort);

        mGridLayoutManager = new GridLayoutManager(getContext(), 3, GridLayoutManager.HORIZONTAL, false);
        mDatabaseRef = FirebaseDatabase.getInstance().getReference();
        mLinearLayout = (LinearLayout) rootView.findViewById(R.id.progressBar);
        mFirebaseAdapter = new FirebaseRecyclerAdapter<Portfolio, PortfolioViewHolder>(
                Portfolio.class,
                R.layout.single_active_list_square,
                PortfolioViewHolder.class,
                mDatabaseRef.child(Constants.KEY_USERS+"/"+uid+"/portfolio")

        ) {
            @Override
            protected void populateViewHolder(final PortfolioViewHolder viewHolder,final Portfolio model, int position) {
                mLinearLayout.setVisibility(LinearLayout.INVISIBLE);
                if (model.getPhoto() != null)
//                    new DownLoadImageTask(viewHolder.photoBy).execute(model.getPhoto());
                    Picasso.with(getActivity().getApplicationContext())
                            .load(Uri.parse(model.getPhoto()))
//                            .resize(50, 50)
//                            .centerCrop()
                            .into(viewHolder.photoBy);
                final String idConnect = this.getRef(position).getKey();
                viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(getActivity(),DetailPdportfolioActivity.class);
                        viewHolder.photoBy.buildDrawingCache();
                        Bitmap bm= viewHolder.photoBy.getDrawingCache();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                        byte[] b = baos.toByteArray();

                        i.putExtra(Constants.DETAIL_PHOTO,b);
                        i.putExtra(Constants.DETAIL_BY,"Portfolio");
                        i.putExtra(Constants.DETAIL_DESC,model.getName());
                        startActivity(i);
                    }
                });
                viewHolder.mView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Delete")
                                .setMessage("Anda yakin ?")
                                .setPositiveButton("iya", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        mStorageRef = FirebaseStorage.getInstance().getReference(Constants.KEY_USERS+"/"+uid+"/portfolio/"+idConnect);

                                        Log.d("ref",": "+Constants.KEY_USERS+"/"+uid+"/portfolio/"+idConnect);
                                        mStorageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                DatabaseReference portDatabase = FirebaseDatabase.getInstance().getReference(Constants.KEY_USERS+"/"+uid+"/portfolio/"+idConnect);

                                                portDatabase.removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {

                                                        Toast.makeText(getActivity(), "Success",
                                                                Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });
                                    }
                                })
                                .setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                })


                                .show();
                        return true;
                    }
                });

            }
        };
        final StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(3, StaggeredGridLayoutManager.VERTICAL);
        mPortRecyclerView.setLayoutManager(layoutManager);
        mPortRecyclerView.setHasFixedSize(true);
        mPortRecyclerView.setAdapter(mFirebaseAdapter);

        return rootView;
    }

    @Override
    public void onStop() {
        super.onStop();
        mFirebaseAdapter.cleanup();
    }

    public static class PortfolioViewHolder extends RecyclerView.ViewHolder {
        public ImageView photoBy;
        public View mView;

        public PortfolioViewHolder(View v) {
            super(v);
            mView = v;
            photoBy = (ImageView) v.findViewById(R.id.ivPhoto);

        }

    }
}
