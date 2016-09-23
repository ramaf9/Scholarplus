package com.v1.firebase.scholarplus.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.CoordinatorLayout;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.v1.firebase.scholarplus.R;
import com.v1.firebase.scholarplus.model.Scholarship;
import com.v1.firebase.scholarplus.ui.scholarlist.ScholarListDetailActivity;
import com.v1.firebase.scholarplus.utils.Constants;

import java.io.ByteArrayOutputStream;

/**
 * Created by MacBook on 9/23/16.
 */

public class RecAdapter extends RecoAdapter<Scholarship, HomeFragment.RecViewHolder> {
    private ProgressBar mProgressBar;
    private Activity mActivity;
    private Double sort;
    private CoordinatorLayout cl_recommended;
    public RecAdapter(Activity activity, Class<Scholarship> modelClass, int modelLayout, Class<HomeFragment.RecViewHolder> viewHolderClass, Query ref, ProgressBar mProgressBar, String sort) {
        super(modelClass, modelLayout, viewHolderClass, ref,sort);
        this.mProgressBar = mProgressBar;
        this.mActivity = activity;
        this.sort = Double.valueOf(sort);
        cl_recommended = (CoordinatorLayout) mActivity.findViewById(R.id.card_view_recommended);

    }

    @Override
    protected void populateViewHolder(final HomeFragment.RecViewHolder viewHolder, final Scholarship model, int position) {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        String v = model.getKuantitatif().get(Constants.FIREBASE_PROPERTY_SEMESTER).toString();


            cl_recommended.setVisibility(View.VISIBLE);
            viewHolder.createdByUser.setText(model.getInstansi());
            if (model.getPhotopath() != null)
//                                        new DownLoadImageTask(viewHolder.photoBy).execute(scholarship.getPhotopath());
                Picasso.with(mActivity.getApplicationContext())
                        .load(model.getPhotopath())
//                                            .resize(50, 50)
//                                            .centerCrop()
                        .into(viewHolder.photoBy);

            final String itemId = this.getRef(position).getKey();

            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(mActivity,ScholarListDetailActivity.class);
                    viewHolder.photoBy.buildDrawingCache();
                    Bitmap bm= viewHolder.photoBy.getDrawingCache();
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bm.compress(Bitmap.CompressFormat.PNG, 10, baos);
                    byte[] b = baos.toByteArray();

                    i.putExtra(Constants.FIREBASE_PROPERTY_BEASISWA, model);

                    i.putExtra(Constants.DETAIL_BG_PHOTO,model.getBgphotopath());

                    i.putExtra(Constants.DETAIL_PHOTO,b);
                    i.putExtra(Constants.KEY_ID,itemId);
                    mActivity.startActivity(i);


                }
            });



    }


}


