package com.v1.firebase.scholarplus.ui.scholarlist;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.v1.firebase.scholarplus.model.Scholarship;
import com.v1.firebase.scholarplus.utils.Constants;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;

/**
 * Created by MacBook on 9/4/16.
 */
public class ActionSortAdapter extends SortAdapter<Scholarship, ScholarListFragment.ScholarshipViewHolder> {
    private ProgressBar mProgressBar;
    private Activity mActivity;
    public ActionSortAdapter(Activity activity, Class<Scholarship> modelClass, int modelLayout, Class<ScholarListFragment.ScholarshipViewHolder> viewHolderClass, Query ref, ProgressBar mProgressBar,String sort) {
        super(modelClass, modelLayout, viewHolderClass, ref,sort);
        this.mProgressBar = mProgressBar;
        this.mActivity = activity;

    }

    @Override
    protected void populateViewHolder(final ScholarListFragment.ScholarshipViewHolder viewHolder,final Scholarship model, int position) {
        mProgressBar.setVisibility(ProgressBar.INVISIBLE);
        viewHolder.createdByUser.setText(model.getNama());
        viewHolder.ipk.setText("IPK "+model.getKuantitatif().get(Constants.FIREBASE_PROPERTY_IPK).toString());
        viewHolder.website.setText(model.getInstansi());
        try{
            String duedate = Constants.getMonth(model.getDuedate());
            viewHolder.duedate.setText(duedate);
        }
        catch(ParseException e){
            viewHolder.duedate.setText("-");
            Log.d("AAA",e.getMessage());

        }

        if (model.getPhotopath() != null)
//        new DownLoadImageTask(viewHolder.photoBy).execute(model.getPhotopath());
            Picasso.with(this.mActivity)
                    .load(model.getPhotopath())
//                    .resize(50, 50)
//                    .centerCrop()
                    .into(viewHolder.photoBy);

        final String itemId = this.getRef(position).getKey();
        viewHolder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mActivity,ScholarListDetailActivity.class);
                viewHolder.photoBy.buildDrawingCache();
                Bitmap bm= viewHolder.photoBy.getDrawingCache();
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                bm.compress(Bitmap.CompressFormat.PNG, 50, baos);
                byte[] b = baos.toByteArray();

                i.putExtra(Constants.FIREBASE_PROPERTY_BEASISWA, model);
                i.putExtra(Constants.DETAIL_PHOTO,b);
                i.putExtra(Constants.DETAIL_BG_PHOTO,model.getBgphotopath());
                i.putExtra(Constants.KEY_ID,itemId);
                mActivity.startActivity(i);
            }
        });

    }
}

